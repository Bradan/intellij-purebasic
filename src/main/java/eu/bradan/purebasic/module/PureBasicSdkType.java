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

package eu.bradan.purebasic.module;

import com.intellij.openapi.projectRoots.*;
import eu.bradan.purebasic.PureBasicIcons;
import eu.bradan.purebasic.builder.PureBasicCompiler;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PureBasicSdkType extends SdkType {
    public PureBasicSdkType() {
        super("PureBasic");
    }

    @NotNull
    public static SdkTypeId getInstance() {
        return SdkType.findInstance(PureBasicSdkType.class);
    }

    @Override
    public Icon getIcon() {
        return PureBasicIcons.PUREBASIC;
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        return System.getenv().get("PUREBASIC_HOME");
    }

    @Override
    public boolean isValidSdkHome(String path) {
        return PureBasicCompiler.getOrLoadCompilerByHome(path) != null;
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        final PureBasicCompiler compiler = PureBasicCompiler.getOrLoadCompilerByHome(sdkHome);
        if (compiler != null) {
            return compiler.getVersionString();
        }
        return null;
    }

    @NotNull
    @Override
    public String suggestSdkName(@Nullable String currentSdkName, String sdkHome) {
        return "PureBasic";
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "PureBasic";
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        SdkModificator modificator = sdk.getSdkModificator();
        modificator.setVersionString(getVersionString(sdk));
        modificator.commitChanges();
    }
}
