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

package eu.bradan.purebasic.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import eu.bradan.purebasic.PureBasicLanguage;
import eu.bradan.purebasic.builder.PureBasicCompiler;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import eu.bradan.purebasic.run.PureBasicRunConfiguration;
import eu.bradan.purebasic.settings.PureBasicCompilerSettings;
import eu.bradan.purebasic.structure.PureBasicStructureViewElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PureBasicCompletionContributor extends CompletionContributor {

    private final HashMap<PureBasicCompiler, ArrayList<String>> predefinedEntries = new HashMap<>();

    public PureBasicCompletionContributor() {
        // ensure the compilers are all loaded
        ServiceManager.getService(PureBasicCompilerSettings.class).getState().getSdks();

        CompletionProvider<CompletionParameters> completionProvider = new CompletionProvider<CompletionParameters>() {
            public void addCompletions(@NotNull CompletionParameters parameters,
                                       @NotNull ProcessingContext context,
                                       @NotNull CompletionResultSet resultSet) {
                Project project = parameters.getOriginalFile().getProject();
                Module module = ModuleUtil.findModuleForFile(parameters.getOriginalFile().getVirtualFile(), project);

                PureBasicCompiler compiler = null;

                RunnerAndConfigurationSettings selectedConfiguration = RunManager.getInstance(project).getSelectedConfiguration();
                if (selectedConfiguration != null) {
                    RunConfiguration runConfig = selectedConfiguration.getConfiguration();
                    if (runConfig instanceof PureBasicRunConfiguration) {
                        PureBasicRunConfiguration pureBasicRunConfig = (PureBasicRunConfiguration) runConfig;
                        PureBasicTargetSettings target = pureBasicRunConfig.getTarget();
                        if (target != null) {
                            compiler = target.getSdk();
                        }
                    }
                }

                if (compiler == null) {
                    compiler = PureBasicCompiler.getDefaultCompiler();
                }

                if (compiler != null) {
                    for (String name : getPredefinedEntries(compiler)) {
                        resultSet.addElement(LookupElementBuilder.create(name));
                    }
                }

                PureBasicStructureViewElement element = new PureBasicStructureViewElement(parameters.getOriginalFile());
                for (TreeElement e : element.getChildren()) {
                    if (e.getPresentation().getPresentableText() != null) {
                        resultSet.addElement(LookupElementBuilder.create(e.getPresentation().getPresentableText()));
                    }
                }
            }
        };
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(PureBasicLanguage.INSTANCE),
                completionProvider
        );
    }

    private ArrayList<String> getPredefinedEntries(PureBasicCompiler compiler) {
        if (predefinedEntries.containsKey(compiler)) {
            return predefinedEntries.get(compiler);
        }

        final ArrayList<String> entries = new ArrayList<>();
        compiler.getSdkDeclarations(new PureBasicCompiler.DeclarationsCollector() {
            @Override
            public void declareConstant(int type, String name, String value) {
                entries.add("#" + name);
            }

            @Override
            public void declareStructure(String name, List<String> content) {
                entries.add(name);
            }

            @Override
            public void declareInterface(String name, List<String> content) {
                entries.add(name);
            }

            @Override
            public void declareFunction(String name, String args, String description) {
                entries.add(name);
            }
        });

        predefinedEntries.put(compiler, entries);
        return entries;
    }
}
