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

import java.util.logging.Level;

/**
 The General class is a RuleBlock Activation method that activates every rule
 following the order in which the rules were added to the rule block.

 @author Juan Rada-Vilela, Ph.D.
 @see Rule
 @see RuleBlock
 @see ActivationFactory
 @since 6.0
 */
public class General extends Activation {

    public General() {
    }

    /**
     No parameters are required to configure the activation method.

     @return an empty string
     */
    @Override
    public String parameters() {
        return "";
    }

    /**
     No parameters are required to configure the activation method.

     @param parameters is an empty string
     */
    @Override
    public void configure(String parameters) {
        //do nothing...
    }

    /**
     Activates every rule in the given rule block following the order in which
     the rules were added.

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

        for (Rule rule : ruleBlock.getRules()) {
            rule.deactivate();
            if (rule.isLoaded()) {
                rule.activateWith(conjunction, disjunction);
                rule.fire(implication);
            }
        }
    }

    @Override
    public General clone() throws CloneNotSupportedException {
        return (General) super.clone();
    }

}
