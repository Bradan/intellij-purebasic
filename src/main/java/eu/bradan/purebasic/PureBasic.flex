package eu.bradan.purebasic;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import eu.bradan.purebasic.psi.PureBasicTypes;
import com.intellij.psi.TokenType;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

%%

%class PureBasicLexer
%implements FlexLexer
%unicode
%ignorecase
%function advance
%type IElementType
%eof{  return;
%eof}

%{
private IElementType lastTokenType = null;
private CharSequence lastToken = null;

private IElementType storeLast(IElementType elementType) {
    lastToken = yytext();
    return lastTokenType = elementType;
}

public static final List<IElementType> KEYWORD_TYPES = Arrays.asList(
            PureBasicTypes.KEYWORD_XOR,
            PureBasicTypes.KEYWORD_XINCLUDEFILE,
            PureBasicTypes.KEYWORD_WITH,
            PureBasicTypes.KEYWORD_WHILE,
            PureBasicTypes.KEYWORD_WEND,
            PureBasicTypes.KEYWORD_USEMODULE,
            PureBasicTypes.KEYWORD_UNUSEMODULE,
            PureBasicTypes.KEYWORD_UNTIL,
            PureBasicTypes.KEYWORD_UNDEFINEMACRO,
            PureBasicTypes.KEYWORD_TO,
            PureBasicTypes.KEYWORD_THREADED,
            PureBasicTypes.KEYWORD_SWAP,
            PureBasicTypes.KEYWORD_STRUCTUREUNION,
            PureBasicTypes.KEYWORD_STRUCTURE,
            PureBasicTypes.KEYWORD_STEP,
            PureBasicTypes.KEYWORD_STATIC,
            PureBasicTypes.KEYWORD_SHARED,
            PureBasicTypes.KEYWORD_SELECT,
            PureBasicTypes.KEYWORD_RUNTIME,
            PureBasicTypes.KEYWORD_RETURN,
            PureBasicTypes.KEYWORD_RESTORE,
            PureBasicTypes.KEYWORD_REPEAT,
            PureBasicTypes.KEYWORD_REDIM,
            PureBasicTypes.KEYWORD_READ,
            PureBasicTypes.KEYWORD_PROTOTYPEC,
            PureBasicTypes.KEYWORD_PROTOTYPE,
            PureBasicTypes.KEYWORD_PROTECTED,
            PureBasicTypes.KEYWORD_PROCEDURERETURN,
            PureBasicTypes.KEYWORD_PROCEDUREDLL,
            PureBasicTypes.KEYWORD_PROCEDURECDLL,
            PureBasicTypes.KEYWORD_PROCEDUREC,
            PureBasicTypes.KEYWORD_PROCEDURE,
            PureBasicTypes.KEYWORD_OR,
            PureBasicTypes.KEYWORD_NOT,
            PureBasicTypes.KEYWORD_NEXT,
            PureBasicTypes.KEYWORD_NEWMAP,
            PureBasicTypes.KEYWORD_NEWLIST,
            PureBasicTypes.KEYWORD_MODULE,
            PureBasicTypes.KEYWORD_MAP,
            PureBasicTypes.KEYWORD_MACROEXPANDEDCOUNT,
            PureBasicTypes.KEYWORD_MACRO,
            PureBasicTypes.KEYWORD_LIST,
            PureBasicTypes.KEYWORD_INTERFACE,
            PureBasicTypes.KEYWORD_INCLUDEPATH,
            PureBasicTypes.KEYWORD_INCLUDEFILE,
            PureBasicTypes.KEYWORD_INCLUDEBINARY,
            PureBasicTypes.KEYWORD_IMPORTC,
            PureBasicTypes.KEYWORD_IMPORT,
            PureBasicTypes.KEYWORD_IF,
            PureBasicTypes.KEYWORD_GOTO,
            PureBasicTypes.KEYWORD_GOSUB,
            PureBasicTypes.KEYWORD_GLOBAL,
            PureBasicTypes.KEYWORD_FOREVER,
            PureBasicTypes.KEYWORD_FOREACH,
            PureBasicTypes.KEYWORD_FOR,
            PureBasicTypes.KEYWORD_FAKERETURN,
            PureBasicTypes.KEYWORD_EXTENDS,
            PureBasicTypes.KEYWORD_ENUMERATIONBINARY,
            PureBasicTypes.KEYWORD_ENUMERATION,
            PureBasicTypes.KEYWORD_ENDWITH,
            PureBasicTypes.KEYWORD_ENDSTRUCTUREUNION,
            PureBasicTypes.KEYWORD_ENDSTRUCTURE,
            PureBasicTypes.KEYWORD_ENDSELECT,
            PureBasicTypes.KEYWORD_ENDPROCEDURE,
            PureBasicTypes.KEYWORD_ENDMODULE,
            PureBasicTypes.KEYWORD_ENDMACRO,
            PureBasicTypes.KEYWORD_ENDINTERFACE,
            PureBasicTypes.KEYWORD_ENDIMPORT,
            PureBasicTypes.KEYWORD_ENDIF,
            PureBasicTypes.KEYWORD_ENDENUMERATION,
            PureBasicTypes.KEYWORD_ENDDECLAREMODULE,
            PureBasicTypes.KEYWORD_ENDDATASECTION,
            PureBasicTypes.KEYWORD_END,
            PureBasicTypes.KEYWORD_ENABLEJS,
            PureBasicTypes.KEYWORD_ENABLEEXPLICIT,
            PureBasicTypes.KEYWORD_ENABLEDEBUGGER,
            PureBasicTypes.KEYWORD_ENABLEASM,
            PureBasicTypes.KEYWORD_ELSEIF,
            PureBasicTypes.KEYWORD_ELSE,
            PureBasicTypes.KEYWORD_DISABLEJS,
            PureBasicTypes.KEYWORD_DISABLEEXPLICIT,
            PureBasicTypes.KEYWORD_DISABLEDEBUGGER,
            PureBasicTypes.KEYWORD_DISABLEASM,
            PureBasicTypes.KEYWORD_DIM,
            PureBasicTypes.KEYWORD_DEFINE,
            PureBasicTypes.KEYWORD_DEFAULT,
            PureBasicTypes.KEYWORD_DECLAREMODULE,
            PureBasicTypes.KEYWORD_DECLAREDLL,
            PureBasicTypes.KEYWORD_DECLARECDLL,
            PureBasicTypes.KEYWORD_DECLAREC,
            PureBasicTypes.KEYWORD_DECLARE,
            PureBasicTypes.KEYWORD_DEBUGLEVEL,
            PureBasicTypes.KEYWORD_DEBUG,
            PureBasicTypes.KEYWORD_DATASECTION,
            PureBasicTypes.KEYWORD_DATA,
            PureBasicTypes.KEYWORD_CONTINUE,
            PureBasicTypes.KEYWORD_COMPILERWARNING,
            PureBasicTypes.KEYWORD_COMPILERSELECT,
            PureBasicTypes.KEYWORD_COMPILERIF,
            PureBasicTypes.KEYWORD_COMPILERERROR,
            PureBasicTypes.KEYWORD_COMPILERENDSELECT,
            PureBasicTypes.KEYWORD_COMPILERENDIF,
            PureBasicTypes.KEYWORD_COMPILERELSEIF,
            PureBasicTypes.KEYWORD_COMPILERELSE,
            PureBasicTypes.KEYWORD_COMPILERDEFAULT,
            PureBasicTypes.KEYWORD_COMPILERCASE,
            PureBasicTypes.KEYWORD_CASE,
            PureBasicTypes.KEYWORD_CALLDEBUGGER,
            PureBasicTypes.KEYWORD_BREAK,
            PureBasicTypes.KEYWORD_AS,
            PureBasicTypes.KEYWORD_ARRAY,
            PureBasicTypes.KEYWORD_AND,
            PureBasicTypes.KEYWORD_ALIGN
    );

public static final List<IElementType> OPERATOR_TYPES = Arrays.asList(
            PureBasicTypes.OP_PARENOPEN,
            PureBasicTypes.OP_PARENCLOSE,
            PureBasicTypes.OP_BRACKOPEN,
            PureBasicTypes.OP_BRACKCLOSE,
            PureBasicTypes.OP_LEQ,
            PureBasicTypes.OP_EQL,
            PureBasicTypes.OP_GEQ,
            PureBasicTypes.OP_EQG,
            PureBasicTypes.OP_NE,
            PureBasicTypes.OP_LSHIFT,
            PureBasicTypes.OP_RSHIFT,
            PureBasicTypes.OP_EQ,
            PureBasicTypes.OP_PLUS,
            PureBasicTypes.OP_MINUS,
            PureBasicTypes.OP_MUL,
            PureBasicTypes.OP_DIV,
            PureBasicTypes.OP_AND,
            PureBasicTypes.OP_OR,
            PureBasicTypes.OP_XOR,
            PureBasicTypes.OP_NOT,
            PureBasicTypes.OP_LESS,
            PureBasicTypes.OP_GREATER,
            PureBasicTypes.OP_MODULO,
            PureBasicTypes.OP_DOT,
            PureBasicTypes.OP_COMMA,
            PureBasicTypes.OP_BACKSLASH,
            PureBasicTypes.OP_HASH,
            PureBasicTypes.OP_MODULE,
            PureBasicTypes.OP_VARADDR,
            PureBasicTypes.OP_LABELADDR,
            PureBasicTypes.OP_DQUOTE,
            PureBasicTypes.OP_QUOTE
        );
%}

