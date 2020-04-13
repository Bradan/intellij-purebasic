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
import org.jetbrains.jps.builders.BuildRootIndex;
import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.builders.BuildTargetRegistry;
import org.jetbrains.jps.builders.TargetOutputIndex;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PureBasicBuildTarget extends BuildTarget<PureBasicBuildRootDescriptor> {
    private static final Logger LOG = Logger.getInstance(PureBasicBuildTarget.class);

    private JpsModule module;

    protected PureBasicBuildTarget(JpsModule module) {
        super(PureBasicBuildTargetType.getInstance());
        this.module = module;
    }

    @Override
    public String getId() {
        return this.module.getName();
    }

    @NotNull
    @Override
    public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry buildTargetRegistry,
                                                          TargetOutputIndex targetOutputIndex) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<PureBasicBuildRootDescriptor> computeRootDescriptors(@NotNull JpsModel jpsModel,
                                                                     ModuleExcludeIndex moduleExcludeIndex,
                                                                     IgnoredFileIndex ignoredFileIndex,
                                                                     BuildDataPaths buildDataPaths) {
        return Collections.emptyList();
    }

    @Override
    public PureBasicBuildRootDescriptor findRootDescriptor(String rootId, BuildRootIndex buildRootIndex) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "PureBasic '" + getId() + "' Target";
    }

    @NotNull
    @Override
    public Collection<File> getOutputRoots(CompileContext compileContext) {
        return Collections.emptyList();
    }

    public JpsModule getModule() {
        return this.module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PureBasicBuildTarget)) return false;

        PureBasicBuildTarget that = (PureBasicBuildTarget) o;

        return module != null ? module.equals(that.module) : that.module == null;
    }

    @Override
    public int hashCode() {
        return module != null ? module.hashCode() : 0;
    }
}
