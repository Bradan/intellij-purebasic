package eu.bradan.purebasic.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicMacroBlockPresentation implements ItemPresentation {
    private final PureBasicMacroBlock element;

    public PureBasicMacroBlockPresentation(PsiElement element) {
        this.element = (PureBasicMacroBlock) element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final PureBasicParseIdentifier parseIdentifier = element.getMacroHead().getParseIdentifier();
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
        return AllIcons.Nodes.Method; // TODO: replace by own icon
    }
}
