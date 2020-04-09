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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.model.JpsModel;

import java.util.Collections;
import java.util.List;

public class PureBasicBuildTargetType extends BuildTargetType<PureBasicBuildTarget> {
    private static PureBasicBuildTargetType instance = null;

    protected PureBasicBuildTargetType() {
        super("PureBasic");
    }

    public static PureBasicBuildTargetType getInstance() {
        if (instance == null) {
            instance = new PureBasicBuildTargetType();
        }
        return instance;
    }

    @NotNull
    @Override
    public List<PureBasicBuildTarget> computeAllTargets(@NotNull JpsModel jpsModel) {
        System.out.println("computeAllTargets");
        return Collections.singletonList(new PureBasicBuildTarget());
    }

    @NotNull
    @Override
    public BuildTargetLoader<PureBasicBuildTarget> createLoader(@NotNull JpsModel jpsModel) {
        System.out.println("createLoader");
        return new BuildTargetLoader<PureBasicBuildTarget>() {
            @Override
            public PureBasicBuildTarget createTarget(@NotNull String s) {
                System.out.println("createTarget " + s);
                return null;
            }
        };
    }
}
