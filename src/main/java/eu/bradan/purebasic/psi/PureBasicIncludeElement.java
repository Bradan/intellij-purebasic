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
import eu.bradan.purebasic.psi.impl.PureBasicIncludePathStmtImpl;
import eu.bradan.purebasic.psi.impl.PureBasicSimpleStatementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

public class PureBasicIncludeElement extends PureBasicSimpleStatementImpl {
    public PureBasicIncludeElement(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    private static PureBasicIncludePathStmtImpl findIncludePathStatement(@NotNull PsiElement element) {
        while (element != null && !(element instanceof PureBasicIncludePathStmt)) {
            PsiElement previous = element.getPrevSibling();
            if (previous == null) {
                previous = element.getParent();
            }
            element = previous;
        }

        return (PureBasicIncludePathStmtImpl) element;
    }

    /**
     * @return The base path to which the include statement is relative to.
     */
    @NotNull
    private String getBasePath() {
        if (!(this instanceof PureBasicIncludePathStmt)) {
            PureBasicIncludePathStmtImpl includePathStatement = findIncludePathStatement(this);
            if (includePathStatement != null) {
                return includePathStatement.getFilename();
            }
        }
        return getContainingFile().getVirtualFile().getParent().getPath();
    }

    @NotNull
    public String getFilename() {
        final String basePath = getBasePath();

        final ASTNode string = getNode().findChildByType(PureBasicTypes.STRING);
        String filename = string != null ? PureBasicUtil.getStringContents(string.getText()) : null;

        if ("".equals(filename)) {
            filename = null;
        }

        File file = filename != null ? new File(basePath, filename) : new File(basePath);
        return file.getPath();
    }

    @Nullable
    public VirtualFile getVirtualFile() {
        String filename = getFilename();
        if (!"".equals(filename)) {
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

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        final String basePath = getBasePath();

        final ASTNode string = getNode().findChildByType(PureBasicTypes.STRING);
        if (string == null) {
            return new PsiReference[]{};
        }
        String filenameString = string.getText();
        String filename = PureBasicUtil.getStringContents(filenameString);

        int offset = string.getStartOffsetInParent() + 1; // a quote symbol is always there
        if (filenameString.startsWith("~")) {
            offset++; // skip the tilde as well
        }

        ArrayList<PsiReference> references = new ArrayList<>();
        final String[] pathParts = filename.split("[\\\\/]");
        File current = new File(basePath);
        for (String pathPart : pathParts) {
            current = new File(current, pathPart);
            VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(current);
            PsiElement resolvedElement = null;
            if (file != null) {
                resolvedElement = PsiManager.getInstance(getProject()).findFile(file);
                if (resolvedElement == null) {
                    resolvedElement = PsiManager.getInstance(getProject()).findDirectory(file);
                }
            }
            references.add(new PureBasicIncludeReference(
                    this,
                    TextRange.create(offset, offset + pathPart.length()),
                    resolvedElement));
            offset += pathPart.length() + 1; // + 1 for the separator
        }

        return references.toArray(PsiReference[]::new);
    }

    @Override
    public PsiReference getReference() {
        PsiReference[] references = getReferences();
        if (references.length > 0) {
            return references[references.length - 1];
        }
        return null;
    }
}
