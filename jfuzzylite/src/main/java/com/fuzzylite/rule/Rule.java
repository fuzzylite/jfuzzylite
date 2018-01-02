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
package com.fuzzylite.rule;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;

import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 The Rule class is a conditional statement that contributes to the control of an
 Engine. Each rule consists of an Antecedent and a Consequent, each of which
 comprises propositions in the form `variable is term`. The propositions in the
 Antecedent can be connected by the conjunctive `and` or the disjunctive `or`,
 both of which are fuzzy logic operators (TNorm and SNorm, respectively).
 Differently, the propositions in the Consequent are independent from each other
 and are separated with a symbolic `and`. The Term in any proposition can be
 preceded by a Hedge that modifies its membership function to model cases such
 as Very, Somewhat, Seldom and Not. Additionally, the contribution of a rule to
 the control of the engine can be determined by its weight `w \in [0.0,
 1.0]`, which is equal to 1.0 if omitted. The structure of a rule is the
 following: `if (antecedent) then (consequent) [with weight]`. The structures of
 the antecedent and the consequent are:

 `if variable is [hedge]* term [(and|or) variable is [hedge]* term]*`

 `then variable is [hedge]* term [and variable is [hedge]* term]* [with w]?`

 where elements in brackets are optional, elements in parentheses are
 compulsory, `*`-marked elements may appear zero or more times, and `?`-marked
 elements may appear once or not at all.

 @author Juan Rada-Vilela, Ph.D.
 @see Antecedent
 @see Consequent
 @see Hedge
 @see RuleBlock
 @since 4.0
 */
public class Rule implements Op.Cloneable {

    /**
     String representation of the `if` keyword in rules
     */
    public static final String FL_IF = "if";
    /**
     String representation of the `is` keyword in rules
     */
    public static final String FL_IS = "is";
    /**
     String representation of the `then` keyword in rules
     */
    public static final String FL_THEN = "then";
    /**
     String representation of the `and` keyword in rules
     */
    public static final String FL_AND = "and";
    /**
     String representation of the `or` keyword in rules
     */
    public static final String FL_OR = "or";
    /**
     String representation of the `with` keyword in rules
     */
    public static final String FL_WITH = "with";

    private boolean enabled;
    private String text;
    private double weight;
    private double activationDegree;
    private boolean triggered;
    private Antecedent antecedent;
    private Consequent consequent;

    public Rule() {
        this("");
    }

    public Rule(String text) {
        this(text, 1.0);
    }

    /**
     *
     * @param text the rule text
     * @param weight Not considered at all. Use `with N` in rule text to specify weight.
     *
     * @deprecated Use Rule(String) instead. This constructor will be made private in a later release.
     */
    public Rule(String text, double weight) {
        this.enabled = true;
        this.text = text;
        this.weight = weight;
        this.activationDegree = 0.0;
        this.triggered = false;
        this.antecedent = new Antecedent();
        this.consequent = new Consequent();
    }

    /**
     Gets whether the rule is enabled. An enabled rule will be triggered, whereas a
     disabled rule will not.

     @return whether the rule is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     Sets whether the rule is enabled. An enabled rule will be triggered, whereas a
     disabled rule will not.

     @param enabled determines whether the rule is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     Gets the text of the rule

     @return the text of the rule
     */
    public String getText() {
        return text;
    }

    /**
     Sets the text of the rule

     @param text is the text of the rule
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     Gets the weight of the rule

     @return the weight of the rule
     */
    public double getWeight() {
        return weight;
    }

    /**
     Sets the weight of the rule

     @param weight is the weight of the rule
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     Gets the antecedent of the rule

     @return the antecedent of the rule
     */
    public Antecedent getAntecedent() {
        return antecedent;
    }

    /**
     Sets the antecedent of the rule

     @param antecedent is the antecedent of the rule
     */
    public void setAntecedent(Antecedent antecedent) {
        this.antecedent = antecedent;
    }

