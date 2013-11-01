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
import com.fuzzylite.hedge.Any;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.variable.InputVariable;

/**
 *
 * @author jcrada
 */
public class Antecedent {

    protected Expression root;

    public void load(String antecedent, Engine engine) {
        //TODO: Implement
    }

    public double activationDegree(TNorm conjunction, SNorm disjunction) {
        return this.activationDegree(conjunction, disjunction, root);
    }

    public double activationDegree(TNorm conjunction, SNorm disjunction, Expression node) {
        if (node instanceof Proposition) {
            Proposition proposition = (Proposition) node;
            boolean isAny = false;
            for (Hedge hedge : proposition.getHedges()) {
                isAny |= (hedge instanceof Any);
                if (isAny) {
                    return 1.0;
                }
            }
            InputVariable inputVariable = (InputVariable) proposition.getVariable();
            double result = proposition.getTerm().membership(inputVariable.getInputValue());
            for (Hedge hedge : proposition.getHedges()) {
                result = hedge.hedge(result);
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

    @Override
    public String toString() {
        return this.toStringInfix(this.root);
    }

    public String toStringPrefix() {
        return this.toStringPrefix(this.root);
    }

    public String toStringInfix() {
        return this.toStringInfix(this.root);
    }

    public String toStringPostfix() {
        return this.toStringPostfix(this.root);
    }

    public String toStringPrefix(Expression node) {
        if (node instanceof Proposition) {
            return node.toString();
        }
        if (node instanceof Operator) {
            Operator operator = (Operator) node;
            return operator.toString() + " "
                    + this.toStringPrefix(operator.left) + " "
                    + this.toStringPrefix(operator.right) + " ";
        }
        throw new RuntimeException(String.format(
                "[expression error] unexpected class <%s>",
                node.getClass().getSimpleName()));
    }

    public String toStringInfix(Expression node) {
        if (node instanceof Proposition) {
            return node.toString();
        }
        if (node instanceof Operator) {
            Operator operator = (Operator) node;
            return this.toStringInfix(operator.left) + " "
                    + operator.toString() + " "
                    + this.toStringInfix(operator.right) + " ";
        }
        throw new RuntimeException(String.format(
                "[expression error] unexpected class <%s>",
                node.getClass().getSimpleName()));
    }

    public String toStringPostfix(Expression node) {
        if (node instanceof Proposition) {
            return node.toString();
        }
        if (node instanceof Operator) {
            Operator operator = (Operator) node;
            return this.toStringPostfix(operator.left) + " "
                    + this.toStringPostfix(operator.right) + " "
                    + operator.toString() + " ";
        }
        throw new RuntimeException(String.format(
                "[expression error] unexpected class <%s>",
                node.getClass().getSimpleName()));
    }
}
