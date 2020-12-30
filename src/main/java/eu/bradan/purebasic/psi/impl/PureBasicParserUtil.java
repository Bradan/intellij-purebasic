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

package eu.bradan.purebasic.psi.impl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

public class PureBasicParserUtil extends GeneratedParserUtilBase {
    @SuppressWarnings("SameReturnValue")
    public static boolean parseAlwaysTrueStatement(PsiBuilder builder, int level) {
        return true;
    }

    public static boolean acceptUntil(PsiBuilder b, int l, Parser token) {
        if (!recursion_guard_(b, l, "parseWhileNotStatement")) return false;
        PsiBuilder.Marker m = enter_section_(b);
        boolean r = false;
        while (!b.eof()) {
            if (token.parse(b, l + 1)) {
                r = true;
                break;
            }
            b.advanceLexer();
        }
        exit_section_(b, m, null, r);
        report_error_(b, r);
        return r;
    }

    public static boolean acceptUntil(PsiBuilder b, int l, Parser tokenOpen, Parser tokenClose) {
        if (!recursion_guard_(b, l, "parseWhileNotStatement")) return false;
        PsiBuilder.Marker m = enter_section_(b);
        boolean r = false;
        int level = 1;
        while (!b.eof()) {
            if (tokenOpen.parse(b, l + 1)) {
                level++;
            }
            if (tokenClose.parse(b, l + 1)) {
                level--;
                if (level <= 0) {
                    r = true;
                    break;
                }
            }
            b.advanceLexer();
        }
        exit_section_(b, m, null, r);
        report_error_(b, r);
        return r;
    }
}
