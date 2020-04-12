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

package eu.bradan.purebasic.model;

import eu.bradan.purebasic.builder.PureBasicCompiler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Objects;

public class JpsPureBasicCompilers {
    public static final JpsPureBasicCompilers INSTANCE = new JpsPureBasicCompilers();

    private LinkedList<CompilerInfo> compilerList;

    private JpsPureBasicCompilers() {
        compilerList = new LinkedList<>();
    }

    public void addCompiler(String sdkHome) {
        if (sdkHome != null) {
            PureBasicCompiler compiler = PureBasicCompiler.getPureBasicCompiler(sdkHome);
            if (compiler != null) {
                CompilerInfo info = new CompilerInfo();
                info.compiler = compiler;
                info.version = compiler.getVersionString();
                compilerList.add(info);
            }
        }
    }

    @Nullable
    public PureBasicCompiler getCompilerByVersion(String version) {
        for (CompilerInfo info : compilerList) {
            if (Objects.equals(info.version, version)) {
                return info.compiler;
            }
        }
        return null;
    }

    private static class CompilerInfo {
        public PureBasicCompiler compiler;
        public String version;
    }
}
