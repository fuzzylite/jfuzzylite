/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.term;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 *
 * @author jcrada
 */
public class Function extends Term {

    public static abstract class Element {

        public String name;
        public Method method;

        public int getArity() {
            return method.getParameterTypes().length;
        }
    }

    public static class Operator extends Element {

        public int precedence, associativity;

        public Operator() {
            this("", null, 0);
        }

        public Operator(String name, Method operator, int precedence) {
            this(name, operator, precedence, -1);
        }

        public Operator(String name, Method operator, int precedence, int associativity) {
            this.name = name;
            this.method = operator;
            this.precedence = precedence;
            this.associativity = associativity;
        }
    }

    public static class BuiltInFunction extends Element {

        public int associativity;

        public BuiltInFunction() {
            this("", null);
        }

        public BuiltInFunction(Method function) {
            this(function.getName(), function);
        }

        public BuiltInFunction(String name, Method function) {
            this(name, function, -1);
        }

        public BuiltInFunction(String name, Method function, int associativity) {
            this.name = name;
            this.method = function;
            this.associativity = associativity;
        }
    }

    public static class Node {

        public Operator operator = null;
        public BuiltInFunction function = null;
        public String variable = "";
        public double value = Double.NaN;
        public Node left = null, right = null;

        public Node(Operator operator) {
            this.operator = operator;
        }

        public Node(BuiltInFunction function) {
            this.function = function;
        }

        public Node(String variable) {
            this.variable = variable;
        }

        public Node(double value) {
            this.value = value;
        }

