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
package com.fuzzylite.rule;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Rule implements Op.Cloneable {

    public static final String FL_IF = "if";
    public static final String FL_IS = "is";
    public static final String FL_THEN = "then";
    public static final String FL_AND = "and";
    public static final String FL_OR = "or";
    public static final String FL_WITH = "with";

    private String text;
    private double weight;
    private Antecedent antecedent;
    private Consequent consequent;
    private Map<String, Hedge> hedges;

    public Rule() {
        this("");
    }

    public Rule(String text) {
        this(text, 1.0);
    }

    public Rule(String text, double weight) {
        this.text = text;
        this.weight = weight;
        this.antecedent = new Antecedent();
        this.consequent = new Consequent();
        this.hedges = new HashMap<String, Hedge>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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

    public Map<String, Hedge> getHedges() {
        return this.hedges;
    }

    public void setHedges(Map<String, Hedge> hedges) {
        this.hedges = hedges;
    }

    public double activationDegree(TNorm conjunction, SNorm disjunction) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        return weight * this.antecedent.activationDegree(conjunction, disjunction);
    }

    public void activate(double activationDegree, TNorm activation) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        this.consequent.modify(activationDegree, activation);
    }

    public boolean isLoaded() {
        return antecedent.isLoaded() && consequent.isLoaded();
    }

    public void unload() {
        antecedent.unload();
        consequent.unload();
        hedges.clear();
    }

    public void load(Engine engine) {
        load(text, engine);
    }

    public void load(String rule, Engine engine) {
        this.text = rule;
        StringTokenizer tokenizer = new StringTokenizer(rule);
        String token;
        String strAntecedent = "";
        String strConsequent = "";
        double ruleWeight = 1.0;

        final byte S_NONE = 0, S_IF = 1, S_THEN = 2, S_WITH = 3, S_END = 4;
        byte state = S_NONE;
        try {
            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                int commentIndex = token.indexOf("#");
                if (commentIndex >= 0) {
                    token = token.substring(0, commentIndex);
                }
                switch (state) {
                    case S_NONE:
                        if (Rule.FL_IF.equals(token)) {
                            state = S_IF;
                        } else {
                            throw new RuntimeException(String.format(
                                    "[syntax error] expected keyword <%s>, but found <%s> in rule: %s", Rule.FL_IF, token, rule));
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
                            ruleWeight = Op.toDouble(token);
                            state = S_END;
                        } catch (NumberFormatException ex) {
                            throw ex;
                        }
                        break;

                    case S_END:
                        throw new RuntimeException(String.format(
                                "[syntax error] unexpected token <%s> at the end of rule", token));
                }
            }

            if (state == S_NONE) {
                throw new RuntimeException(String.format("[syntax error] " + (rule.isEmpty() ? "empty" : "ignored") + "rule: %s", rule));
            } else if (state == S_IF) {
                throw new RuntimeException(String.format(
                        "[syntax error] keyword <%s> not found in rule: %s",
                        Rule.FL_THEN, rule));
            } else if (state == S_WITH) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected a numeric value as the weight of the rule: %s",
                        rule));
            }
            antecedent.load(strAntecedent, this, engine);
            consequent.load(strConsequent, this, engine);
            this.weight = ruleWeight;
        } catch (RuntimeException ex) {
            unload();
            throw ex;
        }
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    public static Rule parse(String rule, Engine engine) {
        Rule result = new Rule();
        result.load(rule, engine);
        return result;
    }

    @Override
    public Rule clone() throws CloneNotSupportedException {
        Rule result = (Rule) super.clone();
        result.antecedent = new Antecedent();
        result.consequent = new Consequent();
        result.hedges = new HashMap<String, Hedge>(this.hedges);
        return result;

    }
}
