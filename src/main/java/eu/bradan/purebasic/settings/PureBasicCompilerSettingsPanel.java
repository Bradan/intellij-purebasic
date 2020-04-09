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

import com.intellij.ui.JBColor;
import eu.bradan.purebasic.PureBasicCompiler;
import eu.bradan.purebasic.ui.DirectoryTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PureBasicCompilerSettingsPanel {
    private JScrollPane scrollPaneSdks;
    private JButton buttonAddSdk;
    private JPanel root;
    private JPanel panelSdks;

    private LinkedList<DirectoryTextField> sdkFields;


    private ResourceBundle resources = ResourceBundle.getBundle("texts/texts");

    public PureBasicCompilerSettingsPanel(PureBasicCompilerSettings.State state) {
        this.sdkFields = new LinkedList<>();

        panelSdks.setLayout(new BoxLayout(panelSdks, BoxLayout.Y_AXIS));

        buttonAddSdk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this) {
                    createSdkEntry("");
                }
            }
        });

        if (state != null) {
            for (String sdk : state.sdks) {
                createSdkEntry(sdk);
            }
        }
    }

    private void createSdkEntry(@NotNull String sdkHome) {
        final JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createLineBorder(JBColor.border()));

        final JLabel sdkVersion = new JLabel("");
        final DirectoryTextField sdkDir = new DirectoryTextField();
        final JButton sdkRemove = new JButton();

        sdkDir.addUpdateListener(() -> {
            final PureBasicCompiler compiler = PureBasicCompiler.getPureBasicCompiler(sdkDir.getText());
            if (compiler != null) {
                sdkVersion.setText(compiler.getVersionString());
            } else {
                sdkVersion.setText("");
            }
        });

        sdkRemove.setText(resources.getString("remove"));

        sdkRemove.addActionListener(e -> {
            panelSdks.remove(panel);
            sdkFields.remove(sdkDir);
        });

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        panel.add(sdkVersion, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        panel.add(sdkRemove, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        panel.add(sdkDir, c);

        sdkDir.setText(sdkHome);

        panelSdks.add(panel);
        sdkFields.add(sdkDir);
    }

    public JPanel getRoot() {
        return root;
    }

    @NotNull
    public PureBasicCompilerSettings.State getState() {
        PureBasicCompilerSettings.State results = new PureBasicCompilerSettings.State();

        for (DirectoryTextField tf : sdkFields) {
            results.sdks.add(tf.getText());
        }

        return results;
    }
}
