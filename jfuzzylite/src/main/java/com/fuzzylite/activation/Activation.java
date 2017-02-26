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

import com.fuzzylite.Op;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;

/**
 The Activation class is the abstract class for RuleBlock activation methods. An
 activation method implements the criteria to activate the rules within a given
 rule block. An activation method needs to process every rule and determine
 whether the rule is to be activated or deactivated. The activation methods were
 first introduced in version 6.0, but in earlier versions the term `activation`
 referred to the TNorm that modulated the consequent of a rule, which is now
 referred to as the `implication` operator.

 @author Juan Rada-Vilela, Ph.D.
 @see Rule
 @see RuleBlock
 @see ActivationFactory
 @since 6.0
 */
public abstract class Activation implements Op.Cloneable {

    public Activation() {

    }

    /**
     Returns the parameters of the activation method, which can be used to
     configure other instances of the activation method.

     @return the parameters of the activation method
     */
    public abstract String parameters();

    /**
     Configures the activation method with the given parameters.

     @param parameters contains a list of space-separated parameter values
     */
    public abstract void configure(String parameters);

    /**
     Activates the rule block.

     @param ruleBlock is the rule block to activate
     */
    public abstract void activate(RuleBlock ruleBlock);

    /**
     Clones the activation method.

     @return a clone of the activation method
     */
    @Override
    public Activation clone() throws CloneNotSupportedException {
        return (Activation) super.clone();
    }

}
