package eu.bradan.purebasic.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicDefineModuleBlockPresentation implements ItemPresentation {
    private final PureBasicDefineModuleBlock element;

    public PureBasicDefineModuleBlockPresentation(PsiElement element) {
        this.element = (PureBasicDefineModuleBlock) element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final PureBasicParseIdentifier parseIdentifier = element.getDefineModuleHead().getParseIdentifier();
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
