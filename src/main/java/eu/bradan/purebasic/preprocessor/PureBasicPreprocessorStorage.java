/*
 * Copyright (c) 2023 Daniel Brall
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

package eu.bradan.purebasic.preprocessor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PureBasicPreprocessorStorage {
    private static final HashMap<Project, PureBasicPreprocessorStorage> instances = new HashMap<>();
    private final HashMap<String, PureBasicPreprocessorScope> scopes = new HashMap<>();

    private PureBasicPreprocessorStorage() {
    }

    public static PureBasicPreprocessorStorage getInstance(Project project) {
        var inst = instances.getOrDefault(project, null);
        if (inst == null) {
            inst = new PureBasicPreprocessorStorage();
            instances.put(project, inst);
        }
        return inst;
    }

    @Nullable
    private static VirtualFile getVirtualFile(PsiFile file) {
        VirtualFile vfile = null;
        for (var f = file; f != null && vfile == null; f = f.getOriginalFile()) {
            vfile = f.getVirtualFile();
        }
        return vfile;
    }

    public synchronized void clearInvalid() {
        for (var entry : scopes.entrySet()) {
            var vfile = VirtualFileManager.getInstance().findFileByUrl(entry.getKey());
            if (vfile == null || !vfile.isValid()) {
                scopes.remove(entry.getKey());
            }
        }
    }

    public synchronized void addScope(VirtualFile file, PureBasicPreprocessorScope scope) {
        scopes.put(file.getCanonicalPath(), scope);
    }

    public synchronized void addScope(PsiFile file, PureBasicPreprocessorScope scope) {
        VirtualFile virtualFile = getVirtualFile(file);
        if (virtualFile != null) {
            addScope(virtualFile, scope);
        }
    }

    public synchronized void addScope(String url, PureBasicPreprocessorScope scope) {
        var virtualFile = VirtualFileManager.getInstance().findFileByUrl(url);
        if (virtualFile != null) {
            addScope(virtualFile, scope);
        }
    }

    @Nullable
    public synchronized PureBasicPreprocessorScope getScope(VirtualFile file) {
        return scopes.getOrDefault(file.getCanonicalPath(), null);
    }

    @Nullable
    public synchronized PureBasicPreprocessorScope getScope(PsiFile file) {
        var virtualFile = getVirtualFile(file);
        return virtualFile != null ? getScope(virtualFile) : null;
    }

    @Nullable
    public synchronized PureBasicPreprocessorScope getScope(String url) {
        var virtualFile = VirtualFileManager.getInstance().findFileByUrl(url);
        return virtualFile != null ? getScope(virtualFile) : null;
    }
}
