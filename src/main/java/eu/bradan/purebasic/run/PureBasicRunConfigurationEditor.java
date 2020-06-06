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

package eu.bradan.purebasic.run;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.fields.ExpandableTextField;
import eu.bradan.purebasic.module.PureBasicModuleSettings;
import eu.bradan.purebasic.module.PureBasicModuleType;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import eu.bradan.purebasic.ui.ModuleListCellRenderer;
import eu.bradan.purebasic.ui.TargetListCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PureBasicRunConfigurationEditor extends SettingsEditor<PureBasicRunConfiguration> {
    private JPanel panel;
    private JComboBox<Module> comboBoxModule;
    private JComboBox<PureBasicTargetSettings> comboBoxTarget;
    private TextFieldWithBrowseButton textFieldWorkingDirectory;
    private ExpandableTextField textFieldArguments;

    public PureBasicRunConfigurationEditor() {
        super();

        comboBoxModule.addActionListener(e -> {
            comboBoxTarget.removeAllItems();
            final Module m = (Module) comboBoxModule.getSelectedItem();
            if (m != null) {
                final PureBasicModuleSettings settings = m.getService(PureBasicModuleSettings.class);
                for (PureBasicTargetSettings target : settings.getState().getTargetOptions()) {
                    comboBoxTarget.addItem(target);
                }
            }
        });

        textFieldWorkingDirectory.addBrowseFolderListener(new TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFolderDescriptor()));
    }

    public PureBasicRunConfigurationEditor(Project project) {
        this();
        createUIComponents();

        if (project != null) {
            for (Module m : ModuleManager.getInstance(project).getModules()) {
                if (PureBasicModuleType.ID.equals(m.getModuleTypeName())) {
                    comboBoxModule.addItem(m);
                }
            }
        }
    }

    @Override
    protected void resetEditorFrom(@NotNull PureBasicRunConfiguration pureBasicRunConfiguration) {
        setData(pureBasicRunConfiguration);
    }

    @Override
    protected void applyEditorTo(@NotNull PureBasicRunConfiguration pureBasicRunConfiguration) throws ConfigurationException {
        getData(pureBasicRunConfiguration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    private void createUIComponents() {
        comboBoxModule.setRenderer(new ModuleListCellRenderer());
        comboBoxTarget.setRenderer(new TargetListCellRenderer());
    }

    public void setData(@NotNull PureBasicRunConfiguration data) {
        comboBoxModule.setSelectedItem(data.getModule());
        comboBoxTarget.setSelectedItem(data.getTarget());
        textFieldArguments.setText(data.getArguments());
        textFieldWorkingDirectory.setText(data.getWorkingDirectory());
    }

    public void getData(@NotNull PureBasicRunConfiguration data) {
        data.setModule((Module) comboBoxModule.getSelectedItem());
        data.setTarget((PureBasicTargetSettings) comboBoxTarget.getSelectedItem());
        data.setArguments(textFieldArguments.getText());
        data.setWorkingDirectory(textFieldWorkingDirectory.getText());
    }

    public boolean isModified(PureBasicRunConfiguration data) {
        if (comboBoxModule.getSelectedItem() != null ? comboBoxModule.getSelectedItem() != data.getModule() : data.getModule() != null)
            return true;
        if (comboBoxTarget.getSelectedItem() != null ? comboBoxTarget.getSelectedItem() != data.getTarget() : data.getTarget() != null)
            return true;
        if (!textFieldArguments.getText().equals(data.getArguments()))
            return true;
        if (!textFieldWorkingDirectory.getText().equals(data.getWorkingDirectory()))
            return true;
        return false;
    }
}
