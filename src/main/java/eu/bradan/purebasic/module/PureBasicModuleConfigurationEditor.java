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
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicModuleConfigurationEditor implements ModuleConfigurationEditor {
    private PureBasicModuleSettingsPanel settingsPanel;
    private Module module;

    public PureBasicModuleConfigurationEditor(@NotNull Module module) {
        this.module = module;
        settingsPanel = null;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "PureBasic";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        final PureBasicModuleSettings settings = module.getService(PureBasicModuleSettings.class);
        if (settingsPanel == null) {
            settingsPanel = new PureBasicModuleSettingsPanel(settings.getState());
        }
        return settingsPanel.getRoot();
    }

    @Override
    public boolean isModified() {
        if (settingsPanel == null) {
            return false;
        }
        final PureBasicModuleSettings settings = module.getService(PureBasicModuleSettings.class);
        final PureBasicModuleSettingsState modifiedState = settingsPanel.getState();
        return !modifiedState.equals(settings.getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel == null) {
            return;
        }
        final PureBasicModuleSettings settings = module.getService(PureBasicModuleSettings.class);
        final PureBasicModuleSettingsState modifiedState = settingsPanel.getState();
        settings.loadState(modifiedState);
    }

    @Override
    public void saveData() {
        try {
            apply();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void moduleStateChanged() {
    }
}
