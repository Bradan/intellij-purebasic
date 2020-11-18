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
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PureBasicBuildToolWindow extends JPanel {
    private final DefaultListModel<PureBasicCompiler.CompileMessage> model;
    private JPanel toolWindowContent;
    private JBList<PureBasicCompiler.CompileMessage> listCompileResult;

    public PureBasicBuildToolWindow(ToolWindow toolWindow) {
        this.setLayout(new BorderLayout());
        this.add(toolWindowContent, BorderLayout.CENTER);

        model = new DefaultListModel<>();
        listCompileResult.setModel(model);
        listCompileResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCompileResult.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel result;
            if (value.type == PureBasicCompiler.CompileMessageType.ERROR) {
                result = new JLabel(value.message, AllIcons.Ide.FatalError, JLabel.LEADING);
            } else {
                result = new JLabel(value.message);
            }
            result.setBackground(isSelected ? JBColor.BLUE : JBColor.background());
            return result;
        });
        listCompileResult.addListSelectionListener(e -> {
            final PureBasicCompiler.CompileMessage msg = model.get(e.getFirstIndex());
            if (msg.file == null || msg.line <= 0) return;

            final VirtualFile file = VfsUtil.findFileByIoFile(new File(msg.file), true);
            if (file == null || file.getFileType().isBinary()) return;

            Project project = ProjectLocator.getInstance().guessProjectForFile(file);
            if (project == null) return;

            FileEditorManager.getInstance(project).openEditor(
                    new OpenFileDescriptor(project, file, msg.line - 1, 0),
                    true);
        });
    }

    public void clear() {
        model.clear();
    }

    public void addLine(String s) {
        model.addElement(new PureBasicCompiler.CompileMessage(
                PureBasicCompiler.CompileMessageType.INFO,
                s, null, -1));
    }

    public void addElement(PureBasicCompiler.CompileMessage msg) {
        model.addElement(msg);
    }

    public JPanel getContent() {
        return this;
    }
}
