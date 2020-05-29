# intellij-purebasic

An IntelliJ PureBasic Plugin

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
