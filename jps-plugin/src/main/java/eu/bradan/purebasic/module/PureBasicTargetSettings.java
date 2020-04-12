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

import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.Objects;

public class PureBasicTargetSettings {
    public String name;
    public String sdk;
    public String inputFile;
    public String outputFile;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        PureBasicTargetSettings options;
        try {
            options = (PureBasicTargetSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            options = new PureBasicTargetSettings();
        }
        XmlSerializerUtil.copyBean(this, options);
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PureBasicTargetSettings)) return false;

        PureBasicTargetSettings that = (PureBasicTargetSettings) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(sdk, that.sdk)) return false;
        if (!Objects.equals(inputFile, that.inputFile)) return false;
        return Objects.equals(outputFile, that.outputFile);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (sdk != null ? sdk.hashCode() : 0);
        result = 31 * result + (inputFile != null ? inputFile.hashCode() : 0);
        result = 31 * result + (outputFile != null ? outputFile.hashCode() : 0);
        return result;
    }
}
