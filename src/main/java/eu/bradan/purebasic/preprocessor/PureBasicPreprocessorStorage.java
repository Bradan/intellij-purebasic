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
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PureBasicPreprocessorStorage {
    private static final HashMap<Project, PureBasicPreprocessorStorage> instances = new HashMap<>();
    private final HashMap<String, LocatableObject<PureBasicMacro>> macros = new HashMap<>();

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

    public void addMacro(PsiFile file, PureBasicMacro macro) {
        macros.put(macro.getName(), new LocatableObject<>(macro, file));
    }

    @Nullable
    public LocatableObject<PureBasicMacro> getMacro(String name) {
        return macros.getOrDefault(name, null);
    }

    public static class LocatableObject<T> {
        private final T object;
        private final PsiFile file;

        private LocatableObject(T object, PsiFile file) {
            this.object = object;
            this.file = file;
        }

        public T getObject() {
            return object;
        }

        public PsiFile getFile() {
            return file;
        }
    }
}
