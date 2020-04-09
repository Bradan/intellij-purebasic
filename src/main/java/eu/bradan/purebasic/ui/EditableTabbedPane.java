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

import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

public class EditableTabbedPane extends JBTabbedPane {
    private final JTextField textField;
    private int currentlyEditing = -1;
    private Component previousComponent = null;
    private final MouseListener mouseListener = new MouseListener() {
        @Override
        public void mouseClicked(@NotNull MouseEvent e) {
            for (int i = 0; i < getTabCount(); i++) {
                if (getTabComponentAt(i) == e.getComponent()) {
                    setSelectedIndex(i);
                    if (e.getClickCount() == 2) {
                        startEditing(i);
                    }
                    e.consume();
                    break;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    };
    private final LinkedList<TitleChangedListener> listeners;

    public EditableTabbedPane() {
        super();
        textField = new JTextField();
        listeners = new LinkedList<>();

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                stopEditing();
            }
        });

        textField.addActionListener(e -> stopEditing());
    }

    public void addTitleChangeListener(TitleChangedListener l) {
        listeners.add(l);
    }

    public void removeTitleChangeListener(TitleChangedListener l) {
        listeners.remove(l);
    }

    private void fireTitleChangeListeners() {
        for (TitleChangedListener l : listeners) {
            l.titleChanged();
        }
    }

    private void updateTabs() {
        outerLoop:
        for (int i = 0; i < getTabCount(); i++) {
            final Component component = getTabComponentAt(i);
            if (component != null) {
                for (MouseListener l : component.getMouseListeners()) {
                    if (l == mouseListener) {
                        continue outerLoop;
                    }
                }
                component.setMinimumSize(new Dimension(component.getPreferredSize().width, component.getPreferredSize().height));
                component.addMouseListener(mouseListener);
            }
        }
    }

    public void stopEditing() {
        if (currentlyEditing >= 0 && previousComponent != null) {
            final String text = textField.getText();

            if (previousComponent instanceof JLabel) {
                final JLabel label = (JLabel) previousComponent;
                label.setText(text);
            }

            setTabComponentAt(currentlyEditing, previousComponent);
            setTitleAt(currentlyEditing, text);

            currentlyEditing = -1;
            previousComponent = null;
        }
    }

    public void startEditing(int tabIndex) {
        stopEditing();

        previousComponent = getTabComponentAt(tabIndex);
        currentlyEditing = tabIndex;

        textField.setText(getTitleAt(tabIndex));
        setTabComponentAt(tabIndex, textField);
        textField.requestFocus();
    }

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        stopEditing();
        super.addTab(title, icon, component, tip);
        updateTabs();
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        stopEditing();
        super.addTab(title, icon, component);
        updateTabs();
    }

    @Override
    public void addTab(String title, Component component) {
        stopEditing();
        super.addTab(title, component);
        updateTabs();
    }

    public interface TitleChangedListener {
        void titleChanged();
    }
}
