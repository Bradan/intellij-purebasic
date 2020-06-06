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

package eu.bradan.purebasic.module;

import com.intellij.util.xmlb.annotations.Transient;
import eu.bradan.purebasic.builder.PureBasicCompiler;
import org.jetbrains.annotations.Nullable;

public class PureBasicTargetSettings {
    private String name;
    private String sdkLabel;
    private String inputFile;
    private String outputFile;

    public PureBasicTargetSettings() {
        this("");
    }

    public PureBasicTargetSettings(String name) {
        this.name = name;
        this.sdkLabel = "";
        this.inputFile = "";
        this.outputFile = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    @Transient
    public PureBasicCompiler getSdk() {
        return PureBasicCompiler.getCompilerByLabel(getSdkLabel());
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getSdkLabel() {
        return this.sdkLabel;
    }

    public void setSdkLabel(String sdkLabel) {
        this.sdkLabel = sdkLabel != null ? sdkLabel : "";
    }
}
