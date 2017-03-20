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
import java.util.List;
import java.util.logging.Level;

/**
 The First class is a RuleBlock Activation method that activates the first
 @f$n@f$ rules whose activation degrees are greater than or equal to the given
 threshold. The rules are iterated in the order they were added to the rule
 block.

 @author Juan Rada-Vilela, Ph.D.
 @see Last
 @see Rule
 @see RuleBlock
 @see ActivationFactory
 @since 6.0
 */
public class First extends Activation {

    private int numberOfRules;
    private double threshold;

    public First() {
        this(1);
    }

    public First(int numberOfRules) {
        this(numberOfRules, 0.0);
    }

    public First(int numberOfRules, double threshold) {
        this.numberOfRules = numberOfRules;
        this.threshold = threshold;
    }

    /**
     Returns the number of rules and the threshold of the activation method

     @return "numberOfRules threshold"
     */
    @Override
    public String parameters() {
        return Op.str(getNumberOfRules()) + " " + Op.str(getThreshold());
    }

    /**
     Configures the activation method with the given number of rules and
     threshold

     @param parameters as "numberOfRules threshold"
     */
    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ", true);
        final int required = 2;
        if (values.size() < required) {
            throw new RuntimeException(MessageFormat.format(
                    "[configuration error] activation {0} requires {1} parameters",
                    this.getClass().getSimpleName(), required));
        }

        setNumberOfRules(Integer.parseInt(values.get(0)));
        setThreshold(Op.toDouble(values.get(1)));
    }

    /**
     Activates the first @f$n@f$ rules whose activation degrees are greater than
     or equal to the given threshold. The rules are iterated in the order the
     rules were added to the rule block.

     @param ruleBlock is the rule block to activate
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

        int activated = 0;
        for (Rule rule : ruleBlock.getRules()) {
            rule.deactivate();

            if (rule.isLoaded()) {
                double activationDegree = rule.activateWith(conjunction, disjunction);
                if (activated < numberOfRules
                        && Op.isGt(activationDegree, 0.0)
                        && Op.isGE(activationDegree, threshold)) {
                    rule.trigger(implication);
                    ++activated;
                }
            }
        }
    }

    /**
     Gets the number of rules for the activation degree

     @return the number of rules for the activation degree
     */
    public int getNumberOfRules() {
        return numberOfRules;
    }

    /**
     Sets the number of rules for the activation degree

     @param numberOfRules is the number of rules for the activation degree
     */
    public void setNumberOfRules(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    /**
     Gets the threshold for the activation degree

     @return the threshold for the activation degree
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     Sets the threshold for the activation degree

     @param threshold is the threshold for the activation degree
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public First clone() throws CloneNotSupportedException {
        return (First) super.clone();
    }
}
