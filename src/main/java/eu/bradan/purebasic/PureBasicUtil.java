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

import java.io.File;
import java.util.Map;

public class PureBasicUtil {
    public static String substitute(String text, Map<String, String> substitutions) throws SubstitutionException {
        final var builder = new StringBuilder();

        boolean awaitingBrace = false;

        for (int i = 0; i < text.length(); i++) {
            final var c = text.charAt(i);
            if (awaitingBrace) {
                awaitingBrace = false;
                switch (c) {
                    case '{':
                        final var start = i;
                        i++;
                        while (i < text.length() && text.charAt(i) != '}') {
                            i++;
                        }
                        final var end = i;
                        final var key = text.substring(start + 1, end);
                        final var value = substitutions.getOrDefault(key, null);
                        if (value != null) {
                            builder.append(value);
                        } else {
                            throw new SubstitutionException("Substitution key " + key + " not found");
                        }
                        continue;
                    case '$':
                        builder.append(c);
                        continue;
                    default:
                        throw new SubstitutionException("Expected { or $ after $");
                }
            } else if (c == '$') {
                awaitingBrace = true;
                continue;
            }
            builder.append(c);
        }

        return builder.toString();
    }

    public static class SubstitutionException extends Exception {
        public SubstitutionException(String message) {
            super(message);
        }
    }

    public static String getStringContents(String string) {
        if (string == null) {
            return null;
        }

        boolean escaped = false;
        if (string.startsWith("~")) {
            string = string.substring(1);
            escaped = true;
        }

        if (string.startsWith("\"")) {
            string = string.substring(1);
        }
        if (string.endsWith("\"")) {
            string = string.substring(0, string.length() - 1);
        }
        if (escaped) {
            string = string.replaceAll("\\\\a", "\u0007")
                    .replaceAll("\\\\b", "\u0008")
                    .replaceAll("\\\\f", "\u000c")
                    .replaceAll("\\\\n", "\n")
                    .replaceAll("\\\\r", "\r")
                    .replaceAll("\\\\t", "\t")
                    .replaceAll("\\\\v", "\u000b")
                    .replaceAll("\\\\\"", "\"")
                    .replaceAll("\\\\", "\\");
        }

        return string;
    }

    public static String relativeTo(File file, File relativeTo) {
        return relativeTo.toURI().relativize(file.toURI()).getPath();
    }
}
