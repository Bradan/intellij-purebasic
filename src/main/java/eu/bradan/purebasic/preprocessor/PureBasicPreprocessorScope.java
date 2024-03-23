/*
 * Copyright (c) 2023 Daniel Brall
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

package eu.bradan.purebasic.preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Tracks the scope of preprocessor directives. E.g. constant definitions, compiler directives, macros, etc.
 */
public class PureBasicPreprocessorScope {

    private final HashMap<String, PureBasicConstant> constants = new HashMap<>();

    private final HashMap<String, PureBasicMacro> macros = new HashMap<>();

    private final HashSet<String> usedModules = new HashSet<>();

    private final LinkedList<String> currentModules = new LinkedList<>();

    /**
     * Creates a new empty scope.
     */
    public PureBasicPreprocessorScope() {
    }

    /**
     * Copy constructor.
     *
     * @param copyFrom copy scope from there
     */
    public PureBasicPreprocessorScope(PureBasicPreprocessorScope copyFrom) {
        if (copyFrom != null) {
            constants.putAll(copyFrom.constants);
            usedModules.addAll(copyFrom.usedModules);
            currentModules.addAll(copyFrom.currentModules);
        }
    }

    /**
     * Outer scope (before) and inner scope (after) are merged into a new scope.
     *
     * @param outer outer scope
     * @param inner inner scope
     */
    public PureBasicPreprocessorScope(PureBasicPreprocessorScope outer, PureBasicPreprocessorScope inner) {
        constants.putAll(outer.constants);
        constants.putAll(inner.constants);
        usedModules.addAll(outer.usedModules);
        usedModules.addAll(inner.usedModules);
        currentModules.addAll(outer.currentModules);
        currentModules.addAll(inner.currentModules);
    }

    /**
     * Overwrites the scope by all the elements in the copyFrom scope.
     *
     * @param copyFrom copy scope from there
     */
    public void overwriteBy(PureBasicPreprocessorScope copyFrom) {
        if (copyFrom != null) {
            constants.putAll(copyFrom.constants);
            usedModules.addAll(copyFrom.usedModules);
            currentModules.addAll(copyFrom.currentModules);
        }
    }

    /**
     * Sets a constant value
     *
     * @param constant the constant
     */
    public synchronized void setConstant(PureBasicConstant constant) {
        constants.put(constant.getName(), constant);
    }

    /**
     * Gets a constant value
     *
     * @param name The name of the constant
     * @return The constant value or null if not found
     */
    public synchronized PureBasicConstant getConstant(String name) {
        return constants.getOrDefault(name, null);
    }

    /**
     * Sets a constant value
     *
     * @param macro the macro
     */
    public synchronized void addMacro(PureBasicMacro macro) {
        macros.put(macro.getName(), macro);
    }

    /**
     * Gets a macro
     *
     * @param name The name of the macro
     * @return The macro or null if not found
     */
    public synchronized PureBasicMacro getMacro(String name) {
        return macros.getOrDefault(name, null);
    }

    public synchronized void enterModule(String name) {
        currentModules.push(name);
    }

    public synchronized String leaveModule() {
        return currentModules.pop();
    }

    public synchronized void useModule(String name) {
        usedModules.add(name);
    }

    public synchronized void unuseModule(String name) {
        usedModules.remove(name);
    }
}
