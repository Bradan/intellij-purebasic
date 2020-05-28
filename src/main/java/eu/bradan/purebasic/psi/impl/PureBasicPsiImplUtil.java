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

package eu.bradan.purebasic.psi.impl;

import com.intellij.navigation.ItemPresentation;
import eu.bradan.purebasic.psi.*;

public class PureBasicPsiImplUtil {
    public static ItemPresentation getPresentation(final PureBasicProcedureBlock element) {
        return new PureBasicProcedureBlockPresentation(element);
    }

    public static ItemPresentation getPresentation(final PureBasicMacroBlock element) {
        return new PureBasicMacroBlockPresentation(element);
    }

    public static ItemPresentation getPresentation(final PureBasicStructureBlock element) {
        return new PureBasicStructureBlockPresentation(element);
    }

    public static ItemPresentation getPresentation(final PureBasicInterfaceBlock element) {
        return new PureBasicInterfaceBlockPresentation(element);
    }

    public static ItemPresentation getPresentation(final PureBasicDeclareModuleBlock element) {
        return new PureBasicDeclareModuleBlockPresentation(element);
    }

    public static ItemPresentation getPresentation(final PureBasicDefineModuleBlock element) {
        return new PureBasicDefineModuleBlockPresentation(element);
    }
}
