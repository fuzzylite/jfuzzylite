/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

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

public class Threshold extends Activation {

    public enum Comparison {
        /**
         * @f$a < \theta@f$
         */
        LessThan,
        /**
         * @f$a \leq \theta@f$
         */
        LessThanOrEqualTo,
        /**
         * @f$a = \theta@f$
         */
        EqualTo,
        /**
         * @f$a \neq \theta@f$
         */
        NotEqualTo,
        /**
         * @f$a \geq \theta@f$
         */
        GreaterThanOrEqualTo,
        /**
         * @f$a > \theta@f$
         */
        GreaterThan;

        static final String[] operators = new String[]{
            "<", "<=", "==", "!=", ">=", ">"
        };

        public String operator() {
            return operators[this.ordinal()];
        }

        public static Comparison fromOperator(String operator) {
            for (int i = 0; i < operators.length; ++i) {
                if (operators[i].equals(operator)) {
                    return values()[i];
                }
            }
            throw new RuntimeException(MessageFormat.format(
                    "Comparison operator <{0}> not available", operator));
        }
    }

    private Comparison comparison;
    private double value;

    public Threshold() {
        this(Comparison.GreaterThanOrEqualTo, 0.0);
    }

    public Threshold(Comparison comparison, double threshold) {
        this.comparison = comparison;
        this.value = threshold;
    }

    public Comparison getComparison() {
        return comparison;
    }

    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double threshold) {
        this.value = threshold;
    }

    public void setThreshold(Comparison comparison, double value) {
        setComparison(comparison);
        setValue(value);
    }

    @Override
    public String parameters() {
        return getComparison().operator() + " " + Op.str(getValue());
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

        setComparison(Comparison.fromOperator(values.get(0)));
        setValue(Op.toDouble(values.get(1)));
    }

    boolean activatesWith(double activationDegree) {
        //favour if-then-return over switch to avoid new file Threshold$1.class
        if (Comparison.LessThan == this.comparison) {
            return Op.isLt(activationDegree, getValue());
        }
        if (Comparison.LessThanOrEqualTo == this.comparison) {
            return Op.isLE(activationDegree, getValue());
        }
        if (Comparison.EqualTo == this.comparison) {
            return Op.isEq(activationDegree, getValue());
        }
        if (Comparison.NotEqualTo == this.comparison) {
            return !Op.isEq(activationDegree, getValue());
        }
        if (Comparison.GreaterThanOrEqualTo == this.comparison) {
            return Op.isGE(activationDegree, getValue());
        }
        if (Comparison.GreaterThan == this.comparison) {
            return Op.isGt(activationDegree, getValue());
        }
        return false;
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

        for (Rule rule : ruleBlock.getRules()) {
            rule.deactivate();
            if (rule.isLoaded()) {
                double activationDegree = rule.computeActivationDegree(conjunction, disjunction);
                rule.setActivationDegree(activationDegree);
                if (activatesWith(activationDegree)) {
                    rule.activate(activationDegree, implication);
                }
            }
        }
    }

    @Override
    public Threshold clone() throws CloneNotSupportedException {
        return (Threshold) super.clone();
    }

}
