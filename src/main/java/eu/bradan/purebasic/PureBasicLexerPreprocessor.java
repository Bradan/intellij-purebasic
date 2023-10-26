/*
 * Copyright (c) 2023 Daniel Brall
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.bradan.purebasic;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import eu.bradan.purebasic.preprocessor.PureBasicMacro;
import eu.bradan.purebasic.preprocessor.PureBasicPreprocessorStorage;
import eu.bradan.purebasic.psi.PureBasicTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * This class is a wrapper around the generated lexer class to expand preprocessor directives.
 */
public class PureBasicLexerPreprocessor implements FlexLexer {
    public static final int PREP_STATE_START = PureBasicLexer.END_OF_STATES;
    public static final int MACRO_HEAD_NAME = PREP_STATE_START;
    public static final int MACRO_HEAD_ARGS = MACRO_HEAD_NAME << 1;
    public static final int MACRO_BODY = MACRO_HEAD_ARGS << 1;
    private final PureBasicLexer lexer;
    private final Queue<LexerToken> lexerTokens = new LinkedList<>();
    private final PsiElement element;
    private final LinkedList<LexerToken> macroArgs = new LinkedList<>();
    private final LinkedList<LexerToken> macroBody = new LinkedList<>();
    private LexerToken lastToken = null;
    private int preprocessorState = 0;
    private String macroName;

    public PureBasicLexerPreprocessor(java.io.Reader in, PsiElement element) {
        lexer = new PureBasicLexer(in);
        this.element = element;
    }

    @NotNull
    private static LinkedList<PureBasicMacro.Argument> parseArguments(LinkedList<LexerToken> tokens) {
        final var arguments = new LinkedList<PureBasicMacro.Argument>();
        int level = 0;
        StringBuilder argumentName = new StringBuilder();
        StringBuilder argumentDefaultValue = null;
        for (var t : tokens) {
            if (t.getTokenType() == PureBasicTypes.OP_COMMA && level == 1) {
                final var name = argumentName.toString().trim();
                if (!name.isEmpty()) {
                    final var defValue = argumentDefaultValue == null ? null : argumentDefaultValue.toString();
                    arguments.add(new PureBasicMacro.Argument(name, defValue));
                }
                // next argument
                argumentName.setLength(0);
                argumentDefaultValue = null;
                continue;
            } else if (t.getTokenType() == PureBasicTypes.OP_PARENCLOSE) {
                final var name = argumentName.toString().trim();
                if (!name.isEmpty()) {
                    final var defValue = argumentDefaultValue == null ? null : argumentDefaultValue.toString();
                    arguments.add(new PureBasicMacro.Argument(name, defValue));
                    break;
                }
                level--;
            } else if (t.getTokenType() == PureBasicTypes.OP_EQ && level == 1) {
                argumentDefaultValue = new StringBuilder(); // default value begins here
                continue;
            }
            if (level >= 1) {
                Objects.requireNonNullElse(argumentDefaultValue, argumentName).append(t.getTokenText());
            }
            if (t.getTokenType() == PureBasicTypes.OP_PARENOPEN) {
                level++;
            }
        }
        return arguments;
    }

    @Override
    public void yybegin(int state) {
        lexer.yybegin(state);
        lexerTokens.clear();
    }

    @Override
    public int yystate() {
        if (lastToken == null) {
            return lexer.yystate() | preprocessorState;
        }
        return lastToken.getState() | preprocessorState;
    }

    @Override
    public int getTokenStart() {
        if (lastToken == null) {
            return lexer.getTokenStart();
        }
        return lastToken.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        if (lastToken == null) {
            return lexer.getTokenEnd();
        }
        return lastToken.getTokenEnd();
    }

    private void gatherTokensWhile(LinkedList<LexerToken> tokens, Predicate<LexerToken> predicate, boolean enqueue) throws IOException {
        LexerToken token;
        do {
            token = new LexerToken();
            tokens.add(token);

            if (enqueue) {
                lexerTokens.add(token);
            }
        } while (predicate.test(token));
    }

