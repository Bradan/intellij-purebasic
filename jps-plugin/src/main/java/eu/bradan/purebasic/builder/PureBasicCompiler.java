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
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PureBasicCompiler {
    private static final Logger LOG = Logger.getInstance(PureBasicBuilder.class);

    private String sdkHome;
    private File compiler;

    private PureBasicCompiler(String sdkHome, File compiler) {
        this.sdkHome = sdkHome;
        this.compiler = compiler;
    }

    @Nullable
    private static File getCompilerExecutable(String sdkHome) {
        // Linux
        File compiler = Paths.get(sdkHome, "compilers", "pbcompiler").toFile();
        if (!compiler.exists()) {
            // Windows
            compiler = Paths.get(sdkHome, "compilers", "pbcompiler.exe").toFile();
        }
        if (!compiler.exists()) {
            // MacOS
            compiler = Paths.get(sdkHome, "Contents", "Resources", "compilers", "pbcompiler").toFile();
        }
        if (compiler.exists() && compiler.canExecute()) {
            return compiler;
        }

        return null;
    }

    @Nullable
    public static PureBasicCompiler getPureBasicCompiler(String sdkHome) {
        File compiler = getCompilerExecutable(sdkHome);
        if (compiler != null) {
            return new PureBasicCompiler(sdkHome, compiler);
        }

        return null;
    }

    public String getSdkHome() {
        return sdkHome;
    }

    public void setSdkHome(String sdkHome) {
        this.sdkHome = sdkHome;
        this.compiler = getCompilerExecutable(sdkHome);
    }

    @Nullable
    private Process run(String[] command) {
        HashMap<String, String> envVars = new HashMap<>(System.getenv());
        envVars.put("PUREBASIC_HOME", sdkHome);
        envVars.put("SPIDERBASIC_HOME", sdkHome);

        String[] env = envVars.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .toArray(String[]::new);

        Runtime rt = Runtime.getRuntime();
        try {
            return rt.exec(command, env);
        } catch (IOException ignored) {
        }
        return null;
    }

    @Nullable
    public String getVersionString() {
        Process proc = run(new String[]{compiler.getAbsolutePath(), "--version"});
        if (proc == null) {
            return null;
        }

        try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            return outputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getSdkDeclarations(DeclarationsCollector collector) {
        Process proc = run(new String[]{compiler.getAbsolutePath(), "--standby"});
        if (proc == null) {
            return;
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(proc.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if ("READY".equals(line)) {
                    break;
                }
            }

            // Functions
            final Pattern patternFunction = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*)\\s*\\(([^)]*)\\)\\s*-\\s*(.*)");
            writer.write("FUNCTIONLIST\n");
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }

                final Matcher m = patternFunction.matcher(line);
                if (m.matches()) {
                    final String name = m.group(0);
                    final String args = m.group(1);
                    final String description = m.group(2);
                    collector.declareFunction(name, args, description);
                }
            }

            // Constants
            writer.write("CONSTANTLIST\n");
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }

                final String[] fields = line.split("\t");
                if (fields.length == 3) {
                    collector.declareConstant(Integer.parseInt(fields[0]), fields[1], fields[2]);
                }
            }

            // Structures
            LinkedList<String> structures = new LinkedList<>();
            writer.write("STRUCTURELIST\n");
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }
                structures.add(line);
            }

            for (String structure : structures) {
                writer.write("STRUCTURE\t" + structure + "\n");
                LinkedList<String> content = new LinkedList<>();
                while ((line = reader.readLine()) != null) {
                    if ("OUTPUT\tCOMPLETE".equals(line)) {
                        break;
                    }
                    content.add(line);
                }
                collector.declareStructure(structure, content);
            }

            // Interfaces
            LinkedList<String> interfaces = new LinkedList<>();
            writer.write("INTERFACELIST\n");
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }
                interfaces.add(line);
            }

            for (String interf : interfaces) {
                writer.write("INTERFACE\t" + interf + "\n");
                LinkedList<String> content = new LinkedList<>();
                while ((line = reader.readLine()) != null) {
                    if ("OUTPUT\tCOMPLETE".equals(line)) {
                        break;
                    }
                    content.add(line);
                }
                collector.declareInterface(interf, content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int compile(PureBasicTargetSettings targetSettings, @NotNull String contentRoot,
                       CompileMessageLogger logger) throws IOException {
        if (contentRoot.startsWith("file://")) {
            contentRoot = contentRoot.substring("file://".length());
        }

        final File inputFile = Paths.get(contentRoot, targetSettings.inputFile).toAbsolutePath().toFile();
        final File outputFile = Paths.get(contentRoot, targetSettings.outputFile).toAbsolutePath().toFile();

        final String[] command = new String[]{
                compiler.getAbsolutePath(),
                "-e", outputFile.toString(),
                inputFile.toString()
        };

        Process proc = run(command);
        if (proc == null) {
            return -1;
        }

        try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
            String line;
            String errorFile = inputFile.toString();

            Pattern patternErrorInclude = Pattern.compile("Error: in included file '(.*)'");
            Pattern patternErrorLine = Pattern.compile("(Error: )?Line ([0-9]+) - (.*)");

            while ((line = outputReader.readLine()) != null) {
                Matcher matchErrorInclude = patternErrorInclude.matcher(line);
                Matcher matchErrorLine = patternErrorLine.matcher(line);

                if (matchErrorInclude.matches()) {
                    errorFile = matchErrorInclude.group(1);
                }
                if (matchErrorLine.matches()) {
                    logger.logError(line, errorFile, Integer.parseInt(matchErrorLine.group(2)));
                } else {
                    logger.logInfo(line, null, -1);
                }
            }
            while ((line = errorReader.readLine()) != null) {
                Matcher matchErrorInclude = patternErrorInclude.matcher(line);
                Matcher matchErrorLine = patternErrorLine.matcher(line);

                if (matchErrorInclude.matches()) {
                    errorFile = matchErrorInclude.group(1);
                }
                if (matchErrorLine.matches()) {
                    logger.logError(line, errorFile, Integer.parseInt(matchErrorLine.group(2)));
                } else {
                    logger.logError(line, null, -1);
                }
            }
        }
        return proc.exitValue();
    }

    public interface CompileMessageLogger {
        void logInfo(String message, String file, int line);

        void logError(String message, String file, int line);
    }

    public interface DeclarationsCollector {
        void declareConstant(int type, String name, String value);

        void declareStructure(String name, List<String> content);

        void declareInterface(String name, List<String> content);

        void declareFunction(String name, String args, String description);
    }
}
