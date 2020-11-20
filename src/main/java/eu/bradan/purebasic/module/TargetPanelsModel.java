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

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.EditableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

class TargetPanelsModel extends AbstractTableModel implements EditableModel {
    private final ArrayList<Entry> panels;
    private final Module module;

    public TargetPanelsModel(@Nullable Module module) {
        this.module = module;
        panels = new ArrayList<>();
    }

    private boolean contains(String name) {
        return panels.stream().anyMatch(x -> name.equals(x.name));
    }

    private String ensureUniqueName(String name) {
        String result = name;

        int i = 1;
        while (contains(result)) {
            i++;
            result = name + " " + i;
        }
        return result;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Target";
        }
        return super.getColumnName(column);
    }

    @Override
    public int getRowCount() {
        return panels.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        final Entry entry = panels.get(row);
        return entry != null ? entry.getName() : "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && aValue instanceof String) {
            Entry entry = panels.get(rowIndex);
            if (!Objects.equals(entry.getName(), aValue)) {
                panels.get(rowIndex).setName(ensureUniqueName((String) aValue));
                fireTableRowsUpdated(rowIndex, rowIndex);
            }
        }
    }

    @Override
    public void addRow() {
        add();
    }

    @Override
    public void exchangeRows(int i, int j) {
    }

    @Override
    public boolean canExchangeRows(int i, int j) {
        return false;
    }

    @Override
    public void removeRow(int i) {
        removeAt(i);
    }

    public boolean isEmpty() {
        return panels.isEmpty();
    }

    public Entry[] getEntries() {
        return panels.toArray(new Entry[0]);
    }

    public Entry getEntry(int index) {
        return panels.get(index);
    }

    public Entry add() {
        final Entry entry = new Entry(module);
        entry.setName(ensureUniqueName("Target"));
        this.add(entry);
        return entry;
    }

    public void add(@NotNull Entry entry) {
        entry.setName(ensureUniqueName(entry.getName()));
        panels.add(entry);
        fireTableRowsInserted(panels.size() - 1, panels.size() - 1);
        fireTableDataChanged();
    }

    public void remove(Entry entry) {
        int i;
        while ((i = panels.indexOf(entry)) != -1) {
            panels.remove(i);
            fireTableRowsDeleted(i, i);
        }
        fireTableDataChanged();
    }

    public Entry removeAt(int row) {
        if (row >= 0 && row <= panels.size()) {
            Entry result = panels.remove(row);
            fireTableRowsDeleted(row, row);
            fireTableDataChanged();
            return result;
        }
        return null;
    }

    public void removeAll() {
        int last = panels.size() - 1;
        panels.clear();
        if (last >= 0) {
            fireTableRowsDeleted(0, last);
            fireTableDataChanged();
        }
    }

    public static class Entry {
        private final String id;
        private final PureBasicTargetSettingsPanel panel;
        private String name;

        public Entry(@Nullable Module module) {
            this(module, new PureBasicTargetSettings());
        }

        public Entry(@Nullable Module module, PureBasicTargetSettings originalSettings) {
            this(originalSettings, new PureBasicTargetSettingsPanel(module));
        }

        public Entry(@NotNull PureBasicTargetSettings originalSettings, PureBasicTargetSettingsPanel panel) {
            this.id = UUID.randomUUID().toString();
            this.name = originalSettings.getName();
            this.panel = panel;
            this.panel.setData(originalSettings);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PureBasicTargetSettingsPanel getPanel() {
            return panel;
        }
    }
}
