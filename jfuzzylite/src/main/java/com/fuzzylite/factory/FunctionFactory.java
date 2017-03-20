/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.factory;

import com.fuzzylite.Op;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.term.Function;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 The FunctionFactory class is a CloningFactory of operators and functions
 utilized by the Function term.

 @author Juan Rada-Vilela, Ph.D.
 @see Function
 @see Element
 @see CloningFactory
 @see FactoryManager
 @since 5.0
 */
public class FunctionFactory extends CloningFactory<Function.Element> {

    public FunctionFactory() {
        registerOperators();
        registerFunctions();
    }

    private void registerOperators() {
        int p = 100;
        try {
            //OPERATORS:
            //First order: not, negate
            registerObject("!", new Function.Element("!", "Logical NOT", Function.Element.Type.Operator,
                    Op.class.getMethod("logicalNot", double.class), p, 1));
            registerObject("~", new Function.Element("~", "Negation", Function.Element.Type.Operator,
                    Op.class.getMethod("negate", double.class), p, 1));

            p -= 10;
            //Second order: power
            registerObject("^", new Function.Element("^", "Power", Function.Element.Type.Operator,
                    Math.class.getMethod("pow", double.class, double.class), p, 1));

            p -= 10;
            //Third order: Multiplication, Division, and Modulo
            registerObject("*", new Function.Element("*", "Multiplication", Function.Element.Type.Operator,
                    Op.class.getMethod("multiply", double.class, double.class), p));

            registerObject("/", new Function.Element("/", "Division", Function.Element.Type.Operator,
                    Op.class.getMethod("divide", double.class, double.class), p));

            registerObject("%", new Function.Element("%", "Modulo", Function.Element.Type.Operator,
                    Op.class.getMethod("modulo", double.class, double.class), p));

            p -= 10;
            //Fourth order: Addition, Subtraction
            registerObject("+", new Function.Element("+", "Addition", Function.Element.Type.Operator,
                    Op.class.getMethod("add", double.class, double.class), p));

            registerObject("-", new Function.Element("-", "Subtraction", Function.Element.Type.Operator,
                    Op.class.getMethod("subtract", double.class, double.class), p));

            p -= 10;
            //Fifth order: logical and
            registerObject(Rule.FL_AND, new Function.Element(Rule.FL_AND, "Logical AND", Function.Element.Type.Operator,
                    Op.class.getMethod("logicalAnd", double.class, double.class), p));
            //Sixth order: logical or
            p -= 10;
            registerObject(Rule.FL_OR, new Function.Element(Rule.FL_OR, "Logical OR", Function.Element.Type.Operator,
                    Op.class.getMethod("logicalOr", double.class, double.class), p));
        } catch (Exception ex) {
            throw new RuntimeException("[factory error] unable to register operator: " + ex.toString());
        }
    }

