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

package eu.bradan.purebasic.settings;

import com.intellij.util.xmlb.annotations.OptionTag;
import eu.bradan.purebasic.builder.PureBasicCompiler;

import java.util.ArrayList;
import java.util.LinkedList;

public class PureBasicCompilerSettingsState {
    @OptionTag
    private final LinkedList<Sdk> sdks;

    public PureBasicCompilerSettingsState() {
        sdks = new LinkedList<>();
    }

    public PureBasicCompiler[] getSdks() {
        ArrayList<PureBasicCompiler> result = new ArrayList<>();
        for (Sdk sdk : sdks) {
            PureBasicCompiler compiler = PureBasicCompiler.getOrLoadCompilerByHome(sdk.getHome());
            if (compiler != null) {
                compiler.setLabels(sdk.getLabels());
            }
            result.add(compiler);
        }
        return result.toArray(new PureBasicCompiler[0]);
    }

    public void addSdk(PureBasicCompiler sdk) {
        if (sdk != null) {
            sdks.add(new Sdk(sdk.getSdkHome(), sdk.getLabels()));
        }
    }

    public void clearSdks() {
        sdks.clear();
    }

    private static class Sdk {
        private String home;
        private String labels;

        public Sdk() {
            this("", "");
        }

        public Sdk(String home, String labels) {
            this.home = home;
            this.labels = labels;
        }

        public String getHome() {
            return home;
        }

        public void setHome(String home) {
            this.home = home;
        }

        public String getLabels() {
            return labels;
        }

        public void setLabels(String labels) {
            this.labels = labels;
        }
    }
}
