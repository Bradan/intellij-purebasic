# intellij-purebasic

<!-- Plugin description -->
PureBasic and SpiderBasic language support for IntelliJ IDEA and IDEA based IDEs.

Known to work with

- PureBasic 5.72 (Linux - x64)
- SpiderBasic 2.10 (Linux - x64)

Usage instructions

1. Install this plugin and a PureBasic or SpiderBasic compiler.
2. Go to <a href="jetbrains://Idea/settings?name=Build%2C+Execution%2C+Deployment--Build+Tools--PureBasic">
   File | Settings | Build, Execution, Deployment | Build Tools | PureBasic</a> and set up your SDK paths.
   Add a label to them (for example PureBasic 5.72 can be labelled as "pb572").
3. Create a project with a PureBasic module.
4. Create some PureBasic or SpiderBasic sources files (Module context menu | New | New PureBasic File).
5. Open the module settings and create a build target. Name it, specify the main source file and the output
   filename as well as a SDK label.
6. Create a PureBasic run configuration which is linked to this module and build target. (PureBasic only)
7. Compile the project and run it.

The current version is still very limited (and there are probably bugs), hence it is only available in the alpha
channel.

External links: [Source code](https://github.com/Bradan/intellij-purebasic)
<!-- Plugin description end -->

## Compile Instructions

Gradle tasks to execute (in separate gradle calls, because a subtask named compileJava must be executed multiple times):

1. *generatePureBasicParser*:
    generate lexer/parser files with JFlex and GrammarKit
2. *assemble*:
    compile everything, create separate jars
3. *createFatJar*:
    creates the final fat jar which also contains the jps plugin.
    
The first compile is necessary because GrammarKit attaches methods to
 the expression tree nodes, which cannot be found if there are no precompiled
 class files.
Unfortunately, gradle has no ability to execute a task twice, hence you have
 to do it manually.
