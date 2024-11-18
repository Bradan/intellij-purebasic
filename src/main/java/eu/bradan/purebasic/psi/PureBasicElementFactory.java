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

package eu.bradan.purebasic.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import eu.bradan.purebasic.PureBasicFileType;
import org.jetbrains.annotations.NotNull;

/**
 * This is some kind of dirty hack. The documentation says there is no easy method to create PsiElements without
 * creating a pseudo file instance containing it:
 * <p>
 * <a href="https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/modifying_psi.html?search=psi#creating-the-new-psi">...</a>
 */
public class PureBasicElementFactory {
    @NotNull
    public static PureBasicFile parseString(Project project, String text) {
        return (PureBasicFile) PsiFileFactory.getInstance(project).
                createFileFromText("PureBasic", PureBasicFileType.INSTANCE, text);
    }

    public static PureBasicExpression parseCondition(Project project, String expression) {
        var file = parseString(project, "#__cond__ = (" + expression + ")");
        var assignment = file.getFirstChild();
        if (assignment != null) {
            return (PureBasicExpression) assignment.getLastChild();
        }
        return null;
    }

    public static PureBasicExpression parseExpression(Project project, String expression) {
        return (PureBasicExpression) parseString(project, "Debug " + expression)
                .getFirstChild() // Debug Statement
                .getLastChild(); // Expression
    }

    public static PsiElement createIdentifier(Project project, String identifier) {
        return parseString(project, identifier + " = 1").getFirstChild();
    }

    public static ASTNode createString(Project project, @NotNull String string) {
        return parseString(project, "Debug ~\"" + string.replaceAll("\"", "\\\"") + "\"")
                .getFirstChild() // Debug Statement
                .getLastChild() // Expression
                .getNode()
                .findChildByType(PureBasicTypes.STRING);
    }

    public static PsiElement replaceSubstring(Project project, @NotNull PsiElement originalElement, @NotNull TextRange range, String newText) {
        String text = originalElement.getText();
        text = text.substring(0, range.getStartOffset()) + newText + text.substring(range.getEndOffset());
        return parseString(project, text).getFirstChild();
    }
}
