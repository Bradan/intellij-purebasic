/*
 * Copyright (c) 2020 Daniel Brall
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

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import eu.bradan.purebasic.psi.PureBasicTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class PureBasicHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("PUREBASIC_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("PUREBASIC_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("PUREBASIC_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey CHARACTER =
            createTextAttributesKey("PUREBASIC_CHARACTER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey CONSTANT =
            createTextAttributesKey("PUREBASIC_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("PUREBASIC_IDENTIFIER", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
    public static final TextAttributesKey INLINE_ASM =
            createTextAttributesKey("PUREBASIC_ASM", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("PUREBASIC_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("PUREBASIC_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("PUREBASIC_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] CHARACTER_KEYS = new TextAttributesKey[]{CHARACTER};
    private static final TextAttributesKey[] CONSTANT_KEYS = new TextAttributesKey[]{CONSTANT};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] INLINE_ASM_KEYS = new TextAttributesKey[]{INLINE_ASM};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new PureBasicLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(PureBasicTypes.KEYWORD)) {
            return KEYWORD_KEYS;
        } else if (tokenType.equals(PureBasicTypes.OPERATOR) || tokenType.equals(PureBasicTypes.SEPARATOR)) {
            return OPERATOR_KEYS;
        } else if (tokenType.equals(PureBasicTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(PureBasicTypes.CHARACTER)) {
            return CHARACTER_KEYS;
        } else if (tokenType.equals(PureBasicTypes.CONSTANT)) {
            return CONSTANT_KEYS;
        } else if (tokenType.equals(PureBasicTypes.IDENTIFIER)
                || tokenType.equals(PureBasicTypes.LABEL_IDENTIFIER)
                || tokenType.equals(PureBasicTypes.POINTER_IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else if (tokenType.equals(PureBasicTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(PureBasicTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(PureBasicTypes.INLINE_ASM)) {
            return INLINE_ASM_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
