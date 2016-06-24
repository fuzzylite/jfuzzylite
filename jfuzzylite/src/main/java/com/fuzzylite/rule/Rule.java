/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.rule;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import java.util.StringTokenizer;
import java.util.logging.Level;

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
    private double activationDegree;
    private boolean activated;

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
        this.activationDegree = 0.0;
        this.activated = false;
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

    public double getActivationDegree() {
        return activationDegree;
    }

    public void setActivationDegree(double activationDegree) {
        this.activationDegree = activationDegree;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public double computeActivationDegree(TNorm conjunction, SNorm disjunction) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        return weight * antecedent.activationDegree(conjunction, disjunction);
    }

    public void activate(double activationDegree, TNorm implication) {
        if (FuzzyLite.isDebugging()) {
            FuzzyLite.logger().log(Level.FINE, "[activating] {0}", toString());
        }
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        if (Op.isGt(activationDegree, 0.0)) {
            if (FuzzyLite.isDebugging()) {
                FuzzyLite.logger().log(Level.FINE, "[degree={0}] {1}",
                        new String[]{Op.str(activationDegree), toString()});
            }
            this.activationDegree = activationDegree;
            consequent.modify(activationDegree, implication);
        }
        this.activated = true;
    }

    public void deactivate() {
        this.activated = false;
        this.activationDegree = 0.0;
        if (FuzzyLite.isDebugging()) {
            FuzzyLite.logger().log(Level.FINE, "[deactivated] {0}", toString());
        }
    }

    public boolean isLoaded() {
        return antecedent != null && consequent != null
                && antecedent.isLoaded() && consequent.isLoaded();
    }

    public void unload() {
        deactivate();
        if (getAntecedent() != null) {
            getAntecedent().unload();
        }
        if (getConsequent() != null) {
            getConsequent().unload();
        }
    }

    public void load(Engine engine) {
        load(getText(), engine);
    }

    public void load(String rule, Engine engine) {
        setText(rule);
        setActivated(false);
        setActivationDegree(0.0);
        StringTokenizer tokenizer = new StringTokenizer(rule);
        String token;
        StringBuilder strAntecedent = new StringBuilder();
        StringBuilder strConsequent = new StringBuilder();
        double ruleWeight = 1.0;

        final byte S_NONE = 0, S_IF = 1, S_THEN = 2, S_WITH = 3, S_END = 4;
        byte state = S_NONE;
        try {
            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                int commentIndex = token.indexOf('#');
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
                            strAntecedent.append(token).append(" ");
                        }
                        break;
                    case S_THEN:
                        if (Rule.FL_WITH.equals(token)) {
                            state = S_WITH;
                        } else {
                            strConsequent.append(token).append(" ");
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
            getAntecedent().load(strAntecedent.toString(), engine);
            getConsequent().load(strConsequent.toString(), engine);
            setWeight(ruleWeight);
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
        return result;
    }

}
