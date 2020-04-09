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

package eu.bradan.purebasic.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

public class FileTextField extends JPanel {
    public interface UpdateListener {
        void update();
    }

    private final JTextField textField;
    private final LinkedList<FileTextField.UpdateListener> listeners;

    public FileTextField() {
        listeners = new LinkedList<>();

        this.setLayout(new BorderLayout(2, 0));

        textField = new JTextField();
        final JButton buttonChoose = new JButton("...");

        add(textField, BorderLayout.CENTER);
        add(buttonChoose, BorderLayout.EAST);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireUpdateListeners();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireUpdateListeners();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireUpdateListeners();
            }
        });

        buttonChoose.addActionListener(e -> {
            String path = openChooser(textField.getText());
            if (path != null) {
                textField.setText(path);
                fireUpdateListeners();
            }
        });
    }

    protected String openChooser(String path) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(path));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    private void fireUpdateListeners() {
        for (FileTextField.UpdateListener l : listeners) {
            l.update();
        }
    }

    public void addUpdateListener(FileTextField.UpdateListener listener) {
        listeners.add(listener);
    }

    public void removeUpdateListener(FileTextField.UpdateListener listener) {
        listeners.remove(listener);
    }
}
