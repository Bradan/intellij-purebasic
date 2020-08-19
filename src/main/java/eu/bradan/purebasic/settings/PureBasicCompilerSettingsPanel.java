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

package eu.bradan.purebasic.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import eu.bradan.purebasic.builder.PureBasicCompiler;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class PureBasicCompilerSettingsPanel {
    private JScrollPane scrollPaneSdks;
    private JButton buttonAddSdk;
    private JPanel root;
    private JPanel panelSdks;

    public PureBasicCompilerSettingsPanel() {
        panelSdks.setLayout(new BoxLayout(panelSdks, BoxLayout.Y_AXIS));

        buttonAddSdk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this) {
                    panelSdks.add(new PBCompilerPanel(panelSdks, "", ""));
                }
            }
        });
    }

    public JPanel getRoot() {
        return root;
    }

    public void setData(@NotNull PureBasicCompilerSettingsState data) {
        panelSdks.removeAll();

        for (PureBasicCompiler compiler : data.getSdks()) {
            panelSdks.add(new PBCompilerPanel(panelSdks, compiler.getSdkHome(), compiler.getLabels()));
        }
    }

    public void getData(@NotNull PureBasicCompilerSettingsState data) {
        data.clearSdks();
        PBCompilerPanel[] panels = Arrays.stream(panelSdks.getComponents())
                .filter(x -> x instanceof PBCompilerPanel)
                .map(x -> (PBCompilerPanel) x)
                .toArray(PBCompilerPanel[]::new);
        for (PBCompilerPanel pbCompilerPanel : panels) {
            PureBasicCompiler compiler = PureBasicCompiler.getOrLoadCompilerByHome(pbCompilerPanel.getSdkHome());
            if (compiler != null) {
                compiler.setLabels(pbCompilerPanel.getSdkLabels());
                data.addSdk(compiler);
            }
        }
    }

    public boolean isModified(@NotNull PureBasicCompilerSettingsState data) {
        PBCompilerPanel[] panels = Arrays.stream(panelSdks.getComponents())
                .filter(x -> x instanceof PBCompilerPanel)
                .map(x -> (PBCompilerPanel) x)
                .toArray(PBCompilerPanel[]::new);

        if (data.getSdks().length != panels.length) {
            // amount of sdks has changed
            return true;
        }
        for (PBCompilerPanel pbCompilerPanel : panels) {
            boolean found = false;
            for (PureBasicCompiler compiler : data.getSdks()) {
                if (Objects.equals(compiler.getSdkHome(), pbCompilerPanel.getSdkHome())
                        && Objects.equals(compiler.getLabels(), pbCompilerPanel.getSdkLabels())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return true;
            }
        }
        return false;
    }

    private static class PBCompilerPanel extends JPanel {
        private final TextFieldWithBrowseButton sdkDir;
        private final JTextField sdkLabels;

        public PBCompilerPanel(JComponent parent, String sdkHome, String labels) {
            super();

            setLayout(new BorderLayout());

            final ResourceBundle resources = ResourceBundle.getBundle("texts/texts");

            setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

            final JPanel panel = new JPanel();
            this.add(panel, BorderLayout.NORTH);

            panel.setBorder(BorderFactory.createLineBorder(JBColor.border()));

            final JLabel sdkVersion = new JLabel("");
            sdkDir = new TextFieldWithBrowseButton();
            final JButton sdkRemove = new JButton();
            sdkLabels = new JTextField();

            sdkDir.getTextField().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    update();
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    update();
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    update();
                }

                private void update() {
                    final PureBasicCompiler compiler = PureBasicCompiler.getOrLoadCompilerByHome(sdkDir.getText());
                    if (compiler != null) {
                        sdkVersion.setText(compiler.getVersionString());
                    } else {
                        sdkVersion.setText("");
                    }
                }
            });

            sdkRemove.setText(resources.getString("remove"));
            sdkRemove.addActionListener(e -> {
                parent.remove(this);
            });

            panel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.fill = GridBagConstraints.HORIZONTAL;

            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            c.weightx = 1.0;
            panel.add(new JLabel(resources.getString("sdk_version")), c);

            c.gridx = 1;
            c.gridy = 0;
            c.gridwidth = 1;
            c.weightx = 1.0;
            panel.add(sdkVersion, c);

            c.gridx = 2;
            c.gridy = 0;
            c.gridwidth = 1;
            c.weightx = 0.0;
            panel.add(sdkRemove, c);

            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            c.weightx = 1.0;
            panel.add(new JLabel(resources.getString("sdk_home_directory")), c);

            c.gridx = 1;
            c.gridy = 1;
            c.gridwidth = 2;
            c.weightx = 1.0;
            panel.add(sdkDir, c);

            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 1;
            c.weightx = 0.0;
            panel.add(new JLabel(resources.getString("sdk_labels")), c);

            c.gridx = 1;
            c.gridy = 2;
            c.gridwidth = 2;
            c.weightx = 1.0;
            panel.add(sdkLabels, c);

            sdkDir.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()));

            sdkDir.setText(sdkHome);
            sdkLabels.setText(labels);
        }

        public String getSdkHome() {
            return sdkDir.getText();
        }

        public String getSdkLabels() {
            return sdkLabels.getText();
        }
    }
}
