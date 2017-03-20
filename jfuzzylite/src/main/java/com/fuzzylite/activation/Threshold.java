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
 The Threshold class is a RuleBlock Activation method that activates the rules
 whose activation degrees satisfy the equation given by the comparison operator
 and the threshold, and deactivates the rules which do not satisfy the equation.

 @author Juan Rada-Vilela, Ph.D.
 @see Rule
 @see RuleBlock
 @see ActivationFactory
 @since 6.0
 */
public class Threshold extends Activation {

    /**
     Comparison is an enumerator that provides six comparison operators between
     the activation degree @f$a@f$ and the threshold @f$\theta@f$.
     */
    public enum Comparison {
        /**
         @f$a < \theta@f$
         */
        LessThan,
        /**
         @f$a \leq \theta@f$
         */
        LessThanOrEqualTo,
        /**
         @f$a = \theta@f$
         */
        EqualTo,
        /**
         @f$a \neq \theta@f$
         */
        NotEqualTo,
        /**
         @f$a \geq \theta@f$
         */
        GreaterThanOrEqualTo,
        /**
         @f$a > \theta@f$
         */
        GreaterThan;

        static final String[] operators = new String[]{
            "<", "<=", "==", "!=", ">=", ">"
        };

        /**
         Returns the comparison operator

         @return the comparison operator
         */
        public String operator() {
            return operators[this.ordinal()];
        }

        /**
         Returns the enumerator from comparison operator

         @param operator is the comparison operator
         @return the enumerator from the comparison operator
         @throws RuntimeException if the given comparison operator is not valid
         */
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

    /**
     Gets the comparison operator for the activation method

     @return comparison operator for the activation method
     */
    public Comparison getComparison() {
        return comparison;
    }

    /**
     Sets the comparison operator for the activation method

     @param comparison is the operator for the activation method
     */
    public void setComparison(Comparison comparison) {
        this.comparison = comparison;
    }

    /**
     Gets the threshold value of the activation method

     @return the threshold value of the activation method
     */
    public double getValue() {
        return value;
    }

    /**
     Sets the threshold value of the activation method

     @param value is the threshold value for activation degrees
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     Sets the comparison operator and the threshold for the activation method

     @param comparison is the comparison enumerator
     @param value is the threshold of the activation method
     */
    public void setThreshold(Comparison comparison, double value) {
        setComparison(comparison);
        setValue(value);
    }

    /**
     Returns the comparison operator followed by the threshold.

     @return comparison operator and threshold
     */
    @Override
    public String parameters() {
        return getComparison().operator() + " " + Op.str(getValue());
    }

    /**
     Configures the activation method with the comparison operator and the
     threshold.

     @param parameters is the comparison operator and threshold
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

        setComparison(Comparison.fromOperator(values.get(0)));
        setValue(Op.toDouble(values.get(1)));
    }

    /**
     Returns whether the activation method will activate a rule with the given
     activation degree

     @param activationDegree an activation degree
     @return whether the comparison equation is satisfied with the activation
     degree and the threshold
     */
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

    /**
     Activates the rules whose activation degrees satisfy the comparison
     equation with the given threshold, and deactivate the rules which do not.

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
                double activationDegree = rule.activateWith(conjunction, disjunction);
                if (activatesWith(activationDegree)) {
                    rule.trigger(implication);
                }
            }
        }
    }

    @Override
    public Threshold clone() throws CloneNotSupportedException {
        return (Threshold) super.clone();
    }

}
