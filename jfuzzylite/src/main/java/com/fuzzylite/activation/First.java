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
package com.fuzzylite.activation;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class First extends Activation {

    private int numberOfRules;
    private double threshold;

    public First() {
        this(1, 0.0);
    }

    public First(int numberOfRules, double threshold) {
        this.numberOfRules = numberOfRules;
        this.threshold = threshold;
    }

    //@todo: First(String parameters);
    @Override
    public String parameters() {
        return Op.str(getNumberOfRules()) + " " + Op.str(getThreshold());
    }

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

    @Override
    public void activate(RuleBlock ruleBlock) {
        FuzzyLite.logger().log(Level.FINE, "Activation: {0} {1}",
                new String[]{getClass().getName(), parameters()});
        TNorm conjunction = ruleBlock.getConjunction();
        SNorm disjunction = ruleBlock.getDisjunction();
        TNorm implication = ruleBlock.getImplication();

        int activated = 0;
        Iterator<Rule> it = ruleBlock.getRules().iterator();
        while (it.hasNext()) {
            Rule rule = it.next();
            rule.deactivate();

            if (rule.isLoaded()) {
                double activationDegree = rule.computeActivationDegree(conjunction, disjunction);
                rule.setActivationDegree(activationDegree);
                if (activated < getNumberOfRules()
                        && Op.isGt(activationDegree, 0.0)
                        && Op.isGE(activationDegree, getThreshold())) {
                    rule.activate(activationDegree, implication);
                    ++activated;
                }
            }
        }
    }

    public int getNumberOfRules() {
        return numberOfRules;
    }

    public void setNumberOfRules(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public First clone() throws CloneNotSupportedException {
        return (First) super.clone();
    }
}