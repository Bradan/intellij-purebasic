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

package eu.bradan.purebasic.builder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CompileLog extends AbstractListModel<CompileMessage> {
    private static final CompileLog compileLog = new CompileLog();
    private final ArrayList<CompileMessage> list;

    private CompileLog() {
        super();
        list = new ArrayList<>();
    }

    public static CompileLog getInstance() {
        return compileLog;
    }

    public void clear() {
        if (!list.isEmpty()) {
            list.clear();
            EventQueue.invokeLater(() ->
                    fireIntervalRemoved(this, 0, list.size()));
        }
    }

    public void addLine(String s) {
        addMessage(new CompileMessage(
                CompileMessage.CompileMessageType.INFO,
                s, null, -1));
    }

    public void addMessage(CompileMessage message) {
        list.add(message);
        EventQueue.invokeLater(() ->
                fireIntervalAdded(this, list.size() - 1, list.size() - 1));
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public CompileMessage getElementAt(int index) {
        return list.get(index);
    }
}
