/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

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
 the control of the engine can be determined by its weight @f$w \in [0.0,
 1.0]@f$, which is equal to 1.0 if omitted. The structure of a rule is the
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
 @since 4.0 */
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
     Indicates whether the rule has been activated. The activation of a rule is
     automatically managed within Rule::activate(). The utility of this property
     can be found in the case of activation methods like First or Last, which
     compute the activation degree of the rules without necessarily activating
     the rules.

     @return whether the rule has been activated
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     Sets whether the rule has been activated. The activation of a rule is
     automatically managed within Rule::activate(). The utility of this property
     can be found in the case of activation methods like First or Last, which
     compute the activation degree of the rules without necessarily activating
     the rules.

     @param activated determines whether the rule has been activated
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     Computes the activation degree for this rule

     @param conjunction is the conjunction operator
     @param disjunction is the disjunction operator
     @return the activation degree of this rule multiplied by its weight
     */
    public double computeActivationDegree(TNorm conjunction, SNorm disjunction) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format("[rule error] the following rule is not loaded: %s", text));
        }
        return weight * antecedent.activationDegree(conjunction, disjunction);
    }

    /**
     Activates the rule with the given activation degree and implication
     operator

     @param activationDegree is the activation degree of the rule
     @param implication is the implication operator from the RuleBlock
     */
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

    /**
     Deactivates the rule setting the activation degree to 0.0
     */
    public void deactivate() {
        this.activated = false;
        this.activationDegree = 0.0;
        if (FuzzyLite.isDebugging()) {
            FuzzyLite.logger().log(Level.FINE, "[deactivated] {0}", toString());
        }
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