NEWLINE=\R
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT=";"[^\r\n]*

KEYWORD_XOR = "XOr"
KEYWORD_XINCLUDEFILE = "XIncludeFile"
KEYWORD_WITH = "With"
KEYWORD_WHILE = "While"
KEYWORD_WEND = "Wend"
KEYWORD_USEMODULE = "UseModule"
KEYWORD_UNUSEMODULE = "UnuseModule"
KEYWORD_UNTIL = "Until"
KEYWORD_UNDEFINEMACRO = "UndefineMacro"
KEYWORD_TO = "To"
KEYWORD_THREADED = "Threaded"
KEYWORD_SWAP = "Swap"
KEYWORD_STRUCTUREUNION = "StructureUnion"
KEYWORD_STRUCTURE = "Structure"
KEYWORD_STEP = "Step"
KEYWORD_STATIC = "Static"
KEYWORD_SHARED = "Shared"
KEYWORD_SELECT = "Select"
KEYWORD_RUNTIME = "Runtime"
KEYWORD_RETURN = "Return"
KEYWORD_RESTORE = "Restore"
KEYWORD_REPEAT = "Repeat"
KEYWORD_REDIM = "ReDim"
KEYWORD_READ = "Read"
KEYWORD_PROTOTYPEC = "PrototypeC"
KEYWORD_PROTOTYPE = "Prototype"
KEYWORD_PROTECTED = "Protected"
KEYWORD_PROCEDURERETURN = "ProcedureReturn"
KEYWORD_PROCEDUREDLL = "ProcedureDLL"
KEYWORD_PROCEDURECDLL = "ProcedureCDLL"
KEYWORD_PROCEDUREC = "ProcedureC"
KEYWORD_PROCEDURE = "Procedure"
KEYWORD_OR = "Or"
KEYWORD_NOT = "Not"
KEYWORD_NEXT = "Next"
KEYWORD_NEWMAP = "NewMap"
KEYWORD_NEWLIST = "NewList"
KEYWORD_MODULE = "Module"
KEYWORD_MAP = "Map"
KEYWORD_MACROEXPANDEDCOUNT = "MacroExpandedCount"
KEYWORD_MACRO = "Macro"
KEYWORD_LIST = "List"
KEYWORD_INTERFACE = "Interface"
KEYWORD_INCLUDEPATH = "IncludePath"
KEYWORD_INCLUDEFILE = "IncludeFile"
KEYWORD_INCLUDEBINARY = "IncludeBinary"
KEYWORD_IMPORTC = "ImportC"
KEYWORD_IMPORT = "Import"
KEYWORD_IF = "If"
KEYWORD_GOTO = "Goto"
KEYWORD_GOSUB = "Gosub"
KEYWORD_GLOBAL = "Global"
KEYWORD_FOREVER = "ForEver"
KEYWORD_FOREACH = "ForEach"
KEYWORD_FOR = "For"
KEYWORD_FAKERETURN = "FakeReturn"
KEYWORD_EXTENDS = "Extends"
KEYWORD_ENUMERATIONBINARY = "EnumerationBinary"
KEYWORD_ENUMERATION = "Enumeration"
KEYWORD_ENDWITH = "EndWith"
KEYWORD_ENDSTRUCTUREUNION = "EndStructureUnion"
KEYWORD_ENDSTRUCTURE = "EndStructure"
KEYWORD_ENDSELECT = "EndSelect"
KEYWORD_ENDPROCEDURE = "EndProcedure"
KEYWORD_ENDMODULE = "EndModule"
KEYWORD_ENDMACRO = "EndMacro"
KEYWORD_ENDINTERFACE = "EndInterface"
KEYWORD_ENDIMPORT = "EndImport"
KEYWORD_ENDIF = "EndIf"
KEYWORD_ENDENUMERATION = "EndEnumeration"
KEYWORD_ENDDECLAREMODULE = "EndDeclareModule"
KEYWORD_ENDDATASECTION = "EndDataSection"
KEYWORD_END = "End"
KEYWORD_ENABLEJS = "EnableJS"
KEYWORD_ENABLEEXPLICIT = "EnableExplicit"
KEYWORD_ENABLEDEBUGGER = "EnableDebugger"
KEYWORD_ENABLEASM = "EnableASM"
KEYWORD_ELSEIF = "ElseIf"
KEYWORD_ELSE = "Else"
KEYWORD_DISABLEJS = "DisableJS"
KEYWORD_DISABLEEXPLICIT = "DisableExplicit"
KEYWORD_DISABLEDEBUGGER = "DisableDebugger"
KEYWORD_DISABLEASM = "DisableASM"
KEYWORD_DIM = "Dim"
KEYWORD_DEFINE = "Define"
KEYWORD_DEFAULT = "Default"
KEYWORD_DECLAREMODULE = "DeclareModule"
KEYWORD_DECLAREDLL = "DeclareDLL"
KEYWORD_DECLARECDLL = "DeclareCDLL"
KEYWORD_DECLAREC = "DeclareC"
KEYWORD_DECLARE = "Declare"
KEYWORD_DEBUGLEVEL = "DebugLevel"
KEYWORD_DEBUG = "Debug"
KEYWORD_DATASECTION = "DataSection"
KEYWORD_DATA = "Data"
KEYWORD_CONTINUE = "Continue"
KEYWORD_COMPILERWARNING = "CompilerWarning"
KEYWORD_COMPILERSELECT = "CompilerSelect"
KEYWORD_COMPILERIF = "CompilerIf"
KEYWORD_COMPILERERROR = "CompilerError"
KEYWORD_COMPILERENDSELECT = "CompilerEndSelect"
KEYWORD_COMPILERENDIF = "CompilerEndIf"
KEYWORD_COMPILERELSEIF = "CompilerElseIf"
KEYWORD_COMPILERELSE = "CompilerElse"
KEYWORD_COMPILERDEFAULT = "CompilerDefault"
KEYWORD_COMPILERCASE = "CompilerCase"
KEYWORD_CASE = "Case"
KEYWORD_CALLDEBUGGER = "CallDebugger"
KEYWORD_BREAK = "Break"
KEYWORD_AS = "As"
KEYWORD_ARRAY = "Array"
KEYWORD_AND = "And"
KEYWORD_ALIGN = "Align"

