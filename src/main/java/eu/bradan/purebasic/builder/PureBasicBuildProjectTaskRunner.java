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

package eu.bradan.purebasic.builder;

import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.task.ModuleBuildTask;
import com.intellij.task.ProjectTask;
import com.intellij.task.ProjectTaskContext;
import com.intellij.task.ProjectTaskRunner;
import com.intellij.ui.content.Content;
import eu.bradan.purebasic.module.PureBasicModuleSettings;
import eu.bradan.purebasic.module.PureBasicModuleType;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class PureBasicBuildProjectTaskRunner extends ProjectTaskRunner {

    @NotNull
    private final ResourceBundle resources = ResourceBundle.getBundle("texts/texts");

    private boolean compileAllTargets(@NotNull Module module, PureBasicBuildToolWindow toolWindow) {
        final PureBasicModuleSettings settings = module.getService(PureBasicModuleSettings.class);
        final VirtualFile root = ModuleRootManager.getInstance(module).getContentRoots()[0];
        final String rootPath = root.getCanonicalPath();

        if (rootPath == null) {
            toolWindow.addLine(String.format(resources.getString("invalidRootPath"), module.getName()));
            return false;
        }

        final boolean[] result = {true};
        for (PureBasicTargetSettings target : settings.getState().getTargetOptions()) {
            PureBasicCompiler sdk = target.getSdk();
            if (sdk == null) {
                // just continue, there might be targets that are only available on a different OS
                continue;
            }
            toolWindow.addLine(String.format(resources.getString("compilingModule"), module.getName()));
            toolWindow.addLine("");
            try {
                sdk.compile(target, rootPath, msg -> {
                    toolWindow.addElement(msg);
                    result[0] &= msg.type != PureBasicCompiler.CompileMessageType.ERROR;
                });
                toolWindow.addLine("");
                toolWindow.addLine("");
            } catch (IOException e) {
                toolWindow.addLine(e.getMessage());
            }
        }
        return result[0];
    }

    @Override
    public boolean canRun(@NotNull ProjectTask projectTask) {
        if (projectTask instanceof ModuleBuildTask) {
            final ModuleBuildTask moduleBuildTask = (ModuleBuildTask) projectTask;
            return PureBasicModuleType.ID.equals(moduleBuildTask.getModule().getModuleTypeName());
        }
        return false;
    }

    @Override
    public Promise<Result> run(@NotNull Project project, @NotNull ProjectTaskContext context, @NotNull ProjectTask... tasks) {
        final ToolWindow toolWindow = ToolWindowManager.getInstance(project)
                .getToolWindow("PureBasic Build");
        if (toolWindow == null) {
            return null;
        }
        final Content buildToolWindowContent = toolWindow.getContentManager().getContent(0);
        if (buildToolWindowContent == null) {
            return null;
        }
        final PureBasicBuildToolWindow buildToolWindow = (PureBasicBuildToolWindow) buildToolWindowContent.getComponent();
        buildToolWindow.clear();

        final AsyncPromise<Result> result = new AsyncPromise<>();
        JobScheduler.getScheduler().schedule(() -> {
            boolean success = true;
            for (ModuleBuildTask task : Arrays.stream(tasks)
                    .filter(t -> t instanceof ModuleBuildTask)
                    .toArray(ModuleBuildTask[]::new)) {
                success &= compileAllTargets(task.getModule(), buildToolWindow);
            }
            boolean finalSuccess = success;
            result.setResult(new Result() {
                @Override
                public boolean isAborted() {
                    return false;
                }

                @Override
                public boolean hasErrors() {
                    return finalSuccess;
                }
            });
        }, 1, TimeUnit.SECONDS);
        return result;
    }
}