    private void registerFunctions() {
        try {
            registerObject("gt", new Function.Element("gt", "Greater than (>)", Function.Element.Type.Function,
                    Op.class.getMethod("gt", double.class, double.class)));
            registerObject("ge", new Function.Element("ge", "Greater than or equal to (>=)", Function.Element.Type.Function,
                    Op.class.getMethod("ge", double.class, double.class)));
            registerObject("eq", new Function.Element("eq", "Equal to (==)", Function.Element.Type.Function,
                    Op.class.getMethod("eq", double.class, double.class)));
            registerObject("neq", new Function.Element("neq", "Not equal to (!=)", Function.Element.Type.Function,
                    Op.class.getMethod("neq", double.class, double.class)));
            registerObject("le", new Function.Element("le", "Less than or equal to (<=)", Function.Element.Type.Function,
                    Op.class.getMethod("le", double.class, double.class)));
            registerObject("lt", new Function.Element("lt", "Less than (<)", Function.Element.Type.Function,
                    Op.class.getMethod("lt", double.class, double.class)));

            registerObject("acos", new Function.Element("acos", "Inverse cosine", Function.Element.Type.Function,
                    Math.class.getMethod("acos", double.class)));
            registerObject("asin", new Function.Element("asin", "Inverse sine", Function.Element.Type.Function,
                    Math.class.getMethod("asin", double.class)));
            registerObject("atan", new Function.Element("atan", "Inverse tangent", Function.Element.Type.Function,
                    Math.class.getMethod("atan", double.class)));

            registerObject("ceil", new Function.Element("ceil", "Ceiling", Function.Element.Type.Function,
                    Math.class.getMethod("ceil", double.class)));
            registerObject("cos", new Function.Element("cos", "Cosine", Function.Element.Type.Function,
                    Math.class.getMethod("cos", double.class)));
            registerObject("cosh", new Function.Element("cosh", "Hyperbolic cosine", Function.Element.Type.Function,
                    Math.class.getMethod("cosh", double.class)));
            registerObject("exp", new Function.Element("exp", "Exponential", Function.Element.Type.Function,
                    Math.class.getMethod("exp", double.class)));
            registerObject("fabs", new Function.Element("fabs", "Absolute", Function.Element.Type.Function,
                    Math.class.getMethod("abs", double.class)));
            registerObject("abs", new Function.Element("abs", "Absolute", Function.Element.Type.Function,
                    Math.class.getMethod("abs", double.class)));
            registerObject("floor", new Function.Element("floor", "Floor", Function.Element.Type.Function,
                    Math.class.getMethod("floor", double.class)));
            registerObject("log", new Function.Element("log", "Natural logarithm", Function.Element.Type.Function,
                    Math.class.getMethod("log", double.class)));
            registerObject("log10", new Function.Element("log10", "Common logarithm", Function.Element.Type.Function,
                    Math.class.getMethod("log10", double.class)));

            registerObject("sin", new Function.Element("sin", "Sine", Function.Element.Type.Function,
                    Math.class.getMethod("sin", double.class)));
            registerObject("sinh", new Function.Element("sinh", "Hyperbolic sine", Function.Element.Type.Function,
                    Math.class.getMethod("sinh", double.class)));
            registerObject("sqrt", new Function.Element("sqrt", "Square root", Function.Element.Type.Function,
                    Math.class.getMethod("sqrt", double.class)));
            registerObject("tan", new Function.Element("tan", "Tangent", Function.Element.Type.Function,
                    Math.class.getMethod("tan", double.class)));
            registerObject("tanh", new Function.Element("tanh", "Hyperbolic tangent", Function.Element.Type.Function,
                    Math.class.getMethod("tanh", double.class)));

            registerObject("log1p", new Function.Element("log1p", "Natural logarithm plus one", Function.Element.Type.Function,
                    Math.class.getMethod("log1p", double.class)));
            registerObject("atan", new Function.Element("log1p", "Natural logarithm plus one", Function.Element.Type.Function,
                    Math.class.getMethod("atan", double.class)));

            registerObject("pow", new Function.Element("pow", "Power", Function.Element.Type.Function,
                    Math.class.getMethod("pow", double.class, double.class)));
            registerObject("atan2", new Function.Element("atan2", "Inverse tangent (y/x)", Function.Element.Type.Function,
                    Math.class.getMethod("atan2", double.class, double.class)));
            registerObject("fmod", new Function.Element("fmod", "Floating-point remainder", Function.Element.Type.Function,
                    Op.class.getMethod("modulo", double.class, double.class)));

            //not found in Java
            //this.functions.put("acosh", new BuiltInFunction("acosh",  & (acosh)));
            //this.functions.put("asinh", new BuiltInFunction("asinh",  & (asinh)));
            //this.functions.put("atanh", new BuiltInFunction("atanh",  & (atanh)));
        } catch (Exception ex) {
            throw new RuntimeException("[factory error] unable to register function: " + ex.toString());
        }
    }

    /**
     Returns a set of the operators available

     @return a set of the operators available
     */
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

    /**
     Returns a set of the functions available

     @return a set of the functions available
     */
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
