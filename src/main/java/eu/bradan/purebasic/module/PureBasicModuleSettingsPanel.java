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
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PureBasicModuleSettingsPanel {
    private JPanel root;
    private final CardLayout cardLayout;
    private final TargetPanelsModel targetsModel;
    private final Module module;
    private JBTable listTargets;
    private JPanel panelTargets;
    private JBSplitter splitter;

    public PureBasicModuleSettingsPanel(@Nullable Module module) {
        this.module = module;

        cardLayout = (CardLayout) panelTargets.getLayout();

        targetsModel = new TargetPanelsModel(module);

        listTargets.setModel(targetsModel);
        listTargets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTargets.getSelectionModel().addListSelectionListener(e -> {
            int selected = listTargets.getSelectedRow();
            if (selected != -1) {
                final TargetPanelsModel.Entry entry = targetsModel.getEntry(selected);
                if (entry.getPanel().getParent() != panelTargets) {
                    panelTargets.add(entry.getPanel(), entry.getId());
                }
                cardLayout.show(panelTargets, entry.getId());
            }
        });

        listTargets.getEmptyText().setText("-");

        final ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(listTargets);
        toolbarDecorator.setAddAction(anActionButton -> {
            // add an entry
            final TargetPanelsModel.Entry entry = targetsModel.add();
            if (entry.getPanel().getParent() != panelTargets) {
                panelTargets.add(entry.getPanel(), entry.getId());
            }

            final int index = targetsModel.getRowCount() - 1;
            listTargets.getSelectionModel().setSelectionInterval(index, index);
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            int row = listTargets.getSelectedRow();
            if (row != -1) {
                panelTargets.remove(targetsModel.removeAt(row).getPanel());
            }
        });
        toolbarDecorator.setEditAction(anActionButton -> {
            int row = listTargets.getSelectedRow();
            if (row != -1) {
                listTargets.editCellAt(row, 0);
            }
        });
        JPanel tbdPanel = toolbarDecorator.createPanel();
        splitter.setFirstComponent(tbdPanel);
        splitter.setSecondComponent(panelTargets);
    }

    public JPanel getRoot() {
        return root;
    }

    public void setData(@NotNull PureBasicModuleSettingsState data) {
        targetsModel.removeAll();
        panelTargets.removeAll();

        for (PureBasicTargetSettings targetSettings : data.getTargetOptions()) {
            final TargetPanelsModel.Entry entry = new TargetPanelsModel.Entry(module, targetSettings);
            panelTargets.add(entry.getPanel(), entry.getId());
            targetsModel.add(entry);
        }
        if (!targetsModel.isEmpty()) {
            final TargetPanelsModel.Entry entry = targetsModel.getEntry(0);
            listTargets.getSelectionModel().setSelectionInterval(0, 0);
            cardLayout.show(panelTargets, entry.getId());
        }
    }

    public void getData(@NotNull PureBasicModuleSettingsState data) {
        data.getTargetOptions().clear();
        for (TargetPanelsModel.Entry entry : targetsModel.getEntries()) {
            PureBasicTargetSettings settings = new PureBasicTargetSettings(entry.getName());
            entry.getPanel().getData(settings);
            data.getTargetOptions().add(settings);
        }
    }

    public boolean isModified(@NotNull PureBasicModuleSettingsState data) {
        if (data.getTargetOptions().size() != targetsModel.getEntries().length) {
            // amount of target options has changed
            return true;
        }
        for (TargetPanelsModel.Entry entry : targetsModel.getEntries()) {
            PureBasicTargetSettings settings = data.getTargetOptions().stream()
                    .filter(t -> Objects.equals(t.getName(), entry.getName()))
                    .findFirst()
                    .orElse(null);
            if (settings == null) {
                // a new target option or name has changed
                return true;
            } else if (entry.getPanel().isModified(settings)) {
                // content has changed
                return true;
            }
        }
        return false;
    }

    private void createUIComponents() {
        splitter = new JBSplitter(false, "PureBasicModuleSettingsTargets", 0.2f);
    }
}