OP_PARENOPEN     = "("
OP_PARENCLOSE    = ")"
OP_BRACKOPEN     = "["
OP_BRACKCLOSE    = "]"
OP_LEQ           = "<="
OP_EQL           = "=<"
OP_GEQ           = ">="
OP_EQG           = "=>"
OP_NE            = "<>"
OP_LSHIFT        = "<<"
OP_RSHIFT        = ">>"
OP_EQ            = "="
OP_PLUS          = "+"
OP_MINUS         = "-"
OP_MUL           = "*"
OP_DIV           = "/"
OP_AND           = "&"
OP_OR            = "|"
OP_XOR           = "!"
OP_NOT           = "~"
OP_LESS          = "<"
OP_GREATER       = ">"
OP_MODULO        = "%"
OP_DOT           = "."
OP_COMMA         = ","
OP_BACKSLASH     = "\\"
OP_HASH          = "#"
OP_MODULE        = "::"
OP_VARADDR       = "@"
OP_LABELADDR     = "?"
OP_DQUOTE        = "\""
OP_QUOTE         = "'"

STRING_DELIM="\""
CHAR_DELIM="'"
NUMBER="$"[0-9A-Fa-f]+ | "%"[01]+ | [0-9]+(\.[0-9]+|"")
IDENTIFIER=[a-zA-Z_][a-zA-Z_0-9]*("$"|"")
POINTER_IDENTIFIER="*"[a-zA-Z_][a-zA-Z_0-9]*("$"|"")
CONSTANT_IDENTIFIER="#"\s*[a-zA-Z_][a-zA-Z_0-9]*("$"|"")

