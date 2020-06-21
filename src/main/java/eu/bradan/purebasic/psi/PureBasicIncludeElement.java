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
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import eu.bradan.purebasic.PureBasicUtil;
import eu.bradan.purebasic.psi.impl.PureBasicSimpleStatementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PureBasicIncludeElement extends PureBasicSimpleStatementImpl {
    public PureBasicIncludeElement(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    private static String findIncludePath(@NotNull PsiElement element) {
        String defaultPath = element.getContainingFile().getVirtualFile().getParent().getCanonicalPath();

        while (element != null && !(element instanceof PureBasicIncludePathStmt)) {
            PsiElement previous = element.getPrevSibling();
            if (previous == null) {
                previous = element.getParent();
            }
            element = previous;
        }

        final String path = element != null ? ((PureBasicIncludeElement) element).getFilename() : null;
        return path != null && !"".equals(path) ? path : defaultPath;
    }

    @Nullable
    public String getFilename() {
        String path = this.getContainingFile().getVirtualFile().getParent().getCanonicalPath();
        if (!(this instanceof PureBasicIncludePathStmt)) {
            String newPath = findIncludePath(this);
            if (newPath != null && !"".equals(newPath)) {
                path = newPath;
            }
        }

        final ASTNode string = getNode().findChildByType(PureBasicTypes.STRING);
        String filename = string != null ? PureBasicUtil.getStringContents(string.getText()) : null;

        if ("".equals(filename)) {
            filename = null;
        }

        File file = null;
        if (path != null && filename != null) {
            file = new File(path, filename);
        } else if (filename != null) {
            file = new File(filename);
        } else if (path != null) {
            file = new File(path);
        }

        return file != null ? file.getPath() : null;
    }

    @Nullable
    public VirtualFile getVirtualFile() {
        String filename = getFilename();
        if (filename != null && !"".equals(filename)) {
            return LocalFileSystem.getInstance().findFileByIoFile(new File(filename));
        }

        return null;
    }

    @Nullable
    public PsiFile getPsiFile() {
        VirtualFile virtualFile = getVirtualFile();
        if (virtualFile != null) {
            return PsiManager.getInstance(getProject()).findFile(virtualFile);
        }

        return null;
    }

    @Override
    public PsiReference getReference() {
        final ASTNode node = getNode().findChildByType(PureBasicTypes.STRING);
        if (node != null) {
            final TextRange range = node.getTextRange().shiftLeft(getNode().getStartOffset());
            return new PureBasicReference(this, range, true);
        }
        return null;
    }
}