    @Override
    public IElementType advance() throws IOException {
        if (!lexerTokens.isEmpty()) {
            lastToken = lexerTokens.poll();
            return lastToken.getTokenType();
        }

        LexerToken token = new LexerToken();
        try {
            if (preprocessorState == MACRO_HEAD_ARGS) {
                macroArgs.add(token);
            }

            if (token.getTokenType() == PureBasicTypes.IDENTIFIER) {
                // check if this is a macro
                var identifier = lexer.yytext().toString().replaceAll("\\s+", "");
                if (preprocessorState == MACRO_HEAD_NAME) {
                    macroName = identifier;
                    preprocessorState = MACRO_HEAD_ARGS;
                } else if (element != null) {
                    final var macro = PureBasicPreprocessorStorage.getInstance(element.getProject()).getMacro(identifier);

                    if (macro != null) {
                        // expand the macro
                        final var macroCode = macro.getObject().getCode();
                        final var macroLexer = new PureBasicLexerPreprocessor(null, null);
                        macroLexer.reset(macroCode, 0, macroCode.length(), 0);
                        final var state = yystate();
                        while (macroLexer.getTokenEnd() < macroCode.length()) {
                            final var elementType = macroLexer.advance();
                            final var text = macroLexer.lexer.yytext().toString();
                            lexerTokens.add(new LexerToken(elementType,
                                    token.getTokenEnd(),
                                    token.getTokenEnd(),
                                    text, state));
                            macroLexer.advance();
                        }
                        return TokenType.WHITE_SPACE;
                    }
                }
            } else if (token.getTokenType() == PureBasicTypes.KEYWORD_MACRO) {
                preprocessorState = MACRO_HEAD_NAME;
            } else if (token.getTokenType() == PureBasicTypes.SEPARATOR) {
                if (preprocessorState == MACRO_HEAD_ARGS) {
                    preprocessorState = MACRO_BODY;
                }
            } else if (token.getTokenType() == PureBasicTypes.KEYWORD_ENDMACRO) {
                preprocessorState = 0;
                finalizeMacro();
            }

            if (preprocessorState == MACRO_BODY) {
                macroBody.add(token);
            }

            if (preprocessorState == MACRO_BODY && token.getTokenType() != PureBasicTypes.SEPARATOR) {
                return TokenType.WHITE_SPACE;
            }
        } finally {
            lastToken = token;
        }
        return token.getTokenType();
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        lexerTokens.clear();

        macroName = null;
        macroArgs.clear();
        macroBody.clear();

        final int stateMaskLexer = PureBasicLexer.END_OF_STATES - 1;
        final int stateMaskPrep = ~stateMaskLexer;

        preprocessorState = initialState & stateMaskPrep;

        lexer.reset(buf, start, end, initialState & stateMaskLexer);
    }

    /**
     * Removes all whitespace tokens and the hash operator from the given list of tokens.
     * This is useful for macro expansion, as you can concatenate the argument of a macro
     * with e.g. a constant name, but we don't need the # then anymore:
     *
     * <pre><code>
     * Macro CompConst(name)
     * #PB_Compiler_#name
     * EndMacro
     * </code></pre>
     *
     * @param tokens the list of tokens to remove the whitespace and hash operator from.
     */
    private void removeOpHashAndWhitespaces(LinkedList<LexerToken> tokens) {
        var toRemove = new LinkedList<LexerToken>();

        final var currentRegion = new LinkedList<LexerToken>();
        var removing = false;
        for (var t : tokens) {
            if (t == null) continue;
            if (t.tokenType == TokenType.WHITE_SPACE) {
                currentRegion.add(t);
            } else if (t.tokenType == PureBasicTypes.OP_HASH) {
                currentRegion.add(t);
                removing = true;
            } else {
                if (removing) {
                    toRemove.addAll(currentRegion);
                    currentRegion.clear();
                    removing = false;
                }
            }
        }

        tokens.removeAll(toRemove);
    }

    private void finalizeMacro() {
        if (macroName != null && !macroName.isEmpty() && element != null) {
            final var arguments = parseArguments(macroArgs);

            trimWhitespacesAndSeparators(macroBody);
            removeOpHashAndWhitespaces(macroBody);

            final var macroBodyCode = new StringBuilder();
            for (var t : macroBody) {
                var tokenText = t.getTokenText().toString();
                if (t.getTokenType() == PureBasicTypes.IDENTIFIER && arguments.stream().anyMatch(a -> a.getName().equals(tokenText))) {
                    macroBodyCode.append("${").append(tokenText).append("}");
                } else {
                    macroBodyCode.append(tokenText.replaceAll("\\$\\{", "\\$\\${"));
                }
            }

            final var macro = new PureBasicMacro(macroName,
                    macroBodyCode.toString(),
                    arguments.toArray(new PureBasicMacro.Argument[0]));

            PureBasicPreprocessorStorage.getInstance(element.getProject())
                    .addMacro(element.getContainingFile(), macro);
        }

        macroName = null;
        macroArgs.clear();
        macroBody.clear();
    }