%state FOLLOW_UP

%%

{END_OF_LINE_COMMENT}                             { yybegin(YYINITIAL); return storeLast(PureBasicTypes.COMMENT); }

{STRING_DELIM}([^\r\n\"]*){STRING_DELIM}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.STRING); }

"~"{STRING_DELIM}([^\r\n\\\"]+|("\\"{STRING_DELIM})|"\\"[abfnrtv\\])*{STRING_DELIM}
                                                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.STRING); }

{CHAR_DELIM}([^\r\n\\\"]+|("\\"{CHAR_DELIM})|"\\"[0abfnrtv\\]){CHAR_DELIM}
                                                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.CHARACTER); }

{KEYWORD_XOR}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_XOR); }
{KEYWORD_XINCLUDEFILE}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_XINCLUDEFILE); }
{KEYWORD_WITH}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_WITH); }
{KEYWORD_WHILE}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_WHILE); }
{KEYWORD_WEND}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_WEND); }
{KEYWORD_USEMODULE}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_USEMODULE); }
{KEYWORD_UNUSEMODULE}              { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_UNUSEMODULE); }
{KEYWORD_UNTIL}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_UNTIL); }
{KEYWORD_UNDEFINEMACRO}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_UNDEFINEMACRO); }
{KEYWORD_TO}                       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_TO); }
{KEYWORD_THREADED}                 { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_THREADED); }
{KEYWORD_SWAP}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_SWAP); }
{KEYWORD_STRUCTUREUNION}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_STRUCTUREUNION); }
{KEYWORD_STRUCTURE}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_STRUCTURE); }
{KEYWORD_STEP}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_STEP); }
{KEYWORD_STATIC}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_STATIC); }
{KEYWORD_SHARED}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_SHARED); }
{KEYWORD_SELECT}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_SELECT); }
{KEYWORD_RUNTIME}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_RUNTIME); }
{KEYWORD_RETURN}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_RETURN); }
{KEYWORD_RESTORE}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_RESTORE); }
{KEYWORD_REPEAT}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_REPEAT); }
{KEYWORD_REDIM}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_REDIM); }
{KEYWORD_READ}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_READ); }
{KEYWORD_PROTOTYPEC}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROTOTYPEC); }
{KEYWORD_PROTOTYPE}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROTOTYPE); }
{KEYWORD_PROTECTED}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROTECTED); }
{KEYWORD_PROCEDURERETURN}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROCEDURERETURN); }
{KEYWORD_PROCEDUREDLL}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROCEDUREDLL); }
{KEYWORD_PROCEDURECDLL}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROCEDURECDLL); }
{KEYWORD_PROCEDUREC}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROCEDUREC); }
{KEYWORD_PROCEDURE}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_PROCEDURE); }
{KEYWORD_OR}                       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_OR); }
{KEYWORD_NOT}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_NOT); }
{KEYWORD_NEXT}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_NEXT); }
{KEYWORD_NEWMAP}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_NEWMAP); }
{KEYWORD_NEWLIST}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_NEWLIST); }
{KEYWORD_MODULE}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_MODULE); }
{KEYWORD_MAP}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_MAP); }
{KEYWORD_MACROEXPANDEDCOUNT}       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_MACROEXPANDEDCOUNT); }
{KEYWORD_MACRO}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_MACRO); }
{KEYWORD_LIST}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_LIST); }
{KEYWORD_INTERFACE}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_INTERFACE); }
{KEYWORD_INCLUDEPATH}              { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_INCLUDEPATH); }
{KEYWORD_INCLUDEFILE}              { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_INCLUDEFILE); }
{KEYWORD_INCLUDEBINARY}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_INCLUDEBINARY); }
{KEYWORD_IMPORTC}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_IMPORTC); }
{KEYWORD_IMPORT}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_IMPORT); }
{KEYWORD_IF}                       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_IF); }
{KEYWORD_GOTO}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_GOTO); }
{KEYWORD_GOSUB}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_GOSUB); }
{KEYWORD_GLOBAL}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_GLOBAL); }
{KEYWORD_FOREVER}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_FOREVER); }
{KEYWORD_FOREACH}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_FOREACH); }
{KEYWORD_FOR}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_FOR); }
{KEYWORD_FAKERETURN}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_FAKERETURN); }
{KEYWORD_EXTENDS}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_EXTENDS); }
{KEYWORD_ENUMERATIONBINARY}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENUMERATIONBINARY); }
{KEYWORD_ENUMERATION}              { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENUMERATION); }
{KEYWORD_ENDWITH}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDWITH); }
{KEYWORD_ENDSTRUCTUREUNION}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDSTRUCTUREUNION); }
{KEYWORD_ENDSTRUCTURE}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDSTRUCTURE); }
{KEYWORD_ENDSELECT}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDSELECT); }
{KEYWORD_ENDPROCEDURE}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDPROCEDURE); }
{KEYWORD_ENDMODULE}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDMODULE); }
{KEYWORD_ENDMACRO}                 { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDMACRO); }
{KEYWORD_ENDINTERFACE}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDINTERFACE); }
{KEYWORD_ENDIMPORT}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDIMPORT); }
{KEYWORD_ENDIF}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDIF); }
{KEYWORD_ENDENUMERATION}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDENUMERATION); }
{KEYWORD_ENDDECLAREMODULE}         { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDDECLAREMODULE); }
{KEYWORD_ENDDATASECTION}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENDDATASECTION); }
{KEYWORD_END}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_END); }
{KEYWORD_ENABLEJS}                 { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENABLEJS); }
{KEYWORD_ENABLEEXPLICIT}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENABLEEXPLICIT); }
{KEYWORD_ENABLEDEBUGGER}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENABLEDEBUGGER); }
{KEYWORD_ENABLEASM}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ENABLEASM); }
{KEYWORD_ELSEIF}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ELSEIF); }
{KEYWORD_ELSE}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ELSE); }
{KEYWORD_DISABLEJS}                { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DISABLEJS); }
{KEYWORD_DISABLEEXPLICIT}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DISABLEEXPLICIT); }
{KEYWORD_DISABLEDEBUGGER}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DISABLEDEBUGGER); }
{KEYWORD_DISABLEASM}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DISABLEASM); }
{KEYWORD_DIM}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DIM); }
{KEYWORD_DEFINE}                   { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DEFINE); }
{KEYWORD_DEFAULT}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DEFAULT); }
{KEYWORD_DECLAREMODULE}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DECLAREMODULE); }
{KEYWORD_DECLAREDLL}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DECLAREDLL); }
{KEYWORD_DECLARECDLL}              { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DECLARECDLL); }
{KEYWORD_DECLAREC}                 { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DECLAREC); }
{KEYWORD_DECLARE}                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DECLARE); }
{KEYWORD_DEBUGLEVEL}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DEBUGLEVEL); }
{KEYWORD_DEBUG}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DEBUG); }
{KEYWORD_DATASECTION}              { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DATASECTION); }
{KEYWORD_DATA}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_DATA); }
{KEYWORD_CONTINUE}                 { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_CONTINUE); }
{KEYWORD_COMPILERWARNING}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERWARNING); }
{KEYWORD_COMPILERSELECT}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERSELECT); }
{KEYWORD_COMPILERIF}               { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERIF); }
{KEYWORD_COMPILERERROR}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERERROR); }
{KEYWORD_COMPILERENDSELECT}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERENDSELECT); }
{KEYWORD_COMPILERENDIF}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERENDIF); }
{KEYWORD_COMPILERELSEIF}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERELSEIF); }
{KEYWORD_COMPILERELSE}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERELSE); }
{KEYWORD_COMPILERDEFAULT}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERDEFAULT); }
{KEYWORD_COMPILERCASE}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_COMPILERCASE); }
{KEYWORD_CASE}                     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_CASE); }
{KEYWORD_CALLDEBUGGER}             { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_CALLDEBUGGER); }
{KEYWORD_BREAK}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_BREAK); }
{KEYWORD_AS}                       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_AS); }
{KEYWORD_ARRAY}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ARRAY); }
{KEYWORD_AND}                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_AND); }
{KEYWORD_ALIGN}                    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD_ALIGN); }

