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

package eu.bradan.purebasic;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class PureBasicCompiler {
    private String sdkHome;
    private File compiler;

    private PureBasicCompiler(String sdkHome, File compiler) {
        this.sdkHome = sdkHome;
        this.compiler = compiler;
    }

    public String getSdkHome() {
        return sdkHome;
    }

    public void setSdkHome(String sdkHome) {
        this.sdkHome = sdkHome;
        this.compiler = getCompilerExecutable(sdkHome);
    }

    @Nullable
    private static File getCompilerExecutable(String sdkHome) {
        // Linux
        File compiler = Paths.get(sdkHome, "compilers", "pbcompiler").toFile();
        if (!compiler.exists()) {
            // Windows
            compiler = Paths.get(sdkHome, "compilers", "pbcompiler.exe").toFile();
        }
        if (!compiler.exists()) {
            // MacOS
            compiler = Paths.get(sdkHome, "Contents", "Resources", "compilers", "pbcompiler").toFile();
        }
        if (compiler.exists() && compiler.canExecute()) {
            return compiler;
        }

        return null;
    }

    @Nullable
    public String getVersionString() {
        BufferedReader outputReader = null;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(new String[]{compiler.getAbsolutePath(), "--version"});

            outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            return outputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputReader != null) {
                try {
                    outputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Nullable
    public static PureBasicCompiler getPureBasicCompiler(String sdkHome) {
        File compiler = getCompilerExecutable(sdkHome);
        if (compiler != null) {
            return new PureBasicCompiler(sdkHome, compiler);
        }

        return null;
    }
}
