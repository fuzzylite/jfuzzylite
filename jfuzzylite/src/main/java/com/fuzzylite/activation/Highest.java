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
package com.fuzzylite.activation;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;

/**
 The Highest class is a RuleBlock Activation method that activates a given
 number of rules with highest activation degrees in descending order.

 @author Juan Rada-Vilela, Ph.D.
 @see Lowest
 @see Rule
 @see RuleBlock
 @see ActivationFactory
 @since 6.0
 */
public class Highest extends Activation {

    private int numberOfRules;

    public Highest() {
        this(1);
    }

    public Highest(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    /**
     Returns the number of rules to activate.

     @return number of rules to activate
     */
    @Override
    public String parameters() {
        return Op.str(getNumberOfRules());
    }

    /**
     Configures the activation method with the number of rules to activate.

     @param parameters contains the number of rules to activate
     */
    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ", true);
        final int required = 1;
        if (values.size() < required) {
            throw new RuntimeException(MessageFormat.format(
                    "[configuration error] activation {0} requires {1} parameters",
                    this.getClass().getSimpleName(), required));
        }

        setNumberOfRules(Integer.parseInt(values.get(0)));
    }

    protected static class Descending implements Comparator<Rule> {

        @Override
        public int compare(Rule a, Rule b) {
            double result = Math.signum(b.getActivationDegree() - a.getActivationDegree());
            return Double.isNaN(result) ? -1 : (int) result;
        }
    }

    /**
     Activates the given number of rules with the highest activation degrees

     @param ruleBlock is the rule block to activate.
     */
    @Override
    public void activate(RuleBlock ruleBlock) {
        if (FuzzyLite.isDebugging()) {
            FuzzyLite.logger().log(Level.FINE, "Activation: {0} {1}",
                    new String[]{getClass().getName(), parameters()});
        }
        TNorm conjunction = ruleBlock.getConjunction();
        SNorm disjunction = ruleBlock.getDisjunction();
        TNorm implication = ruleBlock.getImplication();

        PriorityQueue<Rule> rulesToActivate = new PriorityQueue<Rule>(
                numberOfRules, new Descending());

        for (Rule rule : ruleBlock.getRules()) {
            rule.deactivate();

            if (rule.isLoaded()) {
                double activationDegree = rule.activateWith(conjunction, disjunction);
                if (Op.isGt(activationDegree, 0.0)) {
                    rulesToActivate.offer(rule);
                }
            }
        }

        int activated = 0;
        while (!rulesToActivate.isEmpty() && activated++ < numberOfRules) {
            rulesToActivate.poll().trigger(implication);
        }
    }

    /**
     Returns the number of rules to activate

     @return the number of rules to activate
     */
    public int getNumberOfRules() {
        return numberOfRules;
    }

    /**
     Sets the number of rules to activate

     @param numberOfRules is the number of rules to activate
     */
    public void setNumberOfRules(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    @Override
    public Highest clone() throws CloneNotSupportedException {
        return (Highest) super.clone();
    }
}