{NUMBER}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.NUMBER); }

{OP_PARENOPEN}     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_PARENOPEN); }
{OP_PARENCLOSE}    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_PARENCLOSE); }
{OP_BRACKOPEN}     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_BRACKOPEN); }
{OP_BRACKCLOSE}    { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_BRACKCLOSE); }
{OP_LEQ}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_LEQ); }
{OP_EQL}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_EQL); }
{OP_GEQ}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_GEQ); }
{OP_EQG}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_EQG); }
{OP_NE}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_NE); }
{OP_LSHIFT}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_LSHIFT); }
{OP_RSHIFT}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_RSHIFT); }
{OP_EQ}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_EQ); }
{OP_PLUS}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_PLUS); }
{OP_MINUS}         { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_MINUS); }
{OP_MUL}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_MUL); }
{OP_DIV}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_DIV); }
{OP_AND}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_AND); }
{OP_OR}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_OR); }
{OP_XOR}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_XOR); }
{OP_NOT}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_NOT); }
{OP_LESS}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_LESS); }
{OP_GREATER}       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_GREATER); }
{OP_MODULO}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_MODULO); }
{OP_DOT}           { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_DOT); }
{OP_COMMA}         { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_COMMA); }
{OP_BACKSLASH}     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_BACKSLASH); }
{OP_HASH}          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_HASH); }
{OP_MODULE}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_MODULE); }
{OP_VARADDR}       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_VARADDR); }
{OP_LABELADDR}     { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_LABELADDR); }
{OP_DQUOTE}        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_DQUOTE); }
{OP_QUOTE}         { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OP_QUOTE); }

