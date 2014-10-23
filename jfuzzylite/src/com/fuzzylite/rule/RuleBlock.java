/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.rule;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import java.util.ArrayList;
import java.util.List;

public class RuleBlock {

    protected String name;
    protected List<Rule> rules;
    protected TNorm conjunction;
    protected SNorm disjunction;
    protected TNorm activation;
    protected boolean enabled;

    public RuleBlock() {
        this("");
    }

    public RuleBlock(String name) {
        this(name, null, null, null);
    }

    public RuleBlock(TNorm conjunction, SNorm disjunction, TNorm activation) {
        this("", conjunction, disjunction, activation);
    }

    public RuleBlock(String name, TNorm conjunction, SNorm disjunction, TNorm activation) {
        this.name = name;
        this.conjunction = conjunction;
        this.disjunction = disjunction;
        this.activation = activation;
        this.rules = new ArrayList<Rule>();
        this.enabled = true;
    }

    public void activate() {
        for (Rule rule : rules) {
            double activationDegree = rule.activationDegree(conjunction, disjunction);
            FuzzyLite.logger().fine(String.format("[degree=%s] %s", str(activationDegree), rule.toString()));
            if (Op.isGt(activationDegree, 0.0)) {
                rule.activate(activationDegree, activation);
            }
        }
    }

    @Override
    public String toString() {
        return new FllExporter("", "; ").toString(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TNorm getConjunction() {
        return conjunction;
    }

    public void setConjunction(TNorm conjunction) {
        this.conjunction = conjunction;
    }

    public SNorm getDisjunction() {
        return disjunction;
    }

    public void setDisjunction(SNorm disjunction) {
        this.disjunction = disjunction;
    }

    public TNorm getActivation() {
        return activation;
    }

    public void setActivation(TNorm activation) {
        this.activation = activation;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /*
     * Rules
     */
    public Rule getRule(int index) {
        return this.rules.get(index);
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public Rule removeRule(Rule rule) {
        return this.rules.remove(rule) ? rule : null;
    }

    public int numberOfRules() {
        return this.rules.size();
    }

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
