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
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.RunProfileStarter;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;

public class PureBasicProgramRunner implements ProgramRunner<RunnerSettings> {
    public static final String RUNNER_ID = "PUREBASIC_RUNNER";

    @NotNull
    @Override
    public String getRunnerId() {
        return RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile runProfile) {
        return runProfile instanceof PureBasicRunConfiguration
                && (DefaultRunExecutor.EXECUTOR_ID.equals(executorId) || DefaultDebugExecutor.EXECUTOR_ID.equals(executorId));
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        RunProfileState state = executionEnvironment.getRunProfile()
                .getState(executionEnvironment.getExecutor(), executionEnvironment);

        if (state != null) {
            ExecutionManager.getInstance(executionEnvironment.getProject()).startRunProfile(new RunProfileStarter() {
                @NotNull
                @Override
                public Promise<RunContentDescriptor> executeAsync(@NotNull ExecutionEnvironment environment) throws ExecutionException {
                    return Promises.resolvedPromise(doExecute(executionEnvironment, state));
                }
            }, executionEnvironment);
        }
    }

    @Nullable
    private RunContentDescriptor doExecute(@NotNull ExecutionEnvironment executionEnvironment, @NotNull RunProfileState state) {
        try {
            ExecutionResult executionResult = state.execute(executionEnvironment.getExecutor(), this);
            if (executionResult != null) {
                return new RunContentBuilder(executionResult, executionEnvironment)
                        .showRunContent(executionEnvironment.getContentToReuse());
            }
        } catch (ExecutionException ignored) {
        }

        return null;
    }
}