        public double evaluate(Map<String, Double> localVariables) {
            double result = Double.NaN;
            if (operator != null || function != null) {
                Element element = function;
                if (operator != null) {
                    element = operator;
                }

                try {
                    switch (element.getArity()) {
                        case 0:
                            result = (double) element.method.invoke(null);
                            break;
                        case 1:
                            result = (double) element.method.invoke(null,
                                    left.evaluate(localVariables));
                            break;
                        case 2:
                            result = (double) element.method.invoke(null,
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
                            + "invoking element <" + element.name + ">", ex);
                }
            } else if (variable != null && !variable.isEmpty()) {
                if (localVariables == null || localVariables.isEmpty()) {
                    throw new RuntimeException("[function error] expected a map of "
                            + "variables, but none was provided");
                }
                Double variableValue = localVariables.get(variable);
                if (variableValue == null) {
                    throw new RuntimeException("[function error] variable "
                            + "<" + variable + "> not registered in map");
                }
                result = variableValue.doubleValue();
            } else {
                result = value;
            }
            FuzzyLite.logger().finest(String.format("%s = %s", toPostfix(), str(result)));
            return result;
        }

        @Override
        public String toString() {
            String result;
            if (operator != null) {
                result = operator.name;
            } else if (function != null) {
                result = function.name;
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
    protected String formula;
    protected Engine engine;
    protected Node root;
    protected Map<String, Double> variables;
    protected Map<String, Operator> operators;
    protected Map<String, BuiltInFunction> functions;

    public Function() {
        this("");
    }

    public Function(String name) {
        this(name, "", null);
    }

    public Function(String name, String formula, Engine engine) {
        this.name = name;
        this.formula = formula;
        this.engine = engine;
        this.root = null;
        this.variables = new HashMap<>();
        this.operators = new HashMap<>();
        this.functions = new HashMap<>();
        this.loadOperators();
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
        setFormula(parameters);
    }

    public void load() {
        load(this.formula, this.engine);
    }

    public void load(String formula) {
        load(formula, null);
    }

    public void load(String formula, Engine engine) {
        this.root = parse(formula);
        this.formula = formula;
        this.engine = engine;
    }

    @Override
    public double membership(double x) {
        if (this.root == null) {
            return Double.NaN;
        }
        if (this.engine != null) {
            for (InputVariable inputVariable : this.engine.getInputVariables()) {
                this.variables.put(inputVariable.getName(), inputVariable.getInputValue());
            }
            for (OutputVariable outputVariable : this.engine.getOutputVariables()) {
                this.variables.put(outputVariable.getName(), outputVariable.getLastValidOutput());
            }
        }
        this.variables.put("x", x);
        return evaluate();
    }

    public double evaluate() {
        if (this.root == null) {
            throw new RuntimeException("[function error] evaluation failed because function is not loaded");
        }
        return this.root.evaluate(this.variables);
    }

    public static Function create(String name, String formula, Engine engine) {
        return create(name, formula, engine, true);
    }

    public static Function create(String name, String formula,
            Engine engine, boolean requiresFunctions) {
        Function result = new Function(name);
        if (requiresFunctions) {
            result.loadBuiltInFunctions();
        }
        try {
            result.load(formula, engine);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    private void loadOperators() {
        int p = 7;
        try {
            // (!) Logical and (~) Bitwise NOT
            //        this->_unaryOperators["!"] = new Operator("!", std::logical_not<scalar>, p, 1);
            // ~ negates a number
            this.operators.put("~", new Operator("~",
                    Op.class.getMethod("negate", double.class), p, 1));
            --p; //Power
            this.operators.put("^", new Operator("^",
                    Math.class.getMethod("pow", double.class, double.class), p, 1));
            --p; //Multiplication, Division, and Modulo
            this.operators.put("*", new Operator("*",
                    Op.class.getMethod("multiply", double.class, double.class), p));
            this.operators.put("/", new Operator("/",
                    Op.class.getMethod("divide", double.class, double.class), p));
            this.operators.put("%", new Operator("%",
                    Op.class.getMethod("modulo", double.class, double.class), p));
            --p; //Addition, Subtraction
            this.operators.put("+", new Operator("+",
                    Op.class.getMethod("add", double.class, double.class), p));
            this.operators.put("-", new Operator("-",
                    Op.class.getMethod("subtract", double.class, double.class), p));
//        //        --p; //Bitwise AND
//        //        this->_binaryOperators["&"] = new Operator("&", std::bit_and, p);
//        //        --p; //Bitwise OR
//        //        this->_binaryOperators["|"] = new Operator("|", std::bit_or, p);
            --p; //Logical AND
            this.operators.put(Rule.FL_AND, new Operator(Rule.FL_AND,
                    Op.class.getMethod("logicalAnd", double.class, double.class), p));
            --p; //Logical OR
            this.operators.put(Rule.FL_OR, new Operator(Rule.FL_OR,
                    Op.class.getMethod("logicalOr", double.class, double.class), p));
        } catch (Exception ex) {
            throw new RuntimeException("[function error] operator could not be loaded", ex);
        }
    }

    public void loadBuiltInFunctions() {
        try {
            this.functions.put("acos", new BuiltInFunction(Math.class.getMethod("acos", double.class)));
            this.functions.put("asin", new BuiltInFunction(Math.class.getMethod("asin", double.class)));
            this.functions.put("atan", new BuiltInFunction(Math.class.getMethod("atan", double.class)));

            this.functions.put("ceil", new BuiltInFunction(Math.class.getMethod("ceil", double.class)));
            this.functions.put("cos", new BuiltInFunction(Math.class.getMethod("cos", double.class)));
            this.functions.put("cosh", new BuiltInFunction(Math.class.getMethod("cosh", double.class)));
            this.functions.put("exp", new BuiltInFunction(Math.class.getMethod("exp", double.class)));
            this.functions.put("fabs", new BuiltInFunction(Math.class.getMethod("abs", double.class)));
            this.functions.put("floor", new BuiltInFunction(Math.class.getMethod("floor", double.class)));
            this.functions.put("log", new BuiltInFunction(Math.class.getMethod("log", double.class)));
            this.functions.put("log10", new BuiltInFunction(Math.class.getMethod("log10", double.class)));

            this.functions.put("sin", new BuiltInFunction(Math.class.getMethod("sin", double.class)));
            this.functions.put("sinh", new BuiltInFunction(Math.class.getMethod("sinh", double.class)));
            this.functions.put("sqrt", new BuiltInFunction(Math.class.getMethod("sqrt", double.class)));
            this.functions.put("tan", new BuiltInFunction(Math.class.getMethod("tan", double.class)));
            this.functions.put("tanh", new BuiltInFunction(Math.class.getMethod("tanh", double.class)));

            this.functions.put("log1p", new BuiltInFunction(Math.class.getMethod("log1p", double.class)));
            //not found in Java
//            this.functions.put("acosh", new BuiltInFunction("acosh",  & (acosh)));
//            this.functions.put("asinh", new BuiltInFunction("asinh",  & (asinh)));
//            this.functions.put("atanh", new BuiltInFunction("atanh",  & (atanh)));

            this.functions.put("pow", new BuiltInFunction(Math.class.getMethod("pow", double.class, double.class)));
            this.functions.put("atan2", new BuiltInFunction(Math.class.getMethod("atan2", double.class, double.class)));
            this.functions.put("fmod", new BuiltInFunction(Op.class.getMethod("modulo", double.class, double.class)));
        } catch (Exception ex) {
            throw new RuntimeException("[function error] built-in functions could not be loaded", ex);
        }
    }

    public boolean isOperator(String token) {
        return this.operators.containsKey(token);
    }

    public boolean isBuiltInFunction(String token) {
        return this.functions.containsKey(token);
    }

    public boolean isOperand(String token) {
        //An operand is not a parenthesis...
        if ("(".equals(token) || ")".equals(token) || ",".equals(token)) {
            return false;
        }
        //nor an operator...
        if (isOperator(token)) {
            return false;
        }
        //nor a function...
        if (isBuiltInFunction(token)) {
            return false;
        }
        //...it is everything else :)
        return true;
    }

    public String toPostfix(String formula) {
        return toPostfix(formula, false);
    }

    public String toPostfix(final String formula, boolean loadFunctions) {
        if (loadFunctions) {
            loadBuiltInFunctions();
        }
        //Space the operator to tokenize easier
        Set<String> toSpace = new HashSet<>(this.operators.keySet());
        toSpace.remove(Rule.FL_AND);
        toSpace.remove(Rule.FL_OR);
        toSpace.add("(");
        toSpace.add(")");
        toSpace.add(",");
        String spacedFormula = formula;
        for (String operator : toSpace) {
            spacedFormula = spacedFormula.replace(operator, " " + operator + " ");
        }

        //Tokenizer
        Deque<String> queue = new ArrayDeque<>();
        Deque<String> stack = new ArrayDeque<>();

        StringTokenizer tokenizer = new StringTokenizer(spacedFormula);
        String token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            if (isOperand(token)) {
                queue.offer(token);

            } else if (isBuiltInFunction(token)) {
                stack.push(token);

            } else if (",".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    queue.offer(stack.pop());
                }
                if (stack.isEmpty() || !"(".equals(stack.peek())) {
                    throw new RuntimeException(String.format(
                            "[parsing error] mismatching parentheses in: %s", formula));
                }

            } else if (isOperator(token)) {
                Operator op1 = getOperators().get(token);
                while (true) {
                    Operator op2;
                    if (!stack.isEmpty() && isOperator(stack.peek())) {
                        op2 = getOperators().get(stack.peek());
                    } else {
                        break;
                    }

                    if ((op1.associativity < 0 && op1.precedence <= op2.precedence)
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
                stack.pop();

                if (!stack.isEmpty() && isBuiltInFunction(stack.peek())) {
                    queue.offer(stack.pop());
                }

            } else {
                throw new RuntimeException(String.format(
                        "[parsing error] unexpected error with token <%s>", token));
            }
        }

        while (!stack.isEmpty()) {
            String pop = stack.pop();
            if ("(".equals(pop) || ")".equals(pop)) {
                throw new RuntimeException(String.format(
                        "[parsing error] mismatching parentheses in: %s", formula));
            }
            queue.offer(pop);
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

        Deque<Node> stack = new ArrayDeque<>();

        StringTokenizer tokenizer = new StringTokenizer(postfix);
        String token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (isOperator(token)) {
                Operator op = getOperators().get(token);
                if (op.getArity() > stack.size()) {
                    throw new RuntimeException(String.format(
                            "[function error] operator <%s> has arity <%d>, "
                            + "but <%d> elements are available",
                            op.name, op.getArity(), stack.size()));
                }

                Node node = new Node(op);
                node.left = stack.pop();
                if (op.getArity() == 2) {
                    node.right = stack.pop();
                }
                stack.push(node);

            } else if (isBuiltInFunction(token)) {
                BuiltInFunction function = getFunctions().get(token);
                if (function.getArity() > stack.size()) {
                    throw new RuntimeException(String.format(
                            "[function error] operator <%s> has arity <%d>, "
                            + "but <%d> elements are available",
                            function.name, function.getArity(), stack.size()));
                }

                Node node = new Node(function);
                node.left = stack.pop();
                if (function.getArity() == 2) {
                    node.right = stack.pop();
                }
                stack.push(node);

            } else if (isOperand(token)) {
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
                    "[function error] parsing function <%s> due to: <%s>",
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

    public Map<String, Operator> getOperators() {
        return operators;
    }

    public Map<String, BuiltInFunction> getFunctions() {
        return functions;
    }

    public static void test(String[] args) throws Exception {
        Logger log = FuzzyLite.logger();
        Function f = new Function();
        String text = "3+4*2/(1-5)^2^3";
//        String formula = "3+4*2/2";
        log.info(f.toPostfix(text));
        log.info(f.parse(text).toInfix());
        log.info(Op.str(f.parse(text).evaluate(f.getVariables())));
        f.load(text);
        log.info(">>>" + Op.str(f.evaluate()));

        f.getVariables().put("y", 1.0);
        text = "sin(y*x)^2/x";
        f.loadBuiltInFunctions();
        log.info("pre: " + f.parse(text).toPrefix());
        log.info("in: " + f.parse(text).toInfix());
        log.info("pos: " + f.parse(text).toPostfix());
        f.load(text);
        log.info("Result: " + Op.str(f.membership(1)));

        text = "(Temperature is High and Oxigen is Low) or "
                + "(Temperature is Low and (Oxigen is Low or Oxigen is High))";
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
