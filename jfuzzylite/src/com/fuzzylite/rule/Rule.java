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
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import java.util.StringTokenizer;

/**
 *
 * @author jcrada
 */
public class Rule {

    public static final String FL_IF = "if";
    public static final String FL_IS = "is";
    public static final String FL_EQUALS = "=";
    public static final String FL_THEN = "then";
    public static final String FL_AND = "and";
    public static final String FL_OR = "or";
    public static final String FL_WITH = "with";
    protected Antecedent antecedent;
    protected Consequent consequent;
    protected double weight = 1.0;
    protected String text;

    public Rule() {
    }

    public double activationDegree(TNorm conjunction, SNorm disjunction) {
        return weight * this.antecedent.activationDegree(conjunction, disjunction);
    }

    public void activate(double activationDegree, TNorm activation) {
        this.consequent.modify(activationDegree, activation);
    }

    public static Rule parse(String rule, Engine engine) {
        Rule result = new Rule();
        result.setText(rule);
        StringTokenizer tokenizer = new StringTokenizer(rule);
        String token;
        String strAntecedent = "";
        String strConsequent = "";

        final byte S_NONE = 0, S_IF = 1, S_THEN = 2, S_WITH = 3, S_END = 4;
        byte state = S_NONE;

        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            switch (state) {
                case S_NONE:
                    if (Rule.FL_IF.equals(token)) {
                        state = S_IF;
                    } else {
                        throw new RuntimeException(String.format(
                                "[syntax error] expected keyword <%s>, but found <%d> "
                                + "in rule: %s", Rule.FL_IF, token, rule));
                    }
                    break;

                case S_IF:
                    if (Rule.FL_THEN.equals(token)) {
                        state = S_THEN;
                    } else {
                        strAntecedent += token + " ";
                    }
                    break;
                case S_THEN:
                    if (Rule.FL_WITH.equals(token)) {
                        state = S_WITH;
                    } else {
                        strConsequent += token + " ";
                    }
                    break;
                case S_WITH:
                    try {
                        result.setWeight(Op.toDouble(token));
                        state = S_END;
                    } catch (NumberFormatException ex) {
                        throw ex;
                    }
                    break;

                case S_END:
                    throw new RuntimeException(String.format(
                            "[syntax error] unexpected token <%s> at the end of rule",
                            token));
            }
        }

        if (state == S_NONE) {
            throw new RuntimeException(String.format(
                    "[syntax error] keyword <%s> not found in rule: %s",
                    Rule.FL_IF, rule));
        } else if (state == S_IF) {
            throw new RuntimeException(String.format(
                    "[syntax error] keyword <%s> not found in rule: %s",
                    Rule.FL_THEN, rule));
        } else if (state == S_WITH) {
            throw new RuntimeException(String.format(
                    "[syntax error] expected a numeric value as the weight of the rule: %s",
                    rule));
        }
        result.antecedent = new Antecedent();
        result.antecedent.load(strAntecedent, engine);

        result.consequent = new Consequent();
        result.consequent.load(strConsequent, engine);

        return result;
    }

    @Override
    public String toString() {
        String result = String.format("%s %s %s %s",
                Rule.FL_IF, getAntecedent().toString(),
                Rule.FL_THEN, getConsequent().toString());
        if (!Op.isEq(this.weight, 1.0)) {
            result += String.format(" %s %s",
                    Rule.FL_WITH, str(this.weight));
        }
        return result;
    }

    public Antecedent getAntecedent() {
        return antecedent;
    }

    public void setAntecedent(Antecedent antecedent) {
        this.antecedent = antecedent;
    }

    public Consequent getConsequent() {
        return consequent;
    }

    public void setConsequent(Consequent consequent) {
        this.consequent = consequent;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }
}
