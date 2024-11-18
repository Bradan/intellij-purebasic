/*
 * Copyright (c) 2024 Daniel Brall
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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import eu.bradan.purebasic.PureBasicUtil;
import eu.bradan.purebasic.preprocessor.PureBasicConstant;
import eu.bradan.purebasic.preprocessor.PureBasicPreprocessorScope;
import eu.bradan.purebasic.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PureBasicExpressionElementImpl extends ASTWrapperPsiElement implements PureBasicExpressionElement {
    private static final Logger LOG = Logger.getInstance(PureBasicExpressionElementImpl.class);

    public PureBasicExpressionElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PureBasicConstant evaluateConstant(PureBasicPreprocessorScope scope) {
        PureBasicConstant expression = null;
        if (this instanceof PureBasicParenthesisExpression) {
            var children = getChildren();
            StringBuilder result = new StringBuilder();
            for (var child : children) {
                if (child instanceof PureBasicExpressionElement) {
                    var constant = ((PureBasicExpressionElement) child).evaluateConstant(scope);
                    result.append(constant.getValue());
                }
            }
            expression = new PureBasicConstant("", PureBasicConstant.Type.STRING, result.toString());
        } else if (this instanceof PureBasicSumExpression
                || this instanceof PureBasicFactorExpression
                || this instanceof PureBasicBitshiftExpression
                || this instanceof PureBasicBitwiseAndorExpression
                || this instanceof PureBasicCompExpression
                || this instanceof PureBasicConcatExpression) {
            expression = this.evaluateBinary(scope);
        } else if (this instanceof PureBasicBoolnegExpression
                || this instanceof PureBasicNegateExpression) {
            expression = this.evaluateUnary(scope);
        } else if (this instanceof PureBasicAtom) {
            expression = this.evaluateAtom(scope);
        } else if (this instanceof PureBasicExpressionImpl) {
            var child = getFirstChild();
            if (child instanceof PureBasicExpressionElement) {
                expression = ((PureBasicExpressionElement) child).evaluateConstant(scope);
            }
        }

        LOG.info("Expression: " + this.getText() + " of type " + this.getClass().getCanonicalName() + " -> " + (expression != null ? expression.getValue() : "null"));

        // default case
        return Objects.requireNonNullElseGet(expression, () -> new PureBasicConstant("", PureBasicConstant.Type.INTEGER, "0"));
    }

    private PureBasicConstant evaluateAtom(PureBasicPreprocessorScope scope) {
        var text = this.getText();

        if (text.startsWith("#")) {
            var constant = scope.getConstant(text);
            if (constant != null) {
                return constant;
            }
        }

        try {
            return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, Integer.toString(Integer.parseInt(text)));
        } catch (NumberFormatException e) {
            try {
                return new PureBasicConstant("", PureBasicConstant.Type.FLOAT, Double.toString(Double.parseDouble(text)));
            } catch (NumberFormatException e2) {
                var stringValue = PureBasicUtil.getStringContents(text);
                return new PureBasicConstant("", PureBasicConstant.Type.STRING, stringValue);
            }
        }
    }

    private PureBasicConstant evaluateUnary(PureBasicPreprocessorScope scope) {
        var children = getChildren();
        if (children.length == 2 && children[1] instanceof PureBasicExpressionElement) {
            var constant = ((PureBasicExpressionElement) children[1]).evaluateConstant(scope);
            var op = children[0].getText();
            if (constant.getType() == PureBasicConstant.Type.INTEGER) {
                int value = Integer.parseInt(constant.getValue());
                int result = 0;
                if ("~".equals(op)) {
                    result = ~value;
                } else if ("-".equals(op)) {
                    result = -value;
                }
                return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, Integer.toString(result));
            } else if (constant.getType() == PureBasicConstant.Type.FLOAT) {
                double value = Double.parseDouble(constant.getValue());
                double result = 0;
                if ("-".equals(op)) {
                    result = -value;
                }
                return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, Double.toString(result));
            }
        }

        // default case
        return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, "0");
    }

    private PureBasicConstant evaluateBinary(PureBasicPreprocessorScope scope) {
        var children = getChildren();
        if (children.length == 3 && children[0] instanceof PureBasicExpressionElement exprLeft && children[2] instanceof PureBasicExpressionElement exprRight) {
            var leftConstant = exprLeft.evaluateConstant(scope);
            var rightConstant = exprRight.evaluateConstant(scope);
            var op = children[1].getText();
            if (leftConstant.getType() == PureBasicConstant.Type.INTEGER && rightConstant.getType() == PureBasicConstant.Type.INTEGER) {
                int leftValue = Integer.parseInt(leftConstant.getValue());
                int rightValue = Integer.parseInt(rightConstant.getValue());
                int result = 0;
                if ("+".equals(op)) {
                    result = leftValue + rightValue;
                } else if ("-".equals(op)) {
                    result = leftValue - rightValue;
                } else if ("*".equals(op)) {
                    result = leftValue * rightValue;
                } else if ("/".equals(op)) {
                    result = leftValue / rightValue;
                } else if ("&".equals(op)) {
                    result = leftValue & rightValue;
                } else if ("|".equals(op)) {
                    result = leftValue | rightValue;
                } else if ("^".equals(op)) {
                    result = leftValue ^ rightValue;
                } else if ("<<".equals(op)) {
                    result = leftValue << rightValue;
                } else if (">>".equals(op)) {
                    result = leftValue >> rightValue;
                } else if ("<".equals(op)) {
                    result = leftValue < rightValue ? 1 : 0;
                } else if (">".equals(op)) {
                    result = leftValue > rightValue ? 1 : 0;
                } else if ("<=".equals(op) || "=<".equals(op)) {
                    result = leftValue <= rightValue ? 1 : 0;
                } else if (">=".equals(op) || "=>".equals(op)) {
                    result = leftValue >= rightValue ? 1 : 0;
                } else if ("=".equals(op)) {
                    result = leftValue == rightValue ? 1 : 0;
                } else if ("<>".equals(op)) {
                    result = leftValue != rightValue ? 1 : 0;
                }
                return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, Integer.toString(result));
            } else if (leftConstant.getType() == PureBasicConstant.Type.STRING || rightConstant.getType() == PureBasicConstant.Type.STRING) {
                // at least one of them is a string
                if ("+".equals(op)) {
                    String result = leftConstant.getValue() + rightConstant.getValue();
                    return new PureBasicConstant("", PureBasicConstant.Type.STRING, result);
                } else if ("=".equals(op)) {
                    var result = leftConstant.getValue().equals(rightConstant.getValue()) ? 1 : 0;
                    return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, Integer.toString(result));
                } else if ("<>".equals(op)) {
                    var result = leftConstant.getValue().equals(rightConstant.getValue()) ? 0 : 1;
                    return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, Integer.toString(result));
                }
                return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, "0");
            } else {
                // at least one of them is a float
                double leftValue = Double.parseDouble(leftConstant.getValue());
                double rightValue = Double.parseDouble(rightConstant.getValue());
                double result = 0.0;
                if ("+".equals(op)) {
                    result = leftValue + rightValue;
                } else if ("-".equals(op)) {
                    result = leftValue - rightValue;
                } else if ("*".equals(op)) {
                    result = leftValue * rightValue;
                } else if ("/".equals(op)) {
                    result = leftValue / rightValue;
                } else if ("<".equals(op)) {
                    result = leftValue < rightValue ? 1 : 0;
                } else if (">".equals(op)) {
                    result = leftValue > rightValue ? 1 : 0;
                } else if ("<=".equals(op) || "=<".equals(op)) {
                    result = leftValue <= rightValue ? 1 : 0;
                } else if (">=".equals(op) || "=>".equals(op)) {
                    result = leftValue >= rightValue ? 1 : 0;
                } else if ("=".equals(op)) {
                    result = leftValue == rightValue ? 1 : 0;
                } else if ("<>".equals(op)) {
                    result = leftValue != rightValue ? 1 : 0;
                }
                return new PureBasicConstant("", PureBasicConstant.Type.FLOAT, Double.toString(result));
            }
        }

        // default case
        return new PureBasicConstant("", PureBasicConstant.Type.INTEGER, "0");
    }
}
