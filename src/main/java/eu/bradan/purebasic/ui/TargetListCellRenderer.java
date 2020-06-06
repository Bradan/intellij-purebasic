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

package eu.bradan.purebasic.ui;

import eu.bradan.purebasic.module.PureBasicTargetSettings;

import javax.swing.*;
import java.awt.*;

public class TargetListCellRenderer extends JLabel implements ListCellRenderer<PureBasicTargetSettings> {
    public TargetListCellRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PureBasicTargetSettings> list,
                                                  PureBasicTargetSettings value, int index,
                                                  boolean isSelected, boolean hasFocus) {
        if (value != null) {
            setText(value.getName() + " (" + value.getOutputFile() + ")");
        } else {
            setText("<<invalid>>");
        }
        return this;
    }
}
