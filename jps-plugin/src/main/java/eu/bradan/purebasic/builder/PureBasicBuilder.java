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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.cmdline.BuildRunner;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;

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
        LOG.info("Listing content roots");
        for (String contentRoot : module.getContentRootsList().getUrls()) {
            LOG.info("Content Root: " + contentRoot);
        }
        LOG.info("Listing source roots");
        for (JpsModuleSourceRoot sr : module.getSourceRoots()) {
            LOG.info("Source Root: " + sr.getFile().getAbsolutePath());
        }
    }
}
