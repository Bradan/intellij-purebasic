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

package eu.bradan.purebasic.module;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import eu.bradan.purebasic.PureBasicFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

public class PureBasicTargetSettingsPanel extends JPanel {
    private final FileChooserDescriptor fileChooserDescriptor;
    private JTextField textFieldLabel;
    private TextFieldWithBrowseButton textFieldInput;
    private JPanel root;
    private TextFieldWithBrowseButton textFieldOutput;

    public PureBasicTargetSettingsPanel(@Nullable Module module) {
        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);

        fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(PureBasicFileType.INSTANCE);
        final VirtualFile root = module != null && module.getModuleFile() != null ? module.getModuleFile().getParent() : null;
        if (root != null) {
            fileChooserDescriptor.setRoots(root);
        }
        textFieldInput.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor) {
            @SuppressWarnings("UnstableApiUsage")
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                if (root != null) {
                    return Paths.get(root.getPath())
                            .relativize(Paths.get(chosenFile.getPath()))
                            .toString();
                }
                return super.chosenFileToResultingText(chosenFile);
            }
        });
        textFieldOutput.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor) {
            @SuppressWarnings("UnstableApiUsage")
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                if (root != null) {
                    return Paths.get(root.getPath())
                            .relativize(Paths.get(chosenFile.getPath()))
                            .toString();
                }
                return super.chosenFileToResultingText(chosenFile);
            }
        });
    }

    public void setData(@NotNull PureBasicTargetSettings data) {
        textFieldInput.setText(data.getInputFile());
        textFieldOutput.setText(data.getOutputFile());
        textFieldLabel.setText(data.getSdkLabel());
    }

    public void getData(@NotNull PureBasicTargetSettings data) {
        data.setInputFile(textFieldInput.getText());
        data.setOutputFile(textFieldOutput.getText());
        data.setSdkLabel(textFieldLabel.getText());
    }

    public boolean isModified(@NotNull PureBasicTargetSettings data) {
        if (textFieldInput.getText() != null ? !textFieldInput.getText().equals(data.getInputFile()) : data.getInputFile() != null)
            return true;
        if (textFieldOutput.getText() != null ? !textFieldOutput.getText().equals(data.getOutputFile()) : data.getOutputFile() != null)
            return true;
        return textFieldLabel.getText() != null ? !textFieldLabel.getText().equals(data.getSdkLabel()) : data.getSdkLabel() != null;
    }
}
