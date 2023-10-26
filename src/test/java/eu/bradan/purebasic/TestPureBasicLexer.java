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

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;

public class TestPureBasicLexer extends TestCase {
    @SuppressWarnings("RedundantThrows")
    @Override
    protected void setUp() throws Exception {
        //PureBasicLexer.main(new String[] {"src/test/testData/ParsingTestData.pb"});
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    protected void tearDown() throws Exception {
    }

    @SuppressWarnings("UnusedReturnValue")
    private IElementType assertToken(@NotNull FlexLexer lexer, @NotNull String code, String type, String codePart) {
        IElementType elementType = null;
        try {
            elementType = lexer.advance();
            String token = code.substring(lexer.getTokenStart(), lexer.getTokenEnd());
            if (codePart != null) {
                assertEquals(codePart, token);
            }
            if (type != null) {
                assertEquals(type, elementType.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elementType;
    }

    public void testGlobalVariables() {
        String code = "Global a.i, b.i";

        PureBasicLexer lexer = new PureBasicLexer(new StringReader(code));
        lexer.reset(code, 0, code.length(), PureBasicLexer.YYINITIAL);

        assertToken(lexer, code, "PureBasicTokenType.KEYWORD_GLOBAL", "Global");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", " ");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "a");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_DOT", ".");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "i");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_COMMA", ",");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", " ");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "b");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_DOT", ".");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "i");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());
    }

    public void testProcedure() {
        String code = "Procedure test(a.i)\n  ProcedureReturn a+1\nEndProcedure";

        PureBasicLexer lexer = new PureBasicLexer(new StringReader(code));
        lexer.reset(code, 0, code.length(), PureBasicLexer.YYINITIAL);

        assertToken(lexer, code, "PureBasicTokenType.KEYWORD_PROCEDURE", "Procedure");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", " ");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "test");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_PARENOPEN", "(");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "a");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_DOT", ".");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "i");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_PARENCLOSE", ")");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());


        assertToken(lexer, code, "PureBasicTokenType.SEPARATOR", "\n");
        assertEquals(PureBasicLexer.YYINITIAL, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", "  ");
        assertEquals(PureBasicLexer.YYINITIAL, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.KEYWORD_PROCEDURERETURN", "ProcedureReturn");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", " ");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "a");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_PLUS", "+");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.NUMBER", "1");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());


        assertToken(lexer, code, "PureBasicTokenType.SEPARATOR", "\n");
        assertEquals(PureBasicLexer.YYINITIAL, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.KEYWORD_ENDPROCEDURE", "EndProcedure");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());
    }

    public void testPreprocessor() {
        String code = "Macro test(a, b=1)\n  a+b\nEndMacro";

        PureBasicLexerPreprocessor lexer = new PureBasicLexerPreprocessor(new StringReader(code), null);
        lexer.reset(code, 0, code.length(), PureBasicLexer.YYINITIAL);

        assertToken(lexer, code, "PureBasicTokenType.KEYWORD_MACRO", "Macro");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_NAME, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", " ");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_NAME, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "test");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_PARENOPEN", "(");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "a");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_COMMA", ",");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", " ");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.IDENTIFIER", "b");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_EQ", "=");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.NUMBER", "1");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.OP_PARENCLOSE", ")");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_HEAD_ARGS, lexer.yystate());


        assertToken(lexer, code, "PureBasicTokenType.SEPARATOR", "\n");
        assertEquals(PureBasicLexer.YYINITIAL | PureBasicLexerPreprocessor.MACRO_BODY, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", "  ");
        assertEquals(PureBasicLexer.YYINITIAL | PureBasicLexerPreprocessor.MACRO_BODY, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", "a");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_BODY, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", "+");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_BODY, lexer.yystate());

        assertToken(lexer, code, "WHITE_SPACE", "b");
        assertEquals(PureBasicLexer.FOLLOW_UP | PureBasicLexerPreprocessor.MACRO_BODY, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.SEPARATOR", "\n");
        assertEquals(PureBasicLexer.YYINITIAL | PureBasicLexerPreprocessor.MACRO_BODY, lexer.yystate());

        assertToken(lexer, code, "PureBasicTokenType.KEYWORD_ENDMACRO", "EndMacro");
        assertEquals(PureBasicLexer.FOLLOW_UP, lexer.yystate());
    }
}
