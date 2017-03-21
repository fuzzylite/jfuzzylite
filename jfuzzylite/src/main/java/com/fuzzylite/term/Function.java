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
package com.fuzzylite.term;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.factory.FunctionFactory;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 The Function class is a polynomial Term that represents a generic function `
 f : x \mapsto f(x) `. Every Function object has a public key-value map,
 namely Function::variables, that links variable names to fl::scalar values,
 which are utilized to replace the variable names for their respective values in
 the given formula whenever the function

 `f` is evaluated. Specifically, when the method Function::membership() is
 called, the name and value of the variable
 `x` are automatically loaded into the map. Furthermore, if an Engine is
 given, the names of its InputVariable%s and OutputVariable%s will also be
 automatically loaded into the map linking to their respective input values and
 (previously defuzzified) output values. The Function::variables need to be
 manually loaded whenever variables other than `x`, input variables, and
 output variables, are expressed in the given formula, always having in mind
 that (a) the map replaces existing keys, and (b) the variable `x`, and
 input variables and output variables of an engine will automatically be
 replaced and will also take precedence over previously loaded variables.

 Besides the use of Function as a linguistic Term, it is also utilized to
 convert the text of the Antecedent of a Rule, expressed in infix notation, into
 postfix notation.

 @see Term
 @see Variable
 @see FunctionFactory
 @see Antecedent::load()
 @since 4.0
 @author Juan Rada-Vilela, Ph.D.
 */
public class Function extends Term {

    /**
     The Element class represents a single element in a formula, be that either
     a function or an operator. If the Element represents a function, the
     function can be Unary or Binary, that is, the function take one or two
     parameters (respectively). Else, if the Element represents an operator, the
     parameters to be defined are its `arity`, its `precedence`, and its
     `associativity`.
     */
    public static class Element implements Op.Cloneable {

        /**
         Determines the type of the element
         */
        public enum Type {
            Operator, Function
        }

        private String name;
        private String description;
        private Type type;
        private Method method;
        private int precedence;
        private int associativity;

        public Element(String name, String description, Type type) {
            this(name, description, type, null);
        }

        public Element(String name, String description, Type type, Method method) {
            this(name, description, type, method, 0);
        }

        public Element(String name, String description, Type type, Method method, int precedence) {
            this(name, description, type, method, precedence, -1);
        }

        public Element(String name, String description, Type type, Method method,
                       int precedence, int associativity) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.method = method;
            this.precedence = precedence;
            this.associativity = associativity;
        }

        /**
         Gets the name of the element

         @return the name of the element
         */
        public String getName() {
            return name;
        }

        /**
         Sets the name of the element

         @param name is the name of the element
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         Gets the description of the element

         @return the description of the element
         */
        public String getDescription() {
            return description;
        }

        /**
         Sets the description of the element

         @param description is the description of the element
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         Gets a reference to the method

         @return a reference to the method
         */
        public Method getMethod() {
            return method;
        }

        /**
         Sets a reference to the method

         @param method is a reference to the method
         */
        public void setMethod(Method method) {
            this.method = method;
        }

        /**
         Gets the arity of the method, namely the number of arguments required
         by the method

         @return the arity of the method, namely the number of arguments required
         by the method
         */
        public int getArity() {
            return method.getParameterTypes().length;
        }

        /**
         Gets the type of the element

         @return the type of the element
         */
        public Type getType() {
            return type;
        }

        /**
         Sets the type of the element

         @param type is the type of the element
         */
        public void setType(Type type) {
            this.type = type;
        }

        /**
         Indicates whether the element is a Type::Operator

         @return whether the element is a Type::Operator
         */
        public boolean isOperator() {
            return this.type == Type.Operator;
        }

        /**
         Indicates whether the element is a Type::Function

         @return whether the element is a Type::Function
         */
        public boolean isFunction() {
            return this.type == Type.Function;
        }

        /**
         Gets the precedence of the element. Refer to
         (https://en.wikipedia.org/wiki/Order_of_operations) for information on
         which procedures are performed first in a given mathematical expression

         @return the precedence of the element
         */
        public int getPrecedence() {
            return precedence;
        }

