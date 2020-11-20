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
import eu.bradan.purebasic.PureBasicUtil;
import eu.bradan.purebasic.module.PureBasicTargetSettings;
import eu.bradan.purebasic.settings.PureBasicCompilerSettings;
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
    private static final Logger LOG = Logger.getInstance(PureBasicCompiler.class);
    private static final HashMap<String, PureBasicCompiler> compilerByHome = new HashMap<>();
    private static PureBasicCompiler defaultCompiler = null;
    private String sdkHome;
    private File compiler;
    private String labels;

    private PureBasicCompiler(String sdkHome, File compiler) {
        this.sdkHome = sdkHome;
        this.compiler = compiler;
        this.labels = "";
    }

    @Nullable
    private static File getCompilerExecutable(String sdkHome) {
        // PureBasic
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

        // SpiderBasic
        if (!compiler.exists()) {
            // Linux
            compiler = Paths.get(sdkHome, "compilers", "sbcompiler").toFile();
        }
        if (!compiler.exists()) {
            // Windows
            compiler = Paths.get(sdkHome, "compilers", "sbcompiler.exe").toFile();
        }
        if (!compiler.exists()) {
            // MacOS
            compiler = Paths.get(sdkHome, "Contents", "Resources", "compilers", "sbcompiler").toFile();
        }

        if (compiler.exists() && compiler.canExecute()) {
            return compiler;
        }

        return null;
    }

    @Nullable
    public static PureBasicCompiler getDefaultCompiler() {
        PureBasicCompilerSettings.loadAllCompilers();
        return defaultCompiler;
    }

    @Nullable
    public static PureBasicCompiler getOrLoadCompilerByHome(String sdkHome) {
        PureBasicCompilerSettings.loadAllCompilers();
        PureBasicCompiler compiler = compilerByHome.getOrDefault(sdkHome, null);
        if (compiler != null) {
            return compiler;
        }

        File executable = getCompilerExecutable(sdkHome);
        if (executable != null) {
            compiler = new PureBasicCompiler(sdkHome, executable);
            if (defaultCompiler == null) {
                defaultCompiler = compiler;
            }
        }

        if (compiler != null) {
            compilerByHome.put(compiler.getSdkHome(), compiler);
        }

        return compiler;
    }

    @Nullable
    public static PureBasicCompiler getCompilerByLabel(String label) {
        PureBasicCompilerSettings.loadAllCompilers();
        for (PureBasicCompiler compiler : compilerByHome.values()) {
            if (compiler.getLabels().contains(label)) {
                return compiler;
            }
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

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    private File getCompiler() {
        return compiler;
    }

    @Nullable
    private Process run(String[] command) {
        return run(command, null);
    }

    @Nullable
    private Process run(String[] command, File workingDir) {
        HashMap<String, String> envVars = new HashMap<>(System.getenv());
        envVars.put("PUREBASIC_HOME", sdkHome);
        envVars.put("SPIDERBASIC_HOME", sdkHome);

        String[] env = envVars.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .toArray(String[]::new);

        Runtime rt = Runtime.getRuntime();
        try {
            if (workingDir != null) {
                return rt.exec(command, env, workingDir);
            } else {
                return rt.exec(command, env);
            }
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

    public boolean isPureBasic() {
        String v = getVersionString();
        if (v == null)
            v = "";
        return v.startsWith("PureBasic");
    }

    public boolean isSpiderBasic() {
        String v = getVersionString();
        if (v == null)
            v = "";
        return v.startsWith("SpiderBasic");
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
            writer.flush();
            reader.readLine(); // skip the amount
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }

                final Matcher m = patternFunction.matcher(line);
                if (m.matches()) {
                    final String name = m.group(1);
                    final String args = m.group(2);
                    final String description = m.group(3);
                    collector.declareFunction(name, args, description);
                }
            }

            // Constants
            writer.write("CONSTANTLIST\n");
            writer.flush();
            reader.readLine(); // skip the amount
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
            writer.flush();
            reader.readLine(); // skip the amount
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }
                structures.add(line);
            }

            for (String structure : structures) {
                writer.write("STRUCTURE\t" + structure + "\n");
                writer.flush();
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
            writer.flush();
            reader.readLine(); // skip the amount
            while ((line = reader.readLine()) != null) {
                if ("OUTPUT\tCOMPLETE".equals(line)) {
                    break;
                }
                interfaces.add(line);
            }

            for (String interf : interfaces) {
                writer.write("INTERFACE\t" + interf + "\n");
                writer.flush();
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

        final File inputFile = Paths.get(contentRoot, targetSettings.getInputFile()).toAbsolutePath().toFile();
        final File outputFile = Paths.get(contentRoot, targetSettings.getOutputFile()).toAbsolutePath().toFile();

        String[] command;

        File workingDir = outputFile.getParentFile();

        if (isPureBasic()) {
            command = new String[]{
                    compiler.getAbsolutePath(),
                    "-e", outputFile.toString(),
                    inputFile.toString()
            };
        } else if (isSpiderBasic()) {
            String name = outputFile.getName();
            int index = name.lastIndexOf('.');
            if (index > 0) {
                name = name.substring(0, index) + ".js";
            } else {
                name = name + ".js";
            }
            File jsFile = new File(workingDir, name);
            File depDir = new File(workingDir, "spiderbasic");
            command = new String[]{
                    compiler.getAbsolutePath(),
                    "-o", PureBasicUtil.relativeTo(outputFile, workingDir),
                    "-js", PureBasicUtil.relativeTo(jsFile, workingDir),
                    "-lp", "spiderbasic",
                    "-cl", depDir.toString(),
                    PureBasicUtil.relativeTo(inputFile, workingDir)
            };
        } else {
            logger.log(new CompileMessage(CompileMessage.CompileMessageType.ERROR,
                    "Error: invalid compiler", "", -1));
            // no valid compiler?
            return -1;
        }

        Process proc = run(command, workingDir);
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
                    logger.log(new CompileMessage(CompileMessage.CompileMessageType.ERROR,
                            line, errorFile, Integer.parseInt(matchErrorLine.group(2))));
                } else {
                    logger.log(new CompileMessage(CompileMessage.CompileMessageType.INFO,
                            line, null, -1));
                }
            }
            while ((line = errorReader.readLine()) != null) {
                Matcher matchErrorInclude = patternErrorInclude.matcher(line);
                Matcher matchErrorLine = patternErrorLine.matcher(line);

                if (matchErrorInclude.matches()) {
                    errorFile = matchErrorInclude.group(1);
                }
                if (matchErrorLine.matches()) {
                    logger.log(new CompileMessage(CompileMessage.CompileMessageType.ERROR,
                            line, errorFile, Integer.parseInt(matchErrorLine.group(2))));
                } else {
                    logger.log(new CompileMessage(CompileMessage.CompileMessageType.ERROR,
                            line, null, -1));
                }
            }
        }

        try {
            return proc.waitFor();
        } catch (InterruptedException ignored) {
        }

        return -1;
    }

    public interface CompileMessageLogger {
        void log(CompileMessage message);
    }

    public interface DeclarationsCollector {
        void declareConstant(int type, String name, String value);

        void declareStructure(String name, List<String> content);

        void declareInterface(String name, List<String> content);

        void declareFunction(String name, String args, String description);
    }
}
