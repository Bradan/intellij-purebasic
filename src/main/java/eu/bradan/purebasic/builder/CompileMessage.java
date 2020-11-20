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

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class CompileMessage {
    public final CompileMessageType type;
    public final String message;
    public final String file;
    public final int line;

    public CompileMessage(CompileMessageType type, String message, String file, int line) {
        this.type = type;
        this.message = message;
        this.file = file;
        this.line = line;
    }

    public void navigateTo() {
        if (file == null || line <= 0) return;

        final VirtualFile vFile = VfsUtil.findFileByIoFile(new File(file), true);
        if (vFile == null || vFile.getFileType().isBinary()) return;

        Project project = ProjectLocator.getInstance().guessProjectForFile(vFile);
        if (project == null) return;

        FileEditorManager.getInstance(project).openEditor(
                new OpenFileDescriptor(project, vFile, line - 1, 0),
                true);
    }

    public enum CompileMessageType {
        INFO,
        WARN,
        ERROR
    }
}
