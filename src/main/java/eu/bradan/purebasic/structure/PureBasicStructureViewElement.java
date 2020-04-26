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

package eu.bradan.purebasic.structure;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import eu.bradan.purebasic.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;

public class PureBasicStructureViewElement implements StructureViewTreeElement {
    private final NavigatablePsiElement element;

    private final Class[] leafElements = new Class[]{
            PureBasicVariableDeclIdentifier.class,
            PureBasicArrayDeclaration.class,
            PureBasicListDeclaration.class,
            PureBasicMapDeclaration.class,
            PureBasicLabel.class
    };
    private final Class[] blockElements = new Class[]{
            PureBasicStructure.class,
            PureBasicInterface.class,
            PureBasicProcedure.class,
            PureBasicMacro.class,
            PureBasicFile.class
    };

    public PureBasicStructureViewElement(NavigatablePsiElement psiElement) {
        this.element = psiElement;
    }

    @Override
    public Object getValue() {
        return element.getText();
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        ItemPresentation presentation = element.getPresentation();
        return presentation != null
                ? presentation
                : new PresentationData(element.getText(), "", AllIcons.Nodes.Variable, null);
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (Arrays.asList(blockElements).contains(element.getClass())) {
            LinkedList<Class> classes = new LinkedList<>();
            classes.addAll(Arrays.asList(leafElements));
            classes.addAll(Arrays.asList(blockElements));
            // TODO: create findChildrenOfAnyType which doesn't descent found elements
            return PsiTreeUtil.findChildrenOfAnyType(element, classes.toArray(new Class[0]))
                    .stream()
                    .map(x -> new PureBasicStructureViewElement((NavigatablePsiElement) x))
                    .toArray(PureBasicStructureViewElement[]::new);
        }

        return new TreeElement[0];
    }

    @Override
    public void navigate(boolean requestFocus) {
        element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element.canNavigateToSource();
    }
}
