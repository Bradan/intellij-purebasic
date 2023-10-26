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

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;

public class PureBasicMacro {
    private final String name;
    private final String code;
    private final Argument[] arguments;

    public PureBasicMacro(String name, String code, Argument... arguments) {
        this.name = name;
        this.code = code;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getCode(String... argumentValues) {
        final var valueMap = new HashMap<String, String>();

        for (int i = 0; i < arguments.length; i++) {
            var value = i < argumentValues.length ? argumentValues[i] : arguments[i].getDefaultValue();
            if (value == null) {
                value = "";
            }
            valueMap.put(arguments[i].getName(), value);
        }

        final var subst = new StringSubstitutor(valueMap);
        return subst.replace(code);
    }

    public static class Argument {
        private final String name;
        private final String defaultValue;

        public Argument(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }
    }
}
