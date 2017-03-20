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
import com.fuzzylite.Op;
import com.fuzzylite.activation.Activation;
import com.fuzzylite.activation.General;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import java.util.ArrayList;
import java.util.List;

/**
 The RuleBlock class contains a set of Rule%s and fuzzy logic operators required
 to control an Engine.

 @author Juan Rada-Vilela, Ph.D.
 @see Engine
 @see Rule
 @see Antecedent
 @see Consequent
 @since 4.0
 */
public class RuleBlock implements Op.Cloneable {

    private boolean enabled;
    private String name;
    private String description;
    private TNorm conjunction;
    private SNorm disjunction;
    private TNorm implication;
    private Activation activation;
    private List<Rule> rules;

    public RuleBlock() {
        this("");
    }

    public RuleBlock(String name) {
        this.enabled = true;
        this.name = name;
        this.description = "";
        this.rules = new ArrayList<Rule>();
    }

    /**
     Activates the rule block
     */
    public void activate() {
        if (activation == null) {
            activation = new General();
        }
        activation.activate(this);
    }

    /**
     Unloads all the rules in the rule block
     */
    public void unloadRules() {
        for (Rule rule : this.rules) {
            rule.unload();
        }
    }

    /**
     Loads all the rules into the rule block

     @param engine is the engine where this rule block is registered
     */
    public void loadRules(Engine engine) {
        List<String> exceptions = new ArrayList<String>();
        for (Rule rule : this.rules) {
            if (rule.isLoaded()) {
                rule.unload();
            }
            try {
                rule.load(engine);
            } catch (Exception ex) {
                exceptions.add(String.format("[%s]: %s", rule.getText(), ex.toString()));
            }
        }
        if (!exceptions.isEmpty()) {
            throw new RuntimeException("[ruleblock error] the following "
                    + "rules could not be loaded:\n" + Op.join(exceptions, "\n"));
        }
    }

    /**
     Unloads all the rules in the rule block and then loads each rule again

     @param engine is the engine where this rule block is registered
     */
    public void reloadRules(Engine engine) {
        unloadRules();
        loadRules(engine);
    }

    /**
     Returns a string representation of the rule block in the FuzzyLite Language

     @return a string representation of the rule block in the FuzzyLite Language
     */
    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    /**
     Gets the name of the rule block

     @return the name of the rule block
     */
    public String getName() {
        return name;
    }

    /**
     Sets the name of the rule block

     @param name is the name of the rule block
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Gets the description of the rule block

     @return the description of the rule block
     */
    public String getDescription() {
        return description;
    }

    /**
     Sets the description of the rule block

     @param description is the description of the rule block
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     Gets the conjunction operator

     @return the conjunction operator
     */
    public TNorm getConjunction() {
        return conjunction;
    }

    /**
     Sets the conjunction operator

     @param conjunction is the conjunction operator
     */
    public void setConjunction(TNorm conjunction) {
        this.conjunction = conjunction;
    }

    /**
     Gets the disjunction operator

     @return the disjunction operator
     */
    public SNorm getDisjunction() {
        return disjunction;
    }

    /**
     Sets the disjunction operator

     @param disjunction is the disjunction operator
     */
    public void setDisjunction(SNorm disjunction) {
        this.disjunction = disjunction;
    }

    /**
     Gets the implication operator

     @return the implication operator
     */
    public TNorm getImplication() {
        return implication;
    }

    /**
     Sets the implication operator

     @param implication is the implication operator
     */
    public void setImplication(TNorm implication) {
        this.implication = implication;
    }

    /**
     Gets the activation method

     @return the activation method
     */
    public Activation getActivation() {
        return activation;
    }

    /**
     Sets the activation method

     @param activation is the activation method
     */
    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    /**
     Indicates whether the rule block is enabled

     @return whether the rule block is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     Enables the rule block

     @param enabled whether the rule block is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     Creates a clone of the rule block without the rules being loaded

     @return a clone of the rule block without the rules being loaded
     */
    @Override
    public RuleBlock clone() throws CloneNotSupportedException {
        RuleBlock result = (RuleBlock) super.clone();
        if (this.conjunction != null) {
            result.conjunction = this.conjunction.clone();
        }
        if (this.disjunction != null) {
            result.disjunction = this.disjunction.clone();
        }
        if (this.implication != null) {
            result.implication = this.implication.clone();
        }
        result.rules = new ArrayList<Rule>(this.rules.size());
        for (Rule rule : this.rules) {
            result.addRule(rule.clone());
        }
        return result;
    }

    /**
     Gets the rule at the specified index

     @param index is the index at which the rule is retrieved
     @return the rule at the specified index
     */
    public Rule getRule(int index) {
        return this.rules.get(index);
    }

    /**
     Adds the given rule to the rule block

     @param rule is the rule to add
     */
    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    /**
     Removes the rule at the specified index

     @param index is the index at which the rule will be removed
     @return the rule at the specified index
     */
    public Rule removeRule(int index) {
        return this.rules.remove(index);
    }

    /**
     Returns the number of rules added to the rule block

     @return the number of rules added to the rule block
     */
    public int numberOfRules() {
        return this.rules.size();
    }

    /**
     Returns the list of rules added to the rule block

     @return the list of rules added to the rule block
     */
    public List<Rule> getRules() {
        return this.rules;
    }

    /**
     Sets the rules of the rule block

     @param rules is a vector of rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

}