    private void trimWhitespacesAndSeparators(LinkedList<LexerToken> macroBody) {
        // remove beginning whitespaces from the macro body
        var toRemove = new LinkedList<LexerToken>();
        for (var t : macroBody) {
            if (t.tokenType == TokenType.WHITE_SPACE || t.tokenType == PureBasicTypes.SEPARATOR) {
                toRemove.add(t);
            } else {
                break;
            }
        }
        // remove ending whitespaces from the macro body
        var iter = macroBody.descendingIterator();
        while (iter.hasNext()) {
            var t = iter.next();
            if (t.tokenType == TokenType.WHITE_SPACE || t.tokenType == PureBasicTypes.SEPARATOR) {
                toRemove.add(t);
            } else {
                break;
            }
        }
        macroBody.removeAll(toRemove);
    }

    // TODO: delete
    private void handleMacroDefinition() throws IOException {
        // Macro whitespace* identifier whitespace* parameters? (newline|sep)
        // contents
        // EndMacro

        // gather the identifier
        var tokens = new LinkedList<LexerToken>();
        try {
            String macroName = null;

            gatherTokensWhile(tokens, t -> t.getTokenType() == TokenType.WHITE_SPACE, true);
            if (tokens.isEmpty()) {
                throw new IOException("End of stream.");
            }
            if (tokens.getLast().getTokenType() == PureBasicTypes.IDENTIFIER) {
                macroName = tokens.getLast().getTokenText().toString();
            }

            // gather the parameters all at once
            tokens.clear();
            gatherTokensWhile(tokens, t -> t.getTokenType() != PureBasicTypes.SEPARATOR, true);
            if (tokens.isEmpty()) {
                throw new IOException("End of stream.");
            }
            tokens.removeLast(); // remove the separator

            final var arguments = parseArguments(tokens);

            // macro body
            tokens.clear();
            gatherTokensWhile(tokens, t -> t.getTokenType() != PureBasicTypes.KEYWORD_ENDMACRO, false);
            var endMacro = tokens.removeLast(); // remove the ENDMACRO token
            for (var t : tokens) {
                lexerTokens.add(new LexerToken(t, TokenType.WHITE_SPACE));
            }

            var macroBody = new StringBuilder();
            final var argNames = new HashSet<>(arguments.stream().map(PureBasicMacro.Argument::getName).toList());
            removeOpHashAndWhitespaces(tokens);
            for (var t : tokens) {
                if (t.getTokenType() == PureBasicTypes.IDENTIFIER && argNames.contains(t.getTokenText().toString())) {
                    macroBody.append(t.getTokenText());
                } else {
                    macroBody.append(t.getTokenText());
                }
            }

            if (endMacro.getTokenType() == PureBasicTypes.KEYWORD_ENDMACRO) {
                lexerTokens.add(endMacro);
            } else {
                lexerTokens.add(new LexerToken(endMacro, TokenType.WHITE_SPACE));
            }

            var macro = new PureBasicMacro(macroName, macroBody.toString(), arguments.toArray(new PureBasicMacro.Argument[0]));

            // TODO: register the macro somewhere

            if (tokens.isEmpty()) {
                throw new IOException("End of stream.");
            }
        } catch (IOException e) {
            if (lexerTokens.isEmpty()) {
                throw e;
            }
        }
    }

    private class LexerToken {
        private final int tokenStart;
        private final int tokenEnd;
        private final IElementType tokenType;
        private final CharSequence tokenText;
        private final int state;

        /**
         * Copy constructor changing the tokenStart and tokenEnd values.
         *
         * @param copyFrom  the token to copy from
         * @param tokenType the new tokenType value
         */
        public LexerToken(LexerToken copyFrom, IElementType tokenType) {
            this.tokenType = tokenType;
            this.tokenStart = copyFrom.getTokenStart();
            this.tokenEnd = copyFrom.getTokenEnd();
            this.tokenText = copyFrom.getTokenText();
            this.state = copyFrom.getState();
        }

        /**
         * Constructor for a completely new LexerToken.
         *
         * @param tokenType  the tokenType value
         * @param tokenStart the tokenStart value
         * @param tokenEnd   the tokenEnd value
         * @param tokenText  the tokenText value
         * @param state      the state value
         */
        public LexerToken(IElementType tokenType, int tokenStart, int tokenEnd, String tokenText, int state) {
            this.tokenType = tokenType;
            this.tokenStart = tokenStart;
            this.tokenEnd = tokenEnd;
            this.tokenText = tokenText;
            this.state = state;
        }

        public LexerToken() throws IOException {
            this.tokenType = lexer.advance();
            this.tokenStart = lexer.getTokenStart();
            this.tokenEnd = lexer.getTokenEnd();
            this.tokenText = lexer.yytext();
            this.state = lexer.yystate();
        }

        public int getTokenStart() {
            return tokenStart;
        }

        public int getTokenEnd() {
            return tokenEnd;
        }

        public IElementType getTokenType() {
            return tokenType;
        }

        public CharSequence getTokenText() {
            return tokenText;
        }

        public int getState() {
            return state;
        }
    }
}
