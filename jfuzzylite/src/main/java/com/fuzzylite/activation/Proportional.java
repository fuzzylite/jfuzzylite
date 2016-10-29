/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
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
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 The Proportional class is a RuleBlock Activation method that activates the
 rules utilizing activation degrees proportional to the activation degrees of
 the other rules, thus the sum of the activation degrees is equal to one.

 @author Juan Rada-Vilela, Ph.D.
 @see Rule
 @see RuleBlock
 @see ActivationFactory
 @since 6.0
 */
public class Proportional extends Activation {

    public Proportional() {
    }

    /**
     No parameters are required to configure the activation method

     @return an empty string
     */
    @Override
    public String parameters() {
        return "";
    }

    /**
     No parameters are required to configure the activation method

     @param parameters is an empty string
     */
    @Override
    public void configure(String parameters) {
        //do nothing...
    }

    /**
     Activates the rules utilizing activation degrees proportional to the
     activation degrees of the other rules in the rule block.

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

        double sumActivationDegrees = 0.0;
        List<Rule> rulesToActivate = new ArrayList<Rule>(ruleBlock.getRules().size());
        for (Rule rule : ruleBlock.getRules()) {
            rule.deactivate();
            if (rule.isLoaded()) {
                double activationDegree = rule.computeActivationDegree(conjunction, disjunction);
                rule.setActivationDegree(activationDegree);
                rulesToActivate.add(rule);
                sumActivationDegrees += activationDegree;
            }
        }
        for (Rule rule : rulesToActivate) {
            double activationDegree = rule.getActivationDegree() / sumActivationDegrees;
            rule.activate(activationDegree, implication);
        }
    }

    @Override
    public Proportional clone() throws CloneNotSupportedException {
        return (Proportional) super.clone();
    }

}
