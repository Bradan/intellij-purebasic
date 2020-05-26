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

package eu.bradan.purebasic;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import eu.bradan.purebasic.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PureBasicUtil {
    @Contract(pure = true)
    public static List<String> getScopePath(PsiElement element) {
        // TODO: find the scope path of this element
        return Collections.emptyList();
    }

    @NotNull
    @Contract(pure = true)
    public static List<Declaration> findDeclarations(PureBasicFile file) {
        final HashMap<String, DeclarationScope> scopeMap = new HashMap<>();
        scopeMap.put("global", DeclarationScope.GLOBAL);
        scopeMap.put("shared", DeclarationScope.SHARED);
        scopeMap.put("protected", DeclarationScope.PROTECTED);
        scopeMap.put("static", DeclarationScope.STATIC);
        scopeMap.put("threaded", DeclarationScope.THREADED);

        final LinkedList<Declaration> result = new LinkedList<>();
        for (PsiElement decl : PsiTreeUtil.findChildrenOfAnyType(file,
                PureBasicVariableDeclaration.class,
                PureBasicAssignmentStmt.class,
                PureBasicProcedureHead.class,
                PureBasicMacroHead.class,
                PureBasicStructureHead.class,
                PureBasicInterfaceHead.class,
                PureBasicImportHead.class,
                PureBasicDeclareModuleHead.class,
                PureBasicDefineModuleHead.class,
                PureBasicProcedureDeclaration.class,
                PureBasicEnumerationHead.class)) {
            DeclarationScope scope = DeclarationScope.UNSPECIFIED;
//            PureBasicDeclarationScope scopeElement = decl.getDeclarationScope();
//            if (scopeElement != null) {
//                scope = scopeMap.getOrDefault(scopeElement.getText(), DeclarationScope.UNSPECIFIED);
//            }
        }
        return result;
    }

    public enum DeclarationType {
        CONSTANT,
        VARIABLE,
        PROC,
        MACRO,
        ENUMERATION,
        STRUCTURE,
    }

    public enum DeclarationScope {
        GLOBAL,
        SHARED,
        PROTECTED,
        STATIC,
        THREADED,
        UNSPECIFIED
    }

    public static class Declaration {
        public DeclarationScope scope;
        public DeclarationType type;
        public String name;
        public PsiElement element;

        public Declaration() {
            this.scope = DeclarationScope.UNSPECIFIED;
            this.type = DeclarationType.VARIABLE;
            this.name = null;
            this.element = null;
        }

        public Declaration(DeclarationScope scope, DeclarationType type, String name, PsiElement element) {
            this.scope = scope;
            this.type = type;
            this.name = name;
            this.element = element;
        }
    }
}
