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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PureBasicModuleSettingsPanel {
    private JPanel root;
    private JButton buttonAdd;
    private JButton buttonRemove;
    private JTable listTargets;
    private PureBasicTargetSettingsPanel panelTargetOptions;

    private PureBasicModuleSettings.State state;

    private int editingTarget;

    public PureBasicModuleSettingsPanel(PureBasicModuleSettings.State state) {
        this.editingTarget = -1;

        if (state != null) {
            try {
                this.state = (PureBasicModuleSettings.State) state.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        } else {
            this.state = new PureBasicModuleSettings.State();
        }

        final DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(null, new String[]{""});
        listTargets.setModel(dm);
        listTargets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTargets.getSelectionModel().addListSelectionListener(e -> {
            int selected = listTargets.getSelectedRow();
            if (selected == -1) {
                panelTargetOptions.setEnabled(false);
                stopEditTarget();
            } else {
                panelTargetOptions.setEnabled(true);
                startEditTarget(selected);
            }
        });

        buttonAdd.addActionListener(e -> {
            final PureBasicTargetSettings ts = new PureBasicTargetSettings();
            ts.name = "Target";
            this.state.targetOptions.add(ts);
            dm.addRow(new String[]{ts.name});
        });
        buttonRemove.addActionListener(e -> {
            int row = listTargets.getSelectedRow();
            if (row != -1) {
                this.state.targetOptions.remove(row);
                dm.removeRow(row);
            }
        });

        panelTargetOptions.setEnabled(false);
        for (PureBasicTargetSettings targetSettings : this.state.targetOptions) {
            dm.addRow(new String[]{targetSettings.name});
        }
        if (!this.state.targetOptions.isEmpty()) {
            panelTargetOptions.setEnabled(true);
            listTargets.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    private void stopEditTarget() {
        panelTargetOptions.setEnabled(false);
        this.editingTarget = -1;
    }

    private void startEditTarget(int index) {
        stopEditTarget();
        if (index >= 0) {
            panelTargetOptions.setEnabled(true);
            panelTargetOptions.setTargetSettings(state.targetOptions.get(index));
        }
        this.editingTarget = index;
    }

    public JPanel getRoot() {
        return root;
    }

    public PureBasicModuleSettings.State getState() {
        return this.state;
    }
}
