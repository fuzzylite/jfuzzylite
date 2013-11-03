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
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 *
 * @author jcrada
 */
public class Function extends Term {

    public static abstract class Element {

        public String name;
        public Method function;

        public int getArity() {
            return function.getParameterTypes().length;
        }
    }

    public static class Operator extends Element {

        public short precedence, associativity;
    }

    public static class BuiltInFunction extends Element {

        public short associativity;
    }

    public static class Node {

        public Operator operator;
        public BuiltInFunction function;
        public String variable;
        public double value;
        public Node left, right;

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
                            result = (double) element.function.invoke(null);
                            break;
                        case 1:
                            result = (double) element.function.invoke(null,
                                    left.evaluate(localVariables));
                            break;
                        case 2:
                            result = (double) element.function.invoke(null,
                                    left.evaluate(localVariables),
                                    right.evaluate(localVariables));
                            break;
                        default:
                            throw new RuntimeException(String.format(
                                    "[function error] <%i>-ary element <%s> is not supported, "
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
            FuzzyLite.logger().info(String.format("%s=%s", toPostfix(), str(result)));
            return result;
        }

        public static Node parse(String infix) throws Exception {
            //TODO: implement
            return null;
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
    protected String text;
    protected Engine engine;
    protected Node root;
    protected Map<String, Double> variables;
    protected Map<String, Operator> operators;
    protected Map<String, BuiltInFunction> functions;

    public Function(String name) {

    }

    public Function(String name, String text, Engine engine, boolean loadBuiltInFunctions) {
        this.name = name;
        this.text = text;
        this.engine = engine;
        //TODO: What to do with warning? Overridable method.
        loadOperators();
        if (loadBuiltInFunctions) {
            loadBuiltInFunctions();
        }
    }

    public void load() throws Exception {
        load(this.text, this.engine);
    }

    public void load(String text, Engine engine) throws Exception {
        this.root = parse(text);
        this.text = text;
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

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Function create(String name, String text, Engine engine) {
        //TODO: implement
        Function result = new Function(name);
        return result;
    }

    public void loadOperators() {
        //TODO: implement
    }

    public void loadBuiltInFunctions() {
        //TODO: implement
    }

    public String toPostfix(String text) {
        //TODO: implement
        return "";
    }

    public Node parse(String text) throws Exception {
        //TODO: implement
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Op.class.getMethod("add", double.class, double.class).getParameterTypes().length);
        for (Method m : Math.class.getMethods()) {

            if (Modifier.isPublic(m.getModifiers())
                    && Modifier.isStatic(m.getModifiers())) {
            }
        }
    }
}
