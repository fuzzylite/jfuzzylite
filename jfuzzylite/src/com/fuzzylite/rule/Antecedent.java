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
package com.fuzzylite.rule;

import com.fuzzylite.Engine;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.factory.HedgeFactory;
import com.fuzzylite.hedge.Any;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.term.Function;
import com.fuzzylite.variable.InputVariable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

/**
 *
 * @author jcrada
 */
public class Antecedent {

    protected Expression root;

    public Expression getRoot() {
        return this.root;
    }

    public double activationDegree(TNorm conjunction, SNorm disjunction) {
        return this.activationDegree(conjunction, disjunction, root);
    }

    public double activationDegree(TNorm conjunction, SNorm disjunction, Expression node) {
        if (node instanceof Proposition) {
            Proposition proposition = (Proposition) node;
            if (!proposition.variable.isEnabled()) {
                return 0.0;
            }
            if (!proposition.getHedges().isEmpty()) {
                int lastIndex = proposition.getHedges().size() - 1;
                Hedge any = proposition.getHedges().get(lastIndex);
                if (any instanceof Any) {
                    double result = any.hedge(Double.NaN);
                    while (--lastIndex >= 0) {
                        result = proposition.getHedges().get(lastIndex).hedge(result);
                    }
                    return result;
                }

            }

            InputVariable inputVariable = (InputVariable) proposition.getVariable();
            double result = proposition.getTerm().membership(inputVariable.getInputValue());
            for (int i =  proposition.getHedges().size() - 1 ;i >= 0; --i) {
                result = proposition.getHedges().get(i).hedge(result);
            }
            return result;
        } else if (node instanceof Operator) {
            Operator operator = (Operator) node;
            if (operator.getLeft() == null || operator.getRight() == null) {
                throw new RuntimeException("[syntax error] left and right operators cannot be null");
            }
            if (Rule.FL_AND.equals(operator.getName())) {
                return conjunction.compute(
                        activationDegree(conjunction, disjunction, operator.getLeft()),
                        activationDegree(conjunction, disjunction, operator.getRight()));
            }
            if (Rule.FL_OR.equals(operator.getName())) {
                return disjunction.compute(
                        activationDegree(conjunction, disjunction, operator.getLeft()),
                        activationDegree(conjunction, disjunction, operator.getRight()));
            }
            throw new RuntimeException(String.format(
                    "[syntax error] operator <%s> not recognized",
                    operator.getName()));
        } else {
            throw new RuntimeException("[expression error] unknown instance of Expression");
        }
    }

    public void load(String antecedent, Engine engine) {
        Function function = new Function();
        String postfix = function.toPostfix(antecedent);
        /*
         Builds an proposition tree from the antecedent of a fuzzy rule.
         The rules are:
         1) After a variable comes 'is',
         2) After 'is' comes a hedge or a term
         3) After a hedge comes a hedge or a term
         4) After a term comes a variable or an operator
         */

        final byte S_VARIABLE = 1, S_IS = 2, S_HEDGE = 4, S_TERM = 8, S_AND_OR = 16;
        byte state = S_VARIABLE;
        Deque<Expression> expressionStack = new ArrayDeque<>();
        Proposition proposition = null;

        StringTokenizer tokenizer = new StringTokenizer(postfix);
        String token;

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if ((state & S_VARIABLE) > 0) {
                if (engine.hasInputVariable(token)) {
                    proposition = new Proposition();
                    proposition.variable = engine.getInputVariable(token);
                    expressionStack.push(proposition);

                    state = S_IS;
                    continue;
                }
            }

            if ((state & S_IS) > 0) {
                if (Rule.FL_IS.equals(token)) {
                    state = S_HEDGE | S_TERM;
                    continue;
                }
            }

            if ((state & S_HEDGE) > 0) {
                Hedge hedge = null;
                if (engine.hasHedge(token)) {
                    hedge = engine.getHedge(token);
                } else {
                    HedgeFactory hedgeFactory = FactoryManager.instance().hedge();
                    if (hedgeFactory.isRegistered(token)) {
                        hedge = hedgeFactory.createInstance(token);
                        engine.addHedge(hedge);
                    }
                }
                if (hedge != null) {
                    proposition.hedges.add(hedge);
                    if (hedge instanceof Any) {
                        state = S_VARIABLE | S_AND_OR;
                    } else {
                        state = S_HEDGE | S_TERM;
                    }
                    continue;
                }
            }

            if ((state & S_TERM) > 0) {
                if (proposition.variable.hasTerm(token)) {
                    proposition.term = proposition.variable.getTerm(token);
                    state = S_VARIABLE | S_AND_OR;
                    continue;
                }
            }

            if ((state & S_AND_OR) > 0) {
                if (Rule.FL_AND.equals(token) || Rule.FL_OR.equals(token)) {
                    if (expressionStack.size() != 2) {
                        throw new RuntimeException(String.format(
                                "[syntax error] logical operator <%s> expects two operands, but found <%d>",
                                token, expressionStack.size()));
                    }
                    Operator operator = new Operator();
                    operator.name = token;
                    operator.right = expressionStack.pop();
                    operator.left = expressionStack.pop();
                    expressionStack.push(operator);

                    state = S_VARIABLE | S_AND_OR;
                    continue;
                }
            }

            //If reached this point, there was an error
            if ((state & S_VARIABLE) > 0 || (state & S_AND_OR) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected input variable or operator, but found <%s>",
                        token));
            }
            if ((state & S_IS) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected keyword <%s>, but found <%s>",
                        Rule.FL_IS, token));
            }
            if ((state & S_HEDGE) > 0 || (state & S_TERM) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected hedge or term, but found <%s>",
                        token));
            }
            throw new RuntimeException(String.format(
                    "[syntax error] unexpected token <%s>",
                    token));
        }
        if (expressionStack.size() != 1) {
            throw new RuntimeException(String.format(
                    "[syntax error] stack expected to contain the root, but contains %d nodes",
                    expressionStack.size()));
        }
        this.root = expressionStack.pop();
    }

    @Override
    public String toString() {
        return this.toInfix(this.root);
    }

    public String toPrefix() {
        return this.toPrefix(this.root);
    }

    public String toInfix() {
        return this.toInfix(this.root);
    }

    public String toPostfix() {
        return this.toPostfix(this.root);
    }

    public String toPrefix(Expression node) {
        if (node instanceof Proposition) {
            return node.toString();
        }
        if (node instanceof Operator) {
            Operator operator = (Operator) node;
            return operator.toString() + " "
                    + this.toPrefix(operator.left) + " "
                    + this.toPrefix(operator.right) + " ";
        }
        throw new RuntimeException(String.format(
                "[expression error] unexpected class <%s>",
                node.getClass().getSimpleName()));
    }

    public String toInfix(Expression node) {
        if (node instanceof Proposition) {
            return node.toString();
        }
        if (node instanceof Operator) {
            Operator operator = (Operator) node;
            return this.toInfix(operator.left) + " "
                    + operator.toString() + " "
                    + this.toInfix(operator.right) + " ";
        }
        throw new RuntimeException(String.format(
                "[expression error] unexpected class <%s>",
                node.getClass().getSimpleName()));
    }

    public String toPostfix(Expression node) {
        if (node instanceof Proposition) {
            return node.toString();
        }
        if (node instanceof Operator) {
            Operator operator = (Operator) node;
            return this.toPostfix(operator.left) + " "
                    + this.toPostfix(operator.right) + " "
                    + operator.toString() + " ";
        }
        throw new RuntimeException(String.format(
                "[expression error] unexpected class <%s>",
                node.getClass().getSimpleName()));
    }
}
