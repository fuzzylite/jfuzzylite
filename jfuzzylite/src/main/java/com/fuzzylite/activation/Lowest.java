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
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;

public class Lowest extends Activation {

    private int numberOfRules;

    public Lowest() {
        this(1);
    }

    public Lowest(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    @Override
    public String parameters() {
        return Op.str(getNumberOfRules());
    }

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

    @Override
    public void activate(RuleBlock ruleBlock) {
        FuzzyLite.logger().log(Level.FINE, "Activation: {0} {1}",
                new String[]{getClass().getName(), parameters()});
        TNorm conjunction = ruleBlock.getConjunction();
        SNorm disjunction = ruleBlock.getDisjunction();
        TNorm implication = ruleBlock.getImplication();

        PriorityQueue<Rule> rulesToActivate
                = new PriorityQueue<Rule>(getNumberOfRules(),
                        new Comparator<Rule>() {
                    @Override
                    public int compare(Rule a, Rule b) {
                        double result = Math.signum(a.getActivationDegree() - b.getActivationDegree());
                        return Double.isNaN(result) ? -1 : (int) result;
                    }
                });

        for (Rule rule : ruleBlock.getRules()) {
            rule.deactivate();

            if (rule.isLoaded()) {
                double activationDegree = rule.computeActivationDegree(conjunction, disjunction);
                rule.setActivationDegree(activationDegree);
                if (Op.isGt(activationDegree, 0.0)) {
                    rulesToActivate.offer(rule);
                }
            }
        }

        int activated = 0;
        while (rulesToActivate.size() > 0 && activated++ < getNumberOfRules()) {
            Rule rule = rulesToActivate.poll();
            rule.activate(rule.getActivationDegree(), implication);
        }
    }

    public int getNumberOfRules() {
        return numberOfRules;
    }

    public void setNumberOfRules(int numberOfRules) {
        this.numberOfRules = numberOfRules;
    }

    @Override
    public Highest clone() throws CloneNotSupportedException {
        return (Highest) super.clone();
    }
}
