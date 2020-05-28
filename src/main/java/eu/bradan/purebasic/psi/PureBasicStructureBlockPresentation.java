package eu.bradan.purebasic.psi;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class PureBasicStructureBlockPresentation implements ItemPresentation {
    private final PureBasicStructureBlock element;

    public PureBasicStructureBlockPresentation(PsiElement element) {
        this.element = (PureBasicStructureBlock) element;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final List<PureBasicParseIdentifier> parseIdentifierList = element.getStructureHead().getParseIdentifierList();
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
        return AllIcons.Nodes.Class; // TODO: replace by own icon
    }
}
