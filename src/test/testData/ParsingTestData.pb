; general functionality

EnableExplicit

Global *apointer
*apointer = $af + %1010

Debug "OK"

DeclareModule module1
    Structure structure2
        y.i
    EndStructure
EndDeclareModule

Module module1
EndModule

Structure structure1
    x.i
    y.module1::structure2
    *z.Double
EndStructure

Procedure.s procedure1(param1.i, param2.i=23)
    ProcedureReturn Str(param1) + " " + Str(param2)
EndProcedure

Procedure procedure2()
    ProcedureReturn 45
EndProcedure

Debug procedure1(1, procedure2())

Macro macro1(a=, b=)
    (a + b)
EndMacro

Macro macro2(a, b, c)
    a = b + c
EndMacro