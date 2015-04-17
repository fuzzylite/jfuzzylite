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

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RuleBlock {

    private String name;
    private List<Rule> rules;
    private TNorm conjunction;
    private SNorm disjunction;
    private TNorm activation;
    private boolean enabled;

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
        FuzzyLite.logger().fine("Activating ruleblock: " + name);
        for (Rule rule : rules) {
            if (rule.isLoaded()) {
                double activationDegree = rule.activationDegree(conjunction, disjunction);
                FuzzyLite.logger().fine(String.format("[degree=%s] %s", str(activationDegree), rule.toString()));
                if (Op.isGt(activationDegree, 0.0)) {
                    rule.activate(activationDegree, activation);
                }
            } else {
                FuzzyLite.logger().fine("Rule not loaded: " + rule.toString());
            }
        }
    }

    public void unloadRules() {
        for (Rule rule : this.rules) {
            rule.unload();
        }
    }

    public void loadRules(Engine engine) {
        List<String> exceptions = new LinkedList<String>();
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
        if (exceptions.size() > 0) {
            throw new RuntimeException("[ruleblock error] the following "
                    + "rules could not be loaded:\n" + Op.join(exceptions, "\n"));
        }
    }

    public void reloadRules(Engine engine) {
        unloadRules();
        loadRules(engine);
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
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

    @Override
    public RuleBlock clone() throws CloneNotSupportedException {
        RuleBlock result = (RuleBlock) super.clone();
        if (this.conjunction != null) {
            result.conjunction = this.conjunction.clone();
        }
        if (this.disjunction != null) {
            result.disjunction = this.disjunction.clone();
        }
        if (this.activation != null) {
            result.activation = this.activation.clone();
        }
        result.rules = new ArrayList<Rule>(this.rules.size());
        for (Rule rule : this.rules) {
            result.addRule(rule.clone());
        }
        return result;
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
