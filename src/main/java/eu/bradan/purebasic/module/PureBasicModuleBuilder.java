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

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PureBasicModuleBuilder extends ModuleBuilder implements ModuleBuilderListener {
    PureBasicModuleSettingsState moduleSettings = null;

    public PureBasicModuleBuilder() {
        super();
        addListener(this);
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        super.setupRootModel(modifiableRootModel);

        doAddContentEntry(modifiableRootModel);
    }

    @Override
    public ModuleType<PureBasicModuleBuilder> getModuleType() {
        return PureBasicModuleType.getInstance();
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{new ModuleWizardStep() {
            final PureBasicModuleSettingsPanel settingsPanel = new PureBasicModuleSettingsPanel(null);

            @Override
            public JComponent getComponent() {
                return settingsPanel.getRoot();
            }

            @Override
            public void updateDataModel() {
                moduleSettings = new PureBasicModuleSettingsState();
                settingsPanel.getData(moduleSettings);
            }
        }};
    }

    @Override
    public void moduleCreated(@NotNull Module module) {
        if (moduleSettings != null) {
            final PureBasicModuleSettings settings = module.getService(PureBasicModuleSettings.class);
            settings.loadState(moduleSettings);
            moduleSettings = null;
        }
    }
}
