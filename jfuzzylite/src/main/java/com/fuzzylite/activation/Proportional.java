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
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Proportional extends Activation {

    public Proportional() {
    }

    @Override
    public String parameters() {
        return "";
    }

    @Override
    public void configure(String parameters) {
        //do nothing...
    }

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
