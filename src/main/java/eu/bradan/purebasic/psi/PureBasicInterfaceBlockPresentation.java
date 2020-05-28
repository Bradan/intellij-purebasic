package eu.bradan.purebasic.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class PureBasicInterfaceBlockPresentation implements ItemPresentation {
    private final PureBasicInterfaceBlock element;

    public PureBasicInterfaceBlockPresentation(PsiElement element) {
        this.element = (PureBasicInterfaceBlock) element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final List<PureBasicParseIdentifier> parseIdentifierList = element.getInterfaceHead().getParseIdentifierList();
        final PureBasicParseIdentifier parseIdentifier = parseIdentifierList.isEmpty() ? null : parseIdentifierList.get(0);
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
        return AllIcons.Nodes.Interface; // TODO: replace by own icon
    }
}
