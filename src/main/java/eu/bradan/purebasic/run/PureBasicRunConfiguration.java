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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import eu.bradan.purebasic.builder.PureBasicCompiler;
import eu.bradan.purebasic.module.PureBasicModuleSettings;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import eu.bradan.purebasic.settings.PureBasicCompilerSettings;
import eu.bradan.purebasic.settings.PureBasicCompilerSettingsState;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;
import java.util.Objects;

public class PureBasicRunConfiguration extends RunConfigurationBase<PureBasicRunProfileState> {
    private Module module;

    @OptionTag
    private String moduleId; // for serialization purposes only

    @OptionTag
    private String target;
    private String arguments;
    private String workingDirectory;

    protected PureBasicRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
        reset();

        setShowConsoleOnStdOut(true);
        setShowConsoleOnStdErr(true);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new PureBasicRunConfigurationEditor(this.getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new PureBasicRunProfileState(executionEnvironment);
    }

    @Override
    public void checkSettingsBeforeRun() throws RuntimeConfigurationException {
        super.checkSettingsBeforeRun();
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        moduleId = module != null ? module.getName() : null;

        super.writeExternal(element);

        XmlSerializer.serializeInto(this, element);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);

        XmlSerializer.deserializeInto(this, element);

        Module[] modules = ModuleManager.getInstance(getProject()).getModules();

        this.module = null;
        if (modules.length > 0) {
            this.module = modules[0];
        }
        for (Module module : modules) {
            if (Objects.equals(moduleId, module.getName())) {
                this.module = module;
                break;
            }
        }
    }

    private void reset() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();

        this.module = null;
        this.target = null;
        if (modules.length > 0) {
            this.module = modules[0];
            final PureBasicModuleSettings settings = this.module.getService(PureBasicModuleSettings.class);
            final LinkedList<PureBasicTargetSettings> targets = settings.getState().getTargetOptions();
            this.target = !targets.isEmpty() ? targets.getFirst().getName() : null;
        }
    }

    public GeneralCommandLine getCommandLine(boolean standaloneDebugger) {
        final PureBasicTargetSettings target = getTarget();

        File executable = null;
        if (module != null && target != null) {
            executable = new File(new File(module.getModuleFilePath()).getParentFile(), target.getOutputFile());
        }

        if (executable == null) {
            return null;
        }

        LinkedList<String> command = new LinkedList<>();

        if (standaloneDebugger) {
            final PureBasicCompilerSettings settings = ServiceManager.getService(PureBasicCompilerSettings.class);
            final PureBasicCompilerSettingsState state = settings.getState();
            for (PureBasicCompiler compiler : state.getSdks()) {
                if (compiler != null && target.getSdk() == compiler) {
                    File debugger = new File(new File(compiler.getSdkHome(), "compilers"), "pbdebugger");
                    if (debugger.exists()) {
                        command.add(debugger.getAbsolutePath());
                    }
                    break;
                }
            }
        }

        command.add(executable.getAbsolutePath());
        CommandLineTokenizer tokenizer = new CommandLineTokenizer(arguments);
        while (tokenizer.hasMoreTokens()) {
            command.add(tokenizer.nextToken());
        }

        GeneralCommandLine commandLine = new GeneralCommandLine(command);
        if (workingDirectory != null && !workingDirectory.isEmpty()) {
            final File workDir = new File(executable.getParentFile(), workingDirectory);
            commandLine.setWorkDirectory(workDir);
        } else {
            commandLine.setWorkDirectory(executable.getParentFile());
        }

        return commandLine;
    }

    @Transient
    public Module getModule() {
        return module;
    }

    public void setModule(final Module module) {
        this.module = module;
    }

    @Transient
    public PureBasicTargetSettings getTarget() {
        if (module == null) {
            return null;
        }

        final PureBasicModuleSettings settings = this.module.getService(PureBasicModuleSettings.class);

        return settings.getState().getTargetOptions().stream()
                .filter(t -> Objects.equals(target, t.getName()))
                .findFirst()
                .orElse(null);
    }

    public void setTarget(PureBasicTargetSettings target) {
        this.target = target != null ? target.getName() : null;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