    /**
     Gets the consequent of the rule

     @return the consequent of the rule
     */
    public Consequent getConsequent() {
        return consequent;
    }

    /**
     Sets the consequent of the rule

     @param consequent the consequent of the rule
     */
    public void setConsequent(Consequent consequent) {
        this.consequent = consequent;
    }

    /**
     Gets the activation degree of the rule

     @return the activation degree of the rule
     */
    public double getActivationDegree() {
        return activationDegree;
    }

    /**
     Sets the activation degree of the rule

     @param activationDegree is the activation degree of the rule
     */
    public void setActivationDegree(double activationDegree) {
        this.activationDegree = activationDegree;
    }

    /**
     Deactivates the rule
     */
    public void deactivate() {
        this.activationDegree = 0.0;
        this.triggered = false;
    }

    /**
     Activates the rule by computing its activation degree using the given
     conjunction and disjunction operators

     @param conjunction is the conjunction operator
     @param disjunction is the disjunction operator
     @return the activation degree of the rule
     */
    public double activateWith(TNorm conjunction, SNorm disjunction) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        activationDegree = weight * antecedent.activationDegree(conjunction, disjunction);
        return activationDegree;
    }

    /**
     Triggers the rule's implication (if the rule is enabled) using the given
     implication operator and the underlying activation degree

     @param implication is the implication operator
     */
    public void trigger(TNorm implication) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        if (enabled && Op.isGt(activationDegree, 0.0)) {
            if (FuzzyLite.isDebugging()) {
                FuzzyLite.logger().log(Level.FINE, "[firing with {0}] {1}",
                        new String[]{Op.str(activationDegree), toString()});
            }
            consequent.modify(activationDegree, implication);
            triggered = true;
        }
    }

    /**
     Indicates whether the rule's implication was triggered

     @return whether the rule's implication was triggered
     */
    public boolean isTriggered() {
        return triggered;
    }

    /**
     Indicates whether the rule is loaded

     @return whether the rule is loaded
     */
    public boolean isLoaded() {
        return antecedent != null && consequent != null
                && antecedent.isLoaded() && consequent.isLoaded();
    }

    /**
     Unloads the rule
     */
    public void unload() {
        deactivate();
        if (getAntecedent() != null) {
            getAntecedent().unload();
        }
        if (getConsequent() != null) {
            getConsequent().unload();
        }
    }

    /**
     Loads the rule with the text from Rule::getText(), and uses the engine to
     identify and retrieve references to the input variables and output
     variables as required

     @param engine is the engine from which the rule is part of
     */
    public void load(Engine engine) {
        load(getText(), engine);
    }

    /**
     Loads the rule with the given text, and uses the engine to identify and
     retrieve references to the input variables and output variables as required

     @param rule is the rule in text
     @param engine is the engine from which the rule is part of
     */
    public void load(String rule, Engine engine) {
        deactivate();
        setEnabled(true);
        setText(rule);
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
                    default:
                        throw new RuntimeException(String.format(
                                "[syntax error] unexpected state <%s>", state));
                }
            }

            if (state == S_NONE) {
                throw new RuntimeException(String.format("[syntax error] %s rule: %s", (rule.isEmpty() ? "empty" : "ignored"), rule));
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

    /**
     Returns a string representation of the rule in the FuzzyLite Language

     @return a string representation of the rule in the FuzzyLite Language
     */
    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    /**
     Parses and creates a new rule based on the text passed

     @param rule is the rule in text
     @param engine is the engine from which the rule is part of
     @return a new rule parsed from the given text
     */
    public static Rule parse(String rule, Engine engine) {
        Rule result = new Rule();
        result.load(rule, engine);
        return result;
    }

    /**
     Creates a clone of the rule without being loaded

     @return a clone of the rule without being loaded
     @throws CloneNotSupportedException by definition in Cloneable
     */
    @Override
    public Rule clone() throws CloneNotSupportedException {
        Rule result = (Rule) super.clone();
        result.antecedent = new Antecedent();
        result.consequent = new Consequent();
        return result;
    }

}