<YYINITIAL> "!"[^\r\n:]*                          { yybegin(YYINITIAL); return storeLast(PureBasicTypes.INLINE_ASM); }
<YYINITIAL> {IDENTIFIER}":"[^:]                   {
          yypushback(yylength() - yytext().toString().indexOf(":"));
          yybegin(FOLLOW_UP);
          return storeLast(PureBasicTypes.LABEL_IDENTIFIER);
      }
{IDENTIFIER}                                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.IDENTIFIER); }
{POINTER_IDENTIFIER}                              {
          yybegin(FOLLOW_UP);
          final ArrayList<IElementType> validLastTokens = new ArrayList<>(
                  Arrays.asList(new IElementType[] {
                        PureBasicTypes.SEPARATOR
                        })
                  );
          validLastTokens.addAll(KEYWORD_TYPES);
          validLastTokens.addAll(OPERATOR_TYPES);
          if(validLastTokens.contains(lastTokenType)) {
              return storeLast(PureBasicTypes.POINTER_IDENTIFIER);
          } else {
              yypushback(yylength() - 1);
              return storeLast(PureBasicTypes.OPERATOR);
          }
      }
{CONSTANT_IDENTIFIER}                             {
          yybegin(FOLLOW_UP);
          final List<IElementType> validLastTokens = Arrays.asList(new IElementType[] {
                  PureBasicTypes.IDENTIFIER, PureBasicTypes.CONSTANT_IDENTIFIER, PureBasicTypes.POINTER_IDENTIFIER
          });
          if(validLastTokens.contains(lastTokenType)) {
              yypushback(yylength() - 1);
              return storeLast(PureBasicTypes.OPERATOR);
          } else {
              return storeLast(PureBasicTypes.CONSTANT_IDENTIFIER);
          }
      }

({NEWLINE}|":")                                   { yybegin(YYINITIAL); return storeLast(PureBasicTypes.SEPARATOR); }
({WHITE_SPACE})+                                  { return TokenType.WHITE_SPACE; }


[^]                                               { return storeLast(TokenType.BAD_CHARACTER); }
