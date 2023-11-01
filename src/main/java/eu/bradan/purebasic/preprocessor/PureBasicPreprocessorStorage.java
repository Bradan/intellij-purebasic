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
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PureBasicPreprocessorStorage {
    private static final HashMap<Project, PureBasicPreprocessorStorage> instances = new HashMap<>();
    private final HashMap<String, LinkedList<LocatableObject<PureBasicMacro>>> macros = new HashMap<>();

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

    public synchronized void clearInvalid() {
        for (var entry : macros.entrySet()) {
            var list = entry.getValue();
            list.removeIf(locatableObject -> locatableObject.getFile() == null);
            if (list.isEmpty()) {
                macros.remove(entry.getKey());
            }
        }
    }

    public synchronized void addMacro(PsiFile file, PureBasicMacro macro) {
        var list = macros.getOrDefault(macro.getName(), null);
        if (list == null) {
            list = new LinkedList<>();
            macros.put(macro.getName(), list);
        }

        list.add(new LocatableObject<>(macro, file));
    }

    @NotNull
    public synchronized List<LocatableObject<PureBasicMacro>> getMacros(String name) {
        var list = macros.getOrDefault(name, null);
        if (list != null) {
            return new ArrayList<>(list);
        }
        return new ArrayList<>();
    }

    public static class LocatableObject<T> {
        private final T object;

        @NotNull
        private final WeakReference<PsiFile> file;

        private LocatableObject(T object, PsiFile file) {
            this.object = object;
            this.file = new WeakReference<>(file);
        }

        public T getObject() {
            return object;
        }

        public PsiFile getFile() {
            return file.get();
        }
    }
}
