package eu.bradan.purebasic.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicDeclareModuleBlockPresentation implements ItemPresentation {
    private final PureBasicDeclareModuleBlock element;

    public PureBasicDeclareModuleBlockPresentation(PsiElement element) {
        this.element = (PureBasicDeclareModuleBlock) element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final PureBasicParseIdentifier parseIdentifier = element.getDeclareModuleHead().getParseIdentifier();
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
        return AllIcons.Nodes.Module; // TODO: replace by own icon
    }
}
