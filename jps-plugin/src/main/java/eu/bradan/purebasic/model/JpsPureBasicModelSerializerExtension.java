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

package eu.bradan.purebasic.model;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.XmlSerializer;
import eu.bradan.purebasic.module.PureBasicModuleSettingsState;
import eu.bradan.purebasic.settings.PureBasicCompilerSettingsState;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;
import org.jetbrains.jps.model.serialization.facet.JpsFacetConfigurationSerializer;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModuleSourceRootDummyPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModuleSourceRootPropertiesSerializer;

import java.util.Collections;
import java.util.List;

public class JpsPureBasicModelSerializerExtension extends JpsModelSerializerExtension {
    private static final Logger LOG = Logger.getInstance(JpsPureBasicModelSerializerExtension.class);

    public static final String MODULE_TYPE = "PUREBASIC_MODULE";
    public static final String MODULE_COMPONENT_NAME = "PureBasicModule";
    public static final String SOURCE_ROOT = "PUREBASIC_SOURCE";

    public JpsPureBasicModelSerializerExtension() {
        super();
    }

    @NotNull
    @Override
    public List<? extends JpsModuleSourceRootPropertiesSerializer<?>> getModuleSourceRootPropertiesSerializers() {
        return Collections.singletonList(new JpsModuleSourceRootDummyPropertiesSerializer(
                PureBasicSourceRootType.INSTANCE, SOURCE_ROOT));
    }

    @NotNull
    @Override
    public List<? extends JpsModulePropertiesSerializer<?>> getModulePropertiesSerializers() {
        return Collections.singletonList(new JpsModulePropertiesSerializer<>(
                JpsPureBasicModuleType.INSTANCE, MODULE_TYPE, MODULE_COMPONENT_NAME) {
            @Override
            public JpsPureBasicModuleElement loadProperties(@Nullable Element componentTag) {
                if (componentTag != null) {
                    return new JpsPureBasicModuleElement(XmlSerializer.deserialize(componentTag,
                            PureBasicModuleSettingsState.class));
                }
                return new JpsPureBasicModuleElement();
            }

            @Override
            public void saveProperties(@NotNull JpsPureBasicModuleElement properties, @NotNull Element componentTag) {
            }
        });
    }

    @NotNull
    @Override
    public List<? extends JpsSdkPropertiesSerializer<?>> getSdkPropertiesSerializers() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<? extends JpsFacetConfigurationSerializer<?>> getFacetConfigurationSerializers() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<? extends JpsProjectExtensionSerializer> getProjectExtensionSerializers() {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<? extends JpsGlobalExtensionSerializer> getGlobalExtensionSerializers() {
        return Collections.singletonList(new JpsGlobalExtensionSerializer("other.xml", "PureBasicCompiler") {
            @Override
            public void loadExtension(@NotNull JpsGlobal jpsGlobal, @NotNull Element componentTag) {
                PureBasicCompilerSettingsState values = XmlSerializer.deserialize(componentTag,
                        PureBasicCompilerSettingsState.class);
                values.getSdks();
            }

            @Override
            public void saveExtension(@NotNull JpsGlobal jpsGlobal, @NotNull Element componentTag) {
            }
        });
    }
}
