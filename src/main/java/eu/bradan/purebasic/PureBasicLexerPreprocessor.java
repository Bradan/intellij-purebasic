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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import eu.bradan.purebasic.preprocessor.PureBasicMacro;
import eu.bradan.purebasic.preprocessor.PureBasicPreprocessorStorage;
import eu.bradan.purebasic.psi.PureBasicTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class is a wrapper around the generated lexer class to handle preprocessor directives.
 */
public class PureBasicLexerPreprocessor implements FlexLexer {
    public static final int PREP_STATE_START = PureBasicLexer.END_OF_STATES;
    public static final int MACRO_HEAD_NAME = PREP_STATE_START;
    public static final int MACRO_HEAD_ARGS = MACRO_HEAD_NAME << 1;
    public static final int MACRO_BODY = MACRO_HEAD_ARGS << 1;
    public static final int POTENTIAL_MACRO_CALL = MACRO_BODY << 1;
    private static final Logger LOG = Logger.getInstance(PureBasicLexerPreprocessor.class);
    private static final int MAX_LEVEL = 20;

    private final PureBasicLexer lexer;
    private final Queue<LexerToken> lexerTokens = new LinkedList<>();
    private final PsiElement element;
    private final LinkedList<LexerToken> macroArgs = new LinkedList<>();
    private final LinkedList<LexerToken> macroBody = new LinkedList<>();
    private final int level;
    private String macroName;
    private LexerToken lastToken = null;
    private int preprocessorState = 0;

    public PureBasicLexerPreprocessor(java.io.Reader in, PsiElement element) {
        lexer = new PureBasicLexer(in);
        this.element = element;
        this.level = 0;

        LOG.debug("Creating PureBasicLexerPreprocessor for PsiElement of type "
                + (element != null ? element.getClass() + " @ " + System.identityHashCode(element) : "null"));

        if (element != null)
            PureBasicPreprocessorStorage.getInstance(element.getProject()).clearInvalid();
    }

    public PureBasicLexerPreprocessor(java.io.Reader in, PsiElement element, int level) {
        lexer = new PureBasicLexer(in);
        this.element = element;
        this.level = level;

        LOG.debug("Creating PureBasicLexerPreprocessor for PsiElement of type "
                + (element != null ? element.getClass() + " @ " + System.identityHashCode(element) : "null"));

        if (element != null)
            PureBasicPreprocessorStorage.getInstance(element.getProject()).clearInvalid();
    }

