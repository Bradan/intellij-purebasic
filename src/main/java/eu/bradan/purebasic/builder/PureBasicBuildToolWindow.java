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

package eu.bradan.purebasic.builder;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PureBasicBuildToolWindow extends JPanel {
    private final DefaultListModel<CompileMessage> model;
    private JPanel toolWindowContent;
    private JBList<CompileMessage> listCompileResult;
    private JScrollPane scrollPane;

    public PureBasicBuildToolWindow(ToolWindow toolWindow) {
        this.setLayout(new BorderLayout());
        this.add(toolWindowContent, BorderLayout.CENTER);

        model = new DefaultListModel<>();

        final JLabel cell = new JLabel();
        cell.setBackground(null);
        cell.setHorizontalAlignment(JLabel.LEADING);

        listCompileResult.setModel(model);
        listCompileResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCompileResult.setCellRenderer(new CompilerMessageCellRenderer());
        listCompileResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final CompileMessage msg = listCompileResult.getSelectedValue();
                if (msg != null)
                    msg.navigateTo();
            }
        });
        listCompileResult.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    final CompileMessage msg = listCompileResult.getSelectedValue();
                    if (msg != null)
                        msg.navigateTo();
                }
            }
        });
    }

    public void clear() {
        model.clear();
    }

    public void addLine(String s) {
        model.addElement(new CompileMessage(
                CompileMessage.CompileMessageType.INFO,
                s, null, -1));
    }

    public void addElement(CompileMessage msg) {
        model.addElement(msg);
    }

    public JPanel getContent() {
        return this;
    }

    private static class CompilerMessageCellRenderer extends ColoredListCellRenderer<CompileMessage> {
        @Override
        protected void customizeCellRenderer(@NotNull JList list, CompileMessage value,
                                             int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                if (value.type == CompileMessage.CompileMessageType.ERROR) {
                    setIcon(AllIcons.Ide.FatalError);
                    append(value.message, new SimpleTextAttributes(
                            SimpleTextAttributes.STYLE_BOLD,
                            JBColor.foreground()));
                } else {
                    setIcon(null);
                    append(value.message);
                }
            }
        }
    }
}
