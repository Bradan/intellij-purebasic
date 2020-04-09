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

import com.intellij.openapi.components.ServiceManager;
import eu.bradan.purebasic.PureBasicCompiler;
import eu.bradan.purebasic.settings.PureBasicCompilerSettings;
import eu.bradan.purebasic.ui.FileTextField;

import javax.swing.*;
import java.awt.*;

public class PureBasicTargetSettingsPanel extends JPanel {
    private JComboBox<String> comboBoxSdk;
    private FileTextField textFieldInput;
    private JTextField textFieldOutput;
    private JPanel root;

    private PureBasicTargetSettings targetSettings;

    public PureBasicTargetSettingsPanel() {
        targetSettings = null;

        final PureBasicCompilerSettings settings = ServiceManager.getService(PureBasicCompilerSettings.class);
        final PureBasicCompilerSettings.State state = settings.getState();
        if (state != null) {
            for (String sdk : state.sdks) {
                final PureBasicCompiler compiler = PureBasicCompiler.getPureBasicCompiler(sdk);
                if (compiler != null) {
                    comboBoxSdk.addItem(compiler.getVersionString());
                }
            }
            comboBoxSdk.addItem("<Other SDK>");
        }
        textFieldOutput.setText("Output.exe");

        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);

        comboBoxSdk.addActionListener(e -> {
            if (targetSettings != null) {
                targetSettings.sdk = (String) comboBoxSdk.getSelectedItem();
            }
        });
        textFieldInput.addUpdateListener(() -> {
            if (targetSettings != null) {
                targetSettings.inputFile = textFieldInput.getText();
            }
        });
        textFieldOutput.addActionListener(e -> {
            if (targetSettings != null) {
                targetSettings.outputFile = textFieldOutput.getText();
            }
        });
    }

    public JPanel getRoot() {
        return root;
    }

    public void setTargetSettings(PureBasicTargetSettings targetSettings) {
        this.targetSettings = null;
        comboBoxSdk.setSelectedIndex(comboBoxSdk.getItemCount() - 1);
        if (targetSettings != null) {
            textFieldInput.setText(targetSettings.inputFile);
            textFieldOutput.setText(targetSettings.outputFile);
            for (int i = 0; i < comboBoxSdk.getItemCount(); i++) {
                if (comboBoxSdk.getItemAt(i).equals(targetSettings.sdk)) {
                    comboBoxSdk.setSelectedIndex(i);
                    break;
                }
            }
        }
        this.targetSettings = targetSettings;
    }
}