    /**
     * Copy constructor (the level is incremented)!
     *
     * @param copyFrom the object to copy
     */
    public PureBasicLexerPreprocessor(PureBasicLexerPreprocessor copyFrom) {
        lexer = new PureBasicLexer(copyFrom.lexer);
        this.lexerTokens.addAll(copyFrom.lexerTokens);
        this.element = copyFrom.element;
        this.macroArgs.addAll(copyFrom.macroArgs);
        this.macroBody.addAll(copyFrom.macroBody);
        this.macroName = copyFrom.macroName;
        this.lastToken = copyFrom.lastToken;
        this.preprocessorState = copyFrom.preprocessorState;
        this.level = copyFrom.level + 1;
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

    @Override
    public IElementType advance() throws IOException {
        LexerToken token = lexerTokens.isEmpty() ? nextToken() : lexerTokens.poll();
        if (token == null) {
            return null;
        }

        try {
            if (preprocessorState == MACRO_HEAD_ARGS) {
                macroArgs.add(token);
            }

            if (token.getTokenType() != PureBasicTypes.IDENTIFIER
                    && token.getTokenType() != PureBasicTypes.OP_MODULE) {
                preprocessorState &= ~POTENTIAL_MACRO_CALL;
            }

            if (token.getTokenType() == PureBasicTypes.IDENTIFIER) {
                // check if this is a macro
                var identifier = token.getTokenText().toString().replaceAll("\\s+", "");
                if (preprocessorState == MACRO_HEAD_NAME) {
                    macroName = identifier;
                    preprocessorState = MACRO_HEAD_ARGS;
                } else if (element != null && preprocessorState == 0) {
                    preprocessorState = POTENTIAL_MACRO_CALL;
                    var replacement = expandMacro(token);
                    if (replacement != null) {
                        token = replacement;
                        return replacement.getTokenType();
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
                lastToken = new LexerToken(token, TokenType.WHITE_SPACE);
                return TokenType.WHITE_SPACE;
            }
        } finally {
            lastToken = token;
        }
        return token.getTokenType();
    }

    /**
     * Gathers the tokens of a potential function call and returns the first token which does not belong to it.
     *
     * @param alreadyReadToken The already read token of this function.
     * @param function         The function identifier.
     * @param arguments        The function arguments.
     * @throws IOException If the lexer could not read the next token.
     */
    private void gatherCallTokens(LexerToken alreadyReadToken,
                                  LinkedList<LexerToken> all_tokens,
                                  LinkedList<LexerToken> function,
                                  LinkedList<LinkedList<LexerToken>> arguments) throws IOException {
        PureBasicLexerPreprocessor lex = new PureBasicLexerPreprocessor(this);

        LexerToken token = alreadyReadToken;

        final ArrayList<IElementType> validIdentifier = new ArrayList<>(
                Arrays.asList(
                        PureBasicTypes.IDENTIFIER,
                        PureBasicTypes.OP_MODULE
                )
        );

        while (token != null && validIdentifier.contains(token.getTokenType())) {
            function.add(token);
            all_tokens.add(token);
            token = lex.nextToken();
        }

        if (token != null && token.getTokenType() == PureBasicTypes.OP_PARENOPEN) {
            all_tokens.add(token);

            LinkedList<LexerToken> currentArgument = null;
            int level = 1;
            while (level >= 1) {
                token = lex.nextToken();
                if (token == null)
                    break;
                all_tokens.add(token);

                if (level == 1 && token.getTokenType() == PureBasicTypes.OP_COMMA && currentArgument != null) {
                    arguments.add(currentArgument);
                    currentArgument = new LinkedList<>();
                    continue;
                } else if (token.getTokenType() == PureBasicTypes.OP_PARENCLOSE) {
                    if (level == 1) {
                        if (currentArgument != null)
                            arguments.add(currentArgument);
                        return;
                    }
                    level--;
                }

                if (currentArgument == null) {
                    currentArgument = new LinkedList<>();
                }
                currentArgument.add(token);
                if (token.getTokenType() == PureBasicTypes.OP_PARENOPEN) {
                    level++;
                }
            }
        }
    }

    private String tokensToString(List<LexerToken> tokens, Predicate<LexerToken> filter) {
        final var sb = new StringBuilder();
        tokens.stream().filter(t -> t != null && filter.test(t)).forEach(t -> sb.append(t.getTokenText()));
        return sb.toString();
    }

    private LexerToken expandMacro(LexerToken token) throws IOException {
        var all_tokens = new LinkedList<LexerToken>();
        var function = new LinkedList<LexerToken>();
        var arguments = new LinkedList<LinkedList<LexerToken>>();

        try {
            gatherCallTokens(token, all_tokens, function, arguments);
        } catch (IOException ignored) {
            return null;
        }

        // get the last identifier of function
        var lastIdentifier = function.stream()
                .filter(t -> t.getTokenType() == PureBasicTypes.IDENTIFIER)
                .reduce((a, b) -> b).orElseThrow();

        final var macros = PureBasicPreprocessorStorage.getInstance(element.getProject())
                .getMacros(lastIdentifier.getTokenText().toString());

        PureBasicMacro macro = null;
        PsiFile macroFile;

        for (var m : macros) {
            macroFile = m.getFile();
            if (macroFile != null) {
                macro = m.getObject();
                break;
            }
        }

        if (macro != null && this.level < MAX_LEVEL) {
            // expand the macro
            var argumentStrings = arguments.stream()
                    .map(a -> tokensToString(a, t -> true))
                    .toArray(String[]::new);

            final var endToken = all_tokens.getLast();
            final var tokenText = tokensToString(all_tokens, t -> true);

            final var start = token.getTokenStart();
            final var end = endToken.getTokenEnd();

            // consume all tokens until endToken ends.
            while (token != null && token.getTokenEnd() < endToken.getTokenEnd()) {
                token = nextToken();
            }

            final var macroCode = macro.getCode(argumentStrings);
            final var macroLexer = new PureBasicLexerPreprocessor(null, null, this.level + 1);
            macroLexer.reset(macroCode, 0, macroCode.length(), 0);
            final var state = yystate();
            IElementType elementType;
            while ((elementType = macroLexer.advance()) != null) {
                final var text = macroLexer.yytext().toString();
                lexerTokens.add(new LexerToken(elementType, end, end, text, state));
            }

            LOG.debug(String.format(Locale.getDefault(), "Expanding macro \"%s\" to \"%s\"", tokenText, macroCode));

            return new LexerToken(TokenType.WHITE_SPACE, start, end, tokenText, yystate());
        }

        return null;
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        LOG.debug("Resetting PureBasicLexerPreprocessor to " + start + " - " + end + " with state " + initialState);

        lexerTokens.clear();

        macroName = null;
        macroArgs.clear();
        macroBody.clear();

        final int stateMaskLexer = PureBasicLexer.END_OF_STATES - 1;
        final int stateMaskPrep = ~stateMaskLexer;

        preprocessorState = initialState & stateMaskPrep;

        lexer.reset(buf, start, end, initialState & stateMaskLexer);
    }

    private CharSequence yytext() {
        if (lastToken != null) {
            return lastToken.getTokenText();
        }
        return lexer.yytext();
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
                    macroBodyCode.append(tokenText.replaceAll("\\$", "\\$\\$"));
                }
            }

            final var macro = new PureBasicMacro(macroName,
                    macroBodyCode.toString(),
                    arguments.toArray(new PureBasicMacro.Argument[0]));

            LOG.debug(String.format(Locale.getDefault(), "Registering macro \"%s(%s)\" with code \"%s\"",
                    macroName,
                    String.join(", ", arguments.stream()
                            .map(a -> a.getName() + " = " + a.getDefaultValue())
                            .toArray(String[]::new)),
                    macroBodyCode));

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

    private LexerToken nextToken() throws IOException {
        final var tokenType = lexer.advance();
        if (tokenType != null) {
            final var tokenStart = lexer.getTokenStart();
            final var tokenEnd = lexer.getTokenEnd();
            final var tokenText = lexer.yytext();
            final var state = lexer.yystate();
            return new LexerToken(tokenType, tokenStart, tokenEnd, tokenText, state);
        }
        return null;
    }

    private static class LexerToken {
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
        public LexerToken(IElementType tokenType, int tokenStart, int tokenEnd, CharSequence tokenText, int state) {
            this.tokenType = tokenType;
            this.tokenStart = tokenStart;
            this.tokenEnd = tokenEnd;
            this.tokenText = tokenText;
            this.state = state;
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

        @Override
        public String toString() {
            return "LexerToken{" +
                    "tokenType=" + tokenType +
                    ", tokenText=\"" + tokenText + "\"" +
                    ", tokenStart=" + tokenStart +
                    ", tokenEnd=" + tokenEnd +
                    ", state=" + state +
                    '}';
        }
    }
}
