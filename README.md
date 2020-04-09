# intellij-purebasic

An IntelliJ PureBasic Plugin

## Compile Instructions

Gradle tasks to execute:

1. *generatePureBasicLexer*, *generatePureBasicParser*:
    generate lexer/parser files with GrammarKit/JFlex
2. *compileJava*:
    precompile some files for GrammarKit
3. *generatePureBasicLexer*, *generatePureBasicParser*:
    generate lexer/parser files with GrammarKit/JFlex
    with method information from step two.
4. *assemble*:
    compile again with the generated GrammarKit files.

The first compile is necessary because GrammarKit attaches methods to
 the expression tree nodes, which cannot be found if there are no precompiled
 class files.
Unfortunately, gradle has no ability to execute a task twice, hence you have
 to do it manually.