        /**
         Sets the precedence of the element. Refer to
         (https://en.wikipedia.org/wiki/Order_of_operations) for information on
         which procedures are performed first in a given mathematical expression

         @param precedence is the precedence of the element
         */
        public void setPrecedence(int precedence) {
            this.precedence = precedence;
        }

        /**
         Gets the associativity of the element. Refer to
         (https://en.wikipedia.org/wiki/Operator_associativity) to determine how
         operators of the same precedence are grouped in the absence of
         parentheses

         @return the associativity of the element.
         */
        public int getAssociativity() {
            return associativity;
        }

        /**
         Sets the associativity of the element. Refer to
         (https://en.wikipedia.org/wiki/Operator_associativity) to determine how
         operators of the same precedence are grouped in the absence of
         parentheses

         @param associativity is the associativity of the element.
         */
        public void setAssociativity(int associativity) {
            this.associativity = associativity;
        }

        /**
         Creates a clone of the element.

         @return a clone of the element
         */
        @Override
        public Element clone() throws CloneNotSupportedException {
            return (Element) super.clone();
        }

    }

    /**
     The Node class structures a binary tree by storing pointers to a left Node
     and a right Node, and storing its content as a Function::Element, the name
     of an InputVariable or OutputVariable, or a constant value.
     */
    public static class Node implements Op.Cloneable {

        public Element element = null;
        public String variable = "";
        public double value = Double.NaN;
        public Node left = null, right = null;

        public Node(Element element) {
            this.element = element;
        }

        public Node(String variable) {
            this.variable = variable;
        }

        public Node(double value) {
            this.value = value;
        }

        /**
         Creates a clone of the node.

         @return a clone of the node

         @throws CloneNotSupportedException
         */
        @Override
        public Node clone() throws CloneNotSupportedException {
            Node result = (Node) super.clone();
            if (element != null) {
                result.element = element.clone();
            }
            if (left != null) {
                result.left = left.clone();
            }
            if (right != null) {
                result.right = right.clone();
            }
            return result;
        }

        /**
         Evaluates the node and substitutes the variables therein for the values
         passed in the map. The expression tree is evaluated recursively.

         @param localVariables is a map of substitutions of variable names for
         floating-point values
         @return a floating-point value indicating the result of the evaluation
         of the node
         */
        public double evaluate(Map<String, Double> localVariables) {
            Double result = Double.NaN;
            if (element != null) {
                try {
                    switch (element.getArity()) {
                        case 0:
                            result = (Double) element.getMethod().invoke(null);
                            break;
                        case 1:
                            result = (Double) element.getMethod().invoke(null,
                                    left.evaluate(localVariables));
                            break;
                        case 2:
                            result = (Double) element.getMethod().invoke(null,
                                    right.evaluate(localVariables),
                                    left.evaluate(localVariables));
                            break;
                        default:
                            throw new RuntimeException(String.format(
                                    "[function error] <%d>-ary element <%s> is not supported, "
                                            + "only unary and binary elements are",
                                    element.getArity(), element.toString()));
                    }
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new RuntimeException("[function error] exception thrown "
                            + "invoking element <" + element.getName() + ">\n\t"
                            + ex.toString(), ex);
                }
            } else if (variable != null && !variable.isEmpty()) {
                if (localVariables == null) {
                    throw new RuntimeException("[function error] expected a map of "
                            + "variables, but none was provided");
                }
                Double variableValue = localVariables.get(variable);
                if (variableValue == null) {
                    throw new RuntimeException("[function error] variable "
                            + "<" + variable + "> not registered in map");
                }
                result = variableValue;
            } else {
                result = value;
            }
            if (FuzzyLite.isDebugging()) {
                FuzzyLite.logger().fine(String.format("%s = %s", toPostfix(), Op.str(result)));
            }
            return result;
        }

        /**
         Returns a string with the name of the element, the name of the
         variable, or the constant value, accordingly.

         @return a string with the name of the element, the name of the variable,
         or the constant value, accordingly.
         */
        @Override
        public String toString() {
            String result;
            if (element != null) {
                result = element.getName();
            } else if (variable != null && !variable.isEmpty()) {
                result = variable;
            } else {
                result = Op.str(value);
            }
            return result;
        }

        /**
         Returns a prefix representation of the expression tree

         @return a prefix representation of the expression tree
         */
        public String toPrefix() {
            return this.toPrefix(this);
        }

        /**
         Returns a prefix representation of the expression tree under the given
         node

         @param node is the node to start the prefix representation from. If the
         node is null, then the starting point is `this` node
         @return a prefix representation of the expression tree under the given
         node
         */
        public String toPrefix(Node node) {
            if (!Double.isNaN(node.value)) {
                return Op.str(node.value);
            }
            if (!node.variable.isEmpty()) {
                return node.variable;
            }

            String result = node.toString();
            if (node.left != null) {
                result += " " + this.toPrefix(node.left);
            }
            if (node.right != null) {
                result += " " + this.toPrefix(node.right);
            }
            return result;
        }

        /**
         Returns an infix representation of the expression tree

         @return an infix representation of the expression tree
         */
        public String toInfix() {
            return this.toInfix(this);
        }

        /**
         Returns an infix representation of the expression tree under the given
         node

         @param node is the node to start the infix representation from. If the
         node is null, then the starting point is `this` node
         @return an infix representation of the expression tree under the given
         node
         */
        public String toInfix(Node node) {
            if (!Double.isNaN(node.value)) {
                return Op.str(node.value);
            }
            if (!node.variable.isEmpty()) {
                return node.variable;
            }

            String result = "";
            if (node.left != null) {
                result += this.toInfix(node.left) + " ";
            }
            result += node.toString();
            if (node.right != null) {
                result += " " + this.toInfix(node.right);
            }
            return result;
        }

        /**
         Returns a postfix representation of the expression tree

         @return a postfix representation of the expression tree
         */
        public String toPostfix() {
            return this.toPostfix(this);
        }

        /**
         Returns a postfix representation of the expression tree under the given
         node

         @param node is the node to start the postfix representation from. If the
         node is null, then the starting point is `this` node
         @return a postfix representation of the expression tree under the given
         node
         */
        public String toPostfix(Node node) {
            if (!Double.isNaN(node.value)) {
                return Op.str(node.value);
            }
            if (!node.variable.isEmpty()) {
                return node.variable;
            }
            String result = "";
            if (node.left != null) {
                result += this.toPostfix(node.left) + " ";
            }
            if (node.right != null) {
                result += this.toPrefix(node.right) + " ";
            }
            result += node.toString();
            return result;
        }

    }

    /**
     Function term
     */
    private Node root;
    private String formula;
    private Engine engine;
    private Map<String, Double> variables;

    public Function() {
        this("");
    }

    public Function(String name) {
        this(name, "", null);
    }

    public Function(String name, String formula, Engine engine) {
        this.name = name;
        this.root = null;
        this.formula = formula;
        this.engine = engine;
        this.variables = new HashMap<String, Double>();
    }

    @Override
    public String parameters() {
        return formula;
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        load(parameters);
    }

    /**
     Computes the membership function value of `x` at the root node. If the
     engine has been set, the current values of the input variables and output
     variables are added to the map of Function::variables. In addition, the
     variable `x` will also be added to the map.

     @param x
     @return the membership function value of `x` at the root node
     */
    @Override
    public double membership(double x) {
        if (this.root == null) {
            throw new RuntimeException(String.format(
                    "[function error] function <%s> not loaded.", formula));
        }
        if (this.engine != null) {
            for (InputVariable inputVariable : this.engine.getInputVariables()) {
                this.variables.put(inputVariable.getName(), inputVariable.getValue());
            }
            for (OutputVariable outputVariable : this.engine.getOutputVariables()) {
                this.variables.put(outputVariable.getName(), outputVariable.getValue());
            }
        }
        this.variables.put("x", x);
        return evaluate(this.variables);
    }

    /**
     Computes the function value of this term using the map of variable
     substitutions in this function.

     @return the function value of this term using the map of variable
     substitutions in this function
     */
    public double evaluate() {
        return this.evaluate(this.variables);
    }

    /**
     Computes the function value of this term using the given map of variable
     substitutions.

     @param localVariables is a map of substitution variables
     @return the function value of this term using the given map of variable
     substitutions.
     */
    public double evaluate(Map<String, Double> localVariables) {
        if (this.root == null) {
            throw new RuntimeException("[function error] evaluation failed " +
                    "because function is not loaded");
        }
        return this.root.evaluate(localVariables);
    }

    /**
     Creates a Function term with the given parameters

     @param name is the name of the term
     @param formula is the formula defining the membership function
     @param engine is the engine to which the Function can have access
     @return a Function term configured with the given parameters

     @throws RuntimeException if the formula has a syntax error
     */
    public static Function create(String name, String formula, Engine engine) {
        Function result = new Function(name);
        try {
            result.load(formula, engine);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    @Override
    public Function clone() throws CloneNotSupportedException {
        Function result = (Function) super.clone();
        if (this.root != null) {
            result.root = this.root.clone();
        }
        result.variables = new HashMap<String, Double>(this.variables);
        return result;
    }

    @Override
    public void updateReference(Engine engine) {
        setEngine(engine);
        try {
            load();
        } finally {
            //ignore
        }
    }

    /**
     Indicates whether the formula is loaded

     @return whether the formula is loaded
     */
    public boolean isLoaded() {
        return this.root != null;
    }

    /**
     Unloads the formula and resets the map of substitution variables.
     */
    public void unload() {
        this.root = null;
        this.variables.clear();
    }

    /**
     Loads the current formula expressed in infix notation

     @throws RuntimeException if the formula has syntax errors
     */
    public void load() {
        load(this.formula, this.engine);
    }

    /**
     Loads the given formula expressed in infix notation

     @param formula is the right-hand side of a mathematical equation
     @throws RuntimeException if the formula has syntax errors
     */
    public void load(String formula) {
        load(formula, this.engine);
    }

    /**
     Loads the given formula expressed in infix notation, and sets the engine
     holding the variables to which the formula refers.

     @param formula is the right-hand side of a mathematical equation expressed
     in infix notation
     @param engine is the engine to which the formula can refer
     @throws RuntimeException if the formula has syntax errors
     */
    public void load(String formula, Engine engine) {
        this.root = parse(formula);
        this.formula = formula;
        this.engine = engine;
    }

    /**
     Translates the given formula to postfix notation

     @param formula is the right-hand side of a mathematical equation expressed
     in infix notation
     @return the formula represented in postfix notation

     @throws RuntimeException if the formula has syntax errors
     */
    public String toPostfix(String formula) {
        FunctionFactory factory = FactoryManager.instance().function();
        //Space the operator to tokenize easier
        Set<String> toSpace = factory.availableOperators();
        toSpace.remove(Rule.FL_AND);
        toSpace.remove(Rule.FL_OR);
        toSpace.add("(");
        toSpace.add(")");
        toSpace.add(",");
        String spacedFormula = formula;
        for (String operator : toSpace) {
            spacedFormula = spacedFormula.replace(operator, " " + operator + " ");
        }
        FuzzyLite.logger().fine(spacedFormula);

        //Tokenizer
        Deque<String> queue = new ArrayDeque<String>();
        Deque<String> stack = new ArrayDeque<String>();

        StringTokenizer tokenizer = new StringTokenizer(spacedFormula);
        String token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            Element element = factory.getObject(token);

            boolean isOperand = element == null && !"(".equals(token) && !")".equals(token)
                    && !",".equals(token);

            if (isOperand) {
                FuzzyLite.logger().fine(token + " is operand");
                queue.offer(token);

            } else if (element != null && element.isFunction()) {
                FuzzyLite.logger().fine(token + " is function");
                stack.push(token);

            } else if (",".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    queue.offer(stack.pop());
                }
                if (stack.isEmpty() || !"(".equals(stack.peek())) {
                    throw new RuntimeException(String.format(
                            "[parsing error] mismatching parentheses in: %s", formula));
                }

            } else if (element != null && element.isOperator()) {
                FuzzyLite.logger().fine(token + " is operator");
                Element op1 = element;
                for (; ; ) {
                    Element op2 = null;
                    if (!stack.isEmpty()) {
                        op2 = factory.getObject(stack.peek());
                    }
                    if (op2 == null) {
                        break;
                    }

                    if ((op1.associativity < 0 && op1.precedence == op2.precedence)
                            || op1.precedence < op2.precedence) {
                        queue.offer(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(token);

            } else if ("(".equals(token)) {
                stack.push(token);

            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    queue.offer(stack.pop());
                }
                if (stack.isEmpty() || !"(".equals(stack.peek())) {
                    throw new RuntimeException(String.format(
                            "[parsing error] mismatching parentheses in: %s", formula));
                }
                stack.pop(); //get rid of "("

                Element top = null;
                if (!stack.isEmpty()) {
                    top = factory.getObject(stack.peek());
                }
                if (top != null && top.isFunction()) {
                    queue.offer(stack.pop());
                }
            } else {
                throw new RuntimeException(String.format(
                        "[parsing error] unexpected error with token <%s>", token));
            }
        }

        while (!stack.isEmpty()) {
            if ("(".equals(stack.peek()) || ")".equals(stack.peek())) {
                throw new RuntimeException(String.format(
                        "[parsing error] mismatching parentheses in: %s", formula));
            }
            queue.offer(stack.pop());
        }

        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            result.append(queue.poll());
            if (!queue.isEmpty()) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     Creates a node representing a binary expression tree from the given formula

     @param text is the right-hand side of a mathematical equation expressed in
     infix notation
     @return a node representing a binary expression tree from the given formula

     @throws RuntimeException if the formula has syntax errors
     */
    public Node parse(String text) {
        if (text.isEmpty()) {
            return null;
        }
        String postfix = toPostfix(text);

        Deque<Node> stack = new ArrayDeque<Node>();

        StringTokenizer tokenizer = new StringTokenizer(postfix);
        String token;
        FunctionFactory factory = FactoryManager.instance().function();
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            Element element = factory.getObject(token);

            boolean isOperand = element == null && !"(".equals(token) && !")".equals(token)
                    && !",".equals(token);

            if (element != null) {
                if (element.getArity() > stack.size()) {
                    throw new RuntimeException(String.format("[function error] " +
                                    "operator <%s> has arity <%d>, "
                                    + "but <%d> elements are available: (%s)",
                            element.getName(), element.getArity(), stack.size(),
                            Op.join(stack, ", ")));
                }

                Node node;
                try {
                    node = new Node(element.clone());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                node.left = stack.pop();
                if (element.getArity() == 2) {
                    node.right = stack.pop();
                }
                stack.push(node);
            } else if (isOperand) {
                Node node;
                try {
                    double value = Op.toDouble(token);
                    node = new Node(value);
                } catch (Exception ex) {
                    node = new Node(token);
                }
                stack.push(node);
            }
        }
        if (stack.size() != 1) {
            throw new RuntimeException(String.format(
                    "[function error] ill-formed formula <%s> due to: <%s>",
                    text, Op.join(stack, ";")));
        }
        return stack.pop();
    }

    /**
     Gets the formula of the function

     @return the formula of the function
     */
    public String getFormula() {
        return formula;
    }

    /**
     Sets the formula of the function

     @param formula is the formula of the function
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     Gets the engine to which the formula can refer

     @return the engine to which the formula can refer
     */
    public Engine getEngine() {
        return engine;
    }

    /**
     Sets the engine to which the formula can refer

     @param engine is the engine to which the formula can refer
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     Gets the root node of the expression tree defining the Function. The root
     is null if the formula has not been loaded.

     @return the root node of the expression tree defining the Function, or null
     if the formula has not been loaded
     */
    public Node getRoot() {
        return root;
    }

    /**
     Gets the map of variables to replace their names for their respective
     floating-point values

     @return the map of variables to replace their names for their respective
     floating-point values
     */
    public Map<String, Double> getVariables() {
        return variables;
    }
}
