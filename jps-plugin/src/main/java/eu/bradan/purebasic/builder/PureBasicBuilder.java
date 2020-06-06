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

import com.intellij.openapi.diagnostic.Logger;
import eu.bradan.purebasic.model.JpsPureBasicModuleElement;
import eu.bradan.purebasic.module.PureBasicModuleSettingsState;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.IOException;
import java.util.Collections;

public class PureBasicBuilder extends TargetBuilder<PureBasicBuildRootDescriptor, PureBasicBuildTarget> {
    private static final Logger LOG = Logger.getInstance(PureBasicBuilder.class);

    protected PureBasicBuilder() {
        super(Collections.singletonList(PureBasicBuildTargetType.getInstance()));
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "PureBasic";
    }

    @Override
    public void build(@NotNull PureBasicBuildTarget target,
                      @NotNull DirtyFilesHolder<PureBasicBuildRootDescriptor, PureBasicBuildTarget> holder,
                      @NotNull BuildOutputConsumer outputConsumer,
                      @NotNull CompileContext context) {
        JpsModule module = target.getModule();
        JpsPureBasicModuleElement element = (JpsPureBasicModuleElement) module.getProperties();
        PureBasicModuleSettingsState settings = element.getSettings();

        PureBasicCompiler.CompileMessageLogger logger = new PureBasicCompiler.CompileMessageLogger() {
            @Override
            public void logInfo(String message, String file, int line) {
                if (file != null) {
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.PROGRESS, message, file, line,
                            -1L, -1L, line, -1L));
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.INFO, message, file, line,
                            -1L, -1L, line, -1L));
                } else {
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.PROGRESS, message));
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.INFO, message));
                }
            }

            @Override
            public void logError(String message, String file, int line) {
                if (file != null) {
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.PROGRESS, message, file, line,
                            -1L, -1L, line, -1L));
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.ERROR, message, file, line,
                            -1L, -1L, line, -1L));
                } else {
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.PROGRESS, message));
                    context.processMessage(new CompilerMessage("PureBasic",
                            BuildMessage.Kind.ERROR, message));
                }
            }
        };

        final String contentRoot = module.getContentRootsList().getUrls().get(0);
        for (PureBasicTargetSettings targetSettings : settings.getTargetOptions()) {
            PureBasicCompiler compiler = targetSettings.getSdk();
            if (compiler != null) {
                try {
                    int exitCode = compiler.compile(targetSettings, contentRoot, logger);

                    if (exitCode != 0) {
                        context.processMessage(new CompilerMessage("PureBasic",
                                BuildMessage.Kind.ERROR, "Exit code: " + exitCode));
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }
}
