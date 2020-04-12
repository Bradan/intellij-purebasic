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

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicCompilerSettingsConfigurable extends ConfigurableProvider implements Configurable {

    private PureBasicCompilerSettingsPanel settingsPanel;

    public PureBasicCompilerSettingsConfigurable() {
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
        final PureBasicCompilerSettings settings = ServiceManager.getService(PureBasicCompilerSettings.class);
        if (settingsPanel == null) {
            settingsPanel = new PureBasicCompilerSettingsPanel(settings.getState());
        }
        return settingsPanel.getRoot();
    }

    @Override
    public boolean isModified() {
        if (settingsPanel == null) {
            return false;
        }
        final PureBasicCompilerSettings settings = ServiceManager.getService(PureBasicCompilerSettings.class);
        return !settingsPanel.getState().equals(settings.getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel == null) {
            return;
        }
        final PureBasicCompilerSettings settings = ServiceManager.getService(PureBasicCompilerSettings.class);
        final PureBasicCompilerSettingsState modifiedState = settingsPanel.getState();
        settings.loadState(modifiedState);
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new PureBasicCompilerSettingsConfigurable();
    }

}
