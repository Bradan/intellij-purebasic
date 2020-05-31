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
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.ide.IdeBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PureBasicRunProfileState extends CommandLineState {
    protected PureBasicRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        PureBasicRunConfiguration runProfile = (PureBasicRunConfiguration) getEnvironment().getRunProfile();
        final boolean debug = DefaultDebugExecutor.EXECUTOR_ID.equals(getEnvironment().getExecutor().getId());
        GeneralCommandLine commandLine = runProfile.getCommandLine(debug);

        KillableColoredProcessHandler processHandler = new KillableColoredProcessHandler(commandLine) {
            @Override
            protected void notifyProcessTerminated(int exitCode) {
                print(IdeBundle.message("run.anything.console.process.finished", exitCode), ConsoleViewContentType.SYSTEM_OUTPUT);
                super.notifyProcessTerminated(exitCode);
            }

            @Override
            public final boolean shouldKillProcessSoftly() {
                return super.shouldKillProcessSoftly();
            }

            private void print(@NotNull String message, @NotNull ConsoleViewContentType consoleViewContentType) {
                ConsoleView console = getConsoleView();
                if (console != null) console.print(message, consoleViewContentType);
            }

            @Nullable
            public ConsoleView getConsoleView() {
                RunContentDescriptor contentDescriptor = RunContentManager.getInstance(getEnvironment().getProject())
                        .findContentDescriptor(getEnvironment().getExecutor(), this);

                ConsoleView console = null;
                if (contentDescriptor != null && contentDescriptor.getExecutionConsole() instanceof ConsoleView) {
                    console = (ConsoleView) contentDescriptor.getExecutionConsole();
                }
                return console;
            }
        };

        processHandler.setHasPty(true);

        return processHandler;
    }
}
