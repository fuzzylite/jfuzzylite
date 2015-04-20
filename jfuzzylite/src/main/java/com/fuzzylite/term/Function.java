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
package com.fuzzylite.term;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
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
import java.util.logging.Logger;

public class Function extends Term {

    public static class Element implements Op.Cloneable {

        public enum Type {

            OPERATOR, FUNCTION
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public int getArity() {
            return method.getParameterTypes().length;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public boolean isOperator() {
            return this.type == Type.OPERATOR;
        }

        public boolean isFunction() {
            return this.type == Type.FUNCTION;
        }

        public int getPrecedence() {
            return precedence;
        }

        public void setPrecedence(int precedence) {
            this.precedence = precedence;
        }

        public int getAssociativity() {
            return associativity;
        }

        public void setAssociativity(int associativity) {
            this.associativity = associativity;
        }

        @Override
        public Element clone() throws CloneNotSupportedException {
            return (Element) super.clone();
        }

    }

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
                } catch (Exception ex) {
                    throw new RuntimeException("[function error] exception thrown "
                            + "invoking element <" + element.getName() + ">", ex);
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
            FuzzyLite.logger().fine(String.format("%s = %s", toPostfix(), str(result)));
            return result;
        }

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

        public String toPrefix() {
            return this.toPrefix(this);
        }

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

        public String toInfix() {
            return this.toInfix(this);
        }

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

        public String toPostfix() {
            return this.toPostfix(this);
        }

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
     * Function term
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

    @Override
    public double membership(double x) {
        if (this.root == null) {
            throw new RuntimeException(String.format(
                    "[function error] function <%s> not loaded.", formula));
        }
        if (this.engine != null) {
            for (InputVariable inputVariable : this.engine.getInputVariables()) {
                this.variables.put(inputVariable.getName(), inputVariable.getInputValue());
            }
            for (OutputVariable outputVariable : this.engine.getOutputVariables()) {
                this.variables.put(outputVariable.getName(), outputVariable.getOutputValue());
            }
        }
        this.variables.put("x", x);
        return evaluate(this.variables);
    }

    public double evaluate() {
        return this.evaluate(this.variables);
    }

    public double evaluate(Map<String, Double> localVariables) {
        if (this.root == null) {
            throw new RuntimeException("[function error] evaluation failed because function is not loaded");
        }
        return this.root.evaluate(localVariables);
    }

    public static Function create(String name, String formula, Engine engine) {
        Function result = new Function(name);
        try {
            result.load(formula, engine);
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

    public boolean isLoaded() {
        return this.root != null;
    }

    public void unload() {
        this.root = null;
        this.variables.clear();
    }

    public void load() {
        load(this.formula, this.engine);
    }

    public void load(String formula) {
        load(formula, this.engine);
    }

    public void load(String formula, Engine engine) {
        this.root = parse(formula);
        this.formula = formula;
        this.engine = engine;
    }

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
                for (;;) {
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
                while (!(stack.isEmpty() || "(".equals(stack.peek()))) {
                    queue.offer(stack.pop());
                }
                if (stack.isEmpty() || !"(".equals(stack.peek())) {
                    throw new RuntimeException(String.format(
                            "[parsing error] mismatching parentheses in: %s", formula));
                }
                stack.pop(); //get rid of "("

                if (!stack.isEmpty() && factory.getObject(stack.peek()).isFunction()) {
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
                    throw new RuntimeException(String.format("[function error] operator <%s> has arity <%d>, "
                            + "but <%d> elements are available", element.getName(), element.getArity(), stack.size()));
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

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Node getRoot() {
        return root;
    }

    public Map<String, Double> getVariables() {
        return variables;
    }

    public static void main(String[] args) throws Exception {
        FuzzyLite.setDebug(true);
        Logger log = FuzzyLite.logger();

        Function f = new Function();
        String text = "3+4*2/(1-5)^2^3";
        //String formula = "3+4*2/2";
//        logger.info(f.toPostfix(text));
//        logger.info(f.parse(text).toInfix());
//        logger.info(Op.str(f.parse(text).evaluate(f.getVariables())));
//        f.load(text);
//        logger.info(">>>" + Op.str(f.evaluate()));

        f.getVariables().put("y", 1.0);
        text = "sin (y*x)^2/x";
        log.info("post: " + f.toPostfix(text));
        log.info("pre: " + f.parse(text).toPrefix());
        log.info("in: " + f.parse(text).toInfix());
        log.info("pos: " + f.parse(text).toPostfix());
        f.load(text);
        log.info("Result: " + Op.str(f.membership(1)));

        text = "(Temperature is High and Oxigen is Low) or "
                + "(Temperature is Low and (Oxigen is Low or Oxigen is High))";
        log.info(f.toPostfix(text));

        text = "term1 is t1 or term2 is t2 and term3 is t3";
        log.info(f.toPostfix(text));
        
        f.variables.put("pi", 3.14);
        text = "-5 *4/sin(-pi/2)";
        log.info(f.toPostfix(text));
        try {
            log.info(Op.str(f.parse(text).evaluate(f.getVariables())));
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        text = "~5 *4/sin(~pi/2)";
        log.info(f.toPostfix(text));
        try {
            log.info(Op.str(f.parse(text).evaluate(f.variables)));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
