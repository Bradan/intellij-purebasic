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

import com.intellij.compiler.impl.BuildTargetScopeProvider;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.api.CmdlineProtoUtil;
import org.jetbrains.jps.api.CmdlineRemoteProto.Message.ControllerMessage.ParametersMessage.TargetTypeBuildScope;

import java.util.LinkedList;
import java.util.List;

public class PureBasicBuildTargetScopeProvider extends BuildTargetScopeProvider {
    private static final String MODULE_TYPE_ID = "PUREBASIC_MODULE";

    @NotNull
    @Override
    public List<TargetTypeBuildScope> getBuildTargetScopes(@NotNull CompileScope baseScope, @NotNull Project project, boolean forceBuild) {
        System.out.println("getBuildTargetScopes");
        LinkedList<TargetTypeBuildScope> result = new LinkedList<>(super.getBuildTargetScopes(baseScope, project, forceBuild));

        LinkedList<String> moduleNames = new LinkedList<>();
        for (Module m : baseScope.getAffectedModules()) {
            if (MODULE_TYPE_ID.equals(m.getModuleTypeName())) {
                System.out.println(m.getName());
                moduleNames.add(m.getName());
            }
        }

        result.add(CmdlineProtoUtil.createTargetsScope(
                PureBasicBuildTargetType.getInstance().getTypeId(),
                moduleNames,
                forceBuild));
        return result;
    }
}
