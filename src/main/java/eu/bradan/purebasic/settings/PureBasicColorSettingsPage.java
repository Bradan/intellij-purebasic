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

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import eu.bradan.purebasic.PureBasicHighlighter;
import eu.bradan.purebasic.PureBasicIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class PureBasicColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Character Sequences", PureBasicHighlighter.CHARACTER),
            new AttributesDescriptor("Comments", PureBasicHighlighter.COMMENT),
            new AttributesDescriptor("Constants", PureBasicHighlighter.CONSTANT),
            new AttributesDescriptor("Identifiers", PureBasicHighlighter.IDENTIFIER),
            new AttributesDescriptor("Inline Assembly", PureBasicHighlighter.INLINE_ASM),
            new AttributesDescriptor("Keywords", PureBasicHighlighter.KEYWORD),
            new AttributesDescriptor("Numbers", PureBasicHighlighter.NUMBER),
            new AttributesDescriptor("Operators", PureBasicHighlighter.OPERATOR),
            new AttributesDescriptor("Strings", PureBasicHighlighter.STRING),
            new AttributesDescriptor("[Invalid Symbol]", PureBasicHighlighter.BAD_CHARACTER)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return PureBasicIcons.PUREBASIC;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PureBasicHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "Structure TestStructure\n" +
                "    integer.i\n" +
                "EndStructure\n" +
                "#ConstantValue = 3 + '0'\n" +
                "; Declare some variables\n" +
                "Global test.TestStructure, *pointer.TestStructure\n" +
                "; Assign some values\n" +
                "test\\integer = (5 + #ConstantValue) * 10\n" +
                "*pointer = @test\n" +
                "; Call some function\n" +
                "MessageRequester(\"Title\", \"This is a string\")";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "PureBasic";
    }
}
