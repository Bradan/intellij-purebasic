package eu.bradan.purebasic.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicProcedureBlockPresentation implements ItemPresentation {
    private final PureBasicProcedureBlock element;

    public PureBasicProcedureBlockPresentation(PsiElement element) {
        this.element = (PureBasicProcedureBlock) element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final PureBasicParseIdentifier parseIdentifier = element.getProcedureHead().getParseIdentifier();
        return parseIdentifier != null ? parseIdentifier.getText() : null;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return element.getContainingFile().getName();
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return AllIcons.Nodes.Function; // TODO: replace by own icon?
    }
}
