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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import eu.bradan.purebasic.psi.PureBasicElementFactory;
import eu.bradan.purebasic.psi.PureBasicNamedElement;
import eu.bradan.purebasic.psi.PureBasicTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * By default, we will take the first identifier.
 */
public abstract class PureBasicNamedElementImpl extends ASTWrapperPsiElement implements PureBasicNamedElement {
    private static final TokenSet identifiers = TokenSet.create(
            PureBasicTypes.IDENTIFIER,
            PureBasicTypes.CONSTANT_IDENTIFIER,
            PureBasicTypes.POINTER_IDENTIFIER,
            PureBasicTypes.LABEL_IDENTIFIER
    );

    public PureBasicNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        final ASTNode keyNode = getNode().findChildByType(identifiers);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        final ASTNode node = getNode().findChildByType(identifiers);
        if (node != null) {
            final PsiElement element = PureBasicElementFactory.createIdentifier(getProject(), name);
            getNode().replaceChild(node, element.getNode());
        }
        return this;
    }

    @Nullable
    @Override
    public String getName() {
        final PsiElement nameElement = getNameIdentifier();
        if (nameElement != null) {
            return nameElement.getText();
        }
        return null;
    }
}
