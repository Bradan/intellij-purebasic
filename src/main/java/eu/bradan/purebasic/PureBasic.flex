package eu.bradan.purebasic;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import eu.bradan.purebasic.psi.PureBasicTypes;
import com.intellij.psi.TokenType;
import java.util.List;
import java.util.Arrays;

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
%}

NEWLINE=\R
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT=";"[^\r\n]*
KEYWORDS="XOr"|"XIncludeFile"|"With"|"While"|"Wend"|"UseModule"|"UnuseModule"|"Until"|"UndefineMacro"|"To"|"Threaded"|"Swap"|"StructureUnion"|"Structure"|"Step"|"Static"|"Shared"|"Select"|"Runtime"|"Return"|"Restore"|"Repeat"|"ReDim"|"Read"|"PrototypeC"|"Prototype"|"Protected"|"ProcedureReturn"|"ProcedureDLL"|"ProcedureCDLL"|"ProcedureC"|"Procedure"|"Or"|"Not"|"Next"|"NewMap"|"NewList"|"Module"|"Map"|"MacroExpandedCount"|"Macro"|"List"|"Interface"|"IncludePath"|"IncludeFile"|"IncludeBinary"|"ImportC"|"Import"|"If"|"Goto"|"Gosub"|"Global"|"ForEver"|"ForEach"|"For"|"FakeReturn"|"Extends"|"EnumerationBinary"|"Enumeration"|"EndWith"|"EndStructureUnion"|"EndStructure"|"EndSelect"|"EndProcedure"|"EndModule"|"EndMacro"|"EndInterface"|"EndImport"|"EndIf"|"EndEnumeration"|"EndDeclareModule"|"EndDataSection"|"End"|"EnableJS"|"EnableExplicit"|"EnableDebugger"|"EnableASM"|"ElseIf"|"Else"|"DisableJS"|"DisableExplicit"|"DisableDebugger"|"DisableASM"|"Dim"|"Define"|"Default"|"DeclareModule"|"DeclareDLL"|"DeclareCDLL"|"DeclareC"|"Declare"|"DebugLevel"|"Debug"|"DataSection"|"Data"|"Continue"|"CompilerWarning"|"CompilerSelect"|"CompilerIf"|"CompilerError"|"CompilerEndSelect"|"CompilerEndIf"|"CompilerElseIf"|"CompilerElse"|"CompilerDefault"|"CompilerCase"|"Case"|"CallDebugger"|"Break"|"As"|"Array"|"And"|"Align"
OPERATORS="("|")"|"["|"]"|"<="|"=<"|">="|"=>"|"<>"|"<<"|">>"|"="|"+"|"-"|"*"|"/"|"&"|"|"|"!"|"~"|"<"|">"|"%"|"."|","|"\\"|"#"|"::"|"@"|"?"
STRING_DELIM="\""
CHAR_DELIM="'"
NUMBER="$"[0-9A-Fa-f]+ | "%"[01]+ | [0-9]+(\.[0-9]+|"")
IDENTIFIER=[a-zA-Z_][a-zA-Z_0-9]*("$"|"")
POINTER_IDENTIFIER="*"[a-zA-Z_][a-zA-Z_0-9]*("$"|"")
CONSTANT_IDENTIFIER="#"[a-zA-Z_][a-zA-Z_0-9]*("$"|"")

%state FOLLOW_UP

%%

{END_OF_LINE_COMMENT}                             { yybegin(YYINITIAL); return storeLast(PureBasicTypes.COMMENT); }

{STRING_DELIM}([^\R\"]*){STRING_DELIM}            { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.STRING); }

"~"{STRING_DELIM}([^\R\\\"]+|("\\"{STRING_DELIM})|"\\"[abfnrtv\\])*{STRING_DELIM}
                                                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.STRING); }

{CHAR_DELIM}([^\R\\\"]+|("\\"{CHAR_DELIM})|"\\"[0abfnrtv\\]){CHAR_DELIM}
                                                  { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.CHARACTER); }

{KEYWORDS}                                        { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.KEYWORD); }
{NUMBER}                                          { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.NUMBER); }
{OPERATORS}                                       { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.OPERATOR); }

<YYINITIAL> "!"[^\r\n:]*                          { yybegin(YYINITIAL); return storeLast(PureBasicTypes.INLINE_ASM); }
<YYINITIAL> {IDENTIFIER}":"[^:]                   {
          yypushback(yylength() - yytext().toString().indexOf(":"));
          yybegin(FOLLOW_UP);
          return storeLast(PureBasicTypes.LABEL_IDENTIFIER);
      }
{IDENTIFIER}                                      { yybegin(FOLLOW_UP); return storeLast(PureBasicTypes.IDENTIFIER); }
{POINTER_IDENTIFIER}                              {
          yybegin(FOLLOW_UP);
          final List<IElementType> validLastTokens = Arrays.asList(new IElementType[] {
                  PureBasicTypes.OPERATOR, PureBasicTypes.KEYWORD, PureBasicTypes.SEPARATOR
          });
          if(validLastTokens.contains(lastTokenType)) {
              return storeLast(PureBasicTypes.POINTER_IDENTIFIER);
          } else {
              yypushback(yylength() - 1);
              return storeLast(PureBasicTypes.OPERATOR);
          }
      }
{CONSTANT_IDENTIFIER}                             { yybegin(FOLLOW_UP);
          if(lastTokenType != PureBasicTypes.IDENTIFIER) {
              return storeLast(PureBasicTypes.CONSTANT_IDENTIFIER);
          } else {
              yypushback(yylength() - 1);
              return storeLast(PureBasicTypes.OPERATOR);
          }
      }

({NEWLINE}|":")                                   { yybegin(YYINITIAL); return storeLast(PureBasicTypes.SEPARATOR); }
({WHITE_SPACE})+                                  { return TokenType.WHITE_SPACE; }


[^]                                               { return storeLast(TokenType.BAD_CHARACTER); }
