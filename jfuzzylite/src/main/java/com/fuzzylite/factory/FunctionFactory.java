/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.factory;

import com.fuzzylite.Op;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.term.Function;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author jcrada
 */
public class FunctionFactory extends CloningFactory<Function.Element> implements Op.Cloneable {

    public FunctionFactory() {
        registerOperators();
        registerFunctions();
    }

    private void registerOperators() {
        int p = 100;
        try {
            //OPERATORS:
            //First order: not, negate
            registerObject("!", new Function.Element("!", "Logical NOT", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("logicalNot", double.class), p, 1));
            registerObject("~", new Function.Element("~", "Negation", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("negate", double.class), p, 1));

            p -= 10;
            //Second order: power
            registerObject("^", new Function.Element("^", "Power", Function.Element.Type.OPERATOR,
                    Math.class.getMethod("pow", double.class, double.class), p, 1));

            p -= 10;
            //Third order: Multiplication, Division, and Modulo
            registerObject("*", new Function.Element("*", "Multiplication", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("multiply", double.class, double.class), p));

            registerObject("/", new Function.Element("/", "Division", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("divide", double.class, double.class), p));

            registerObject("%", new Function.Element("%", "Modulo", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("modulo", double.class, double.class), p));

            p -= 10;
            //Fourth order: Addition, Subtraction
            registerObject("+", new Function.Element("+", "Addition", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("add", double.class, double.class), p));

            registerObject("-", new Function.Element("-", "Subtraction", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("subtract", double.class, double.class), p));

            p -= 10;
            //Fifth order: logical and
            registerObject(Rule.FL_AND, new Function.Element(Rule.FL_AND, "Logical AND", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("logicalAnd", double.class, double.class), p));
            //Sixth order: logical or
            p -= 10;
            registerObject(Rule.FL_OR, new Function.Element(Rule.FL_OR, "Logical OR", Function.Element.Type.OPERATOR,
                    Op.class.getMethod("logicalOr", double.class, double.class), p));
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "[factory error] unable to register operator at level %d", p), ex);
        }
    }

    private void registerFunctions() {
        try {
            registerObject("gt", new Function.Element("gt", "Greater than (>)", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("isGt", double.class, double.class)));
            registerObject("ge", new Function.Element("ge", "Greater than or equal to (>=)", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("isGE", double.class, double.class)));
            registerObject("eq", new Function.Element("eq", "Equal to (==)", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("isEq", double.class, double.class)));
            registerObject("neq", new Function.Element("neq", "Not equal to (!=)", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("isNEq", double.class, double.class)));
            registerObject("le", new Function.Element("le", "Less than or equal to (<=)", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("isLE", double.class, double.class)));
            registerObject("lt", new Function.Element("lt", "Less than (<)", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("isLt", double.class, double.class)));

            registerObject("acos", new Function.Element("acos", "Inverse cosine", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("acos", double.class)));
            registerObject("asin", new Function.Element("asin", "Inverse sine", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("asin", double.class)));
            registerObject("atan", new Function.Element("atan", "Inverse tangent", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("atan", double.class)));

            registerObject("ceil", new Function.Element("ceil", "Ceiling", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("ceil", double.class)));
            registerObject("cos", new Function.Element("cos", "Cosine", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("cos", double.class)));
            registerObject("cosh", new Function.Element("cosh", "Hyperbolic cosine", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("cosh", double.class)));
            registerObject("exp", new Function.Element("exp", "Exponential", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("exp", double.class)));
            registerObject("fabs", new Function.Element("fabs", "Absolute", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("abs", double.class)));
            registerObject("floor", new Function.Element("floor", "Floor", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("floor", double.class)));
            registerObject("log", new Function.Element("log", "Natural logarithm", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("log", double.class)));
            registerObject("log10", new Function.Element("log10", "Common logarithm", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("log10", double.class)));

            registerObject("sin", new Function.Element("sin", "Sine", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("sin", double.class)));
            registerObject("sinh", new Function.Element("sinh", "Hyperbolic sine", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("sinh", double.class)));
            registerObject("sqrt", new Function.Element("sqrt", "Square root", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("sqrt", double.class)));
            registerObject("tan", new Function.Element("tan", "Tangent", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("tan", double.class)));
            registerObject("tanh", new Function.Element("tanh", "Hyperbolic tangent", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("tanh", double.class)));

            registerObject("log1p", new Function.Element("log1p", "Natural logarithm plus one", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("log1p", double.class)));
            registerObject("atan", new Function.Element("log1p", "Natural logarithm plus one", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("atan", double.class)));

            registerObject("pow", new Function.Element("pow", "Power", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("pow", double.class, double.class)));
            registerObject("atan2", new Function.Element("atan2", "Inverse tangent (y/x)", Function.Element.Type.FUNCTION,
                    Math.class.getMethod("atan2", double.class, double.class)));
            registerObject("fmod", new Function.Element("fmod", "Floating-point remainder", Function.Element.Type.FUNCTION,
                    Op.class.getMethod("modulo", double.class, double.class)));

            //not found in Java
            //this.functions.put("acosh", new BuiltInFunction("acosh",  & (acosh)));
            //this.functions.put("asinh", new BuiltInFunction("asinh",  & (asinh)));
            //this.functions.put("atanh", new BuiltInFunction("atanh",  & (atanh)));
        } catch (Exception ex) {
            throw new RuntimeException("[factory error] unable to register function", ex);
        }

    }

    public Set<String> availableOperators() {
        Set<String> operators = new HashSet<String>(this.getObjects().keySet());
        Iterator<String> it = operators.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!getObject(key).isOperator()) {
                it.remove();
            }
        }
        return operators;
    }

    public Set<String> availableFunctions() {
        Set<String> functions = new HashSet<String>(this.getObjects().keySet());
        Iterator<String> it = functions.iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!getObject(key).isFunction()) {
                it.remove();
            }
        }
        return functions;
    }

    @Override
    public FunctionFactory clone() throws CloneNotSupportedException {
        return (FunctionFactory) super.clone();
    }

}
