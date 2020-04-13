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
import eu.bradan.purebasic.model.JpsPureBasicCompilers;
import eu.bradan.purebasic.model.JpsPureBasicModuleElement;
import eu.bradan.purebasic.module.PureBasicModuleSettingsState;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.BufferedReader;
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
                      @NotNull CompileContext context) throws ProjectBuildException, IOException {
        JpsModule module = target.getModule();
        JpsPureBasicModuleElement element = (JpsPureBasicModuleElement) module.getProperties();
        PureBasicModuleSettingsState settings = element.getSettings();

        final String contentRoot = module.getContentRootsList().getUrls().get(0);
        LOG.info("Content root: " + contentRoot);
        for (PureBasicTargetSettings targetSettings : settings.targetOptions) {
            PureBasicCompiler compiler = JpsPureBasicCompilers.INSTANCE.getCompilerByVersion(targetSettings.sdk);
            if (compiler != null) {
                try {
                    BufferedReader reader = compiler.compile(targetSettings, contentRoot);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        context.processMessage(new CompilerMessage("PureBasic",
                                BuildMessage.Kind.PROGRESS, line));
                        context.processMessage(new CompilerMessage("PureBasic",
                                BuildMessage.Kind.INFO, line));
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }
}
