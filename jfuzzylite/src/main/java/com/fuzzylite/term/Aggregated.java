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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;

import java.util.ArrayList;
import java.util.List;

/**
 The Aggregated class is a special Term that stores a fuzzy set with the
 Activated terms from the Antecedent%s of a Rule, thereby serving mainly as the
 fuzzy output value of the OutputVariable%s. The ownership of the activated
 terms will be transferred to objects of this class, and therefore their
 destructors will be called upon destruction of this term (or calling
 Aggregated::clear()).

 @author Juan Rada-Vilela, Ph.D.
 @see Antecedent
 @see Rule
 @see OutputVariable
 @see Activated
 @see Term
 @since 6.0
 */
public class Aggregated extends Term {

    private List<Activated> terms;
    private double minimum;
    private double maximum;
    private SNorm aggregation;

    public Aggregated() {
        this("");
    }

    public Aggregated(String name) {
        this(name, Double.NaN, Double.NaN, null);
    }

    public Aggregated(String name, double minimum, double maximum) {
        this(name, minimum, maximum, null);
    }

    public Aggregated(String name, double minimum, double maximum, SNorm aggregation) {
        this.terms = new ArrayList<Activated>();
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.aggregation = aggregation;
    }

    /**
     Returns the parameters of the term

     @return `"aggregation minimum maximum terms"`
     */
    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter();
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s %s %s",
                Op.str(minimum), Op.str(maximum),
                exporter.toString(aggregation)));
        for (Term term : terms) {
            result.append(" ").append(exporter.toString(term));
        }
        return result.toString();
    }

    /**
     Does nothing

     @param parameters are irrelevant
     */
    @Override
    public void configure(String parameters) {
        //do nothing
    }

    /**
     Aggregates the membership function values of `x` utilizing the
     aggregation operator

     @param x is a value
     @return `\sum_i{\mu_i(x)}, i \in \mbox{terms}`
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double mu = 0.0;
        for (Activated term : this.terms) {
            mu = this.aggregation.compute(mu, term.membership(x));
        }
        return mu;
    }

    /**
     Computes the aggregated activation degree for the given term. If the same
     term is present multiple times, the aggregation operator is utilized to sum
     the activation degrees of the term. If the aggregation operator is
     fl::null, a regular sum is performed.

     @param forTerm is the term for which to compute the aggregated activation
     degree
     @return the aggregated activation degree for the given term
     */
    public double activationDegree(Term forTerm) {
        double result = 0.0;
        for (Activated activatedTerm : this.terms) {
            if (activatedTerm.getTerm() == forTerm) {
                if (this.aggregation != null) {
                    result = this.aggregation.compute(result, activatedTerm.getDegree());
                } else {
                    result += activatedTerm.getDegree(); //Default for WeightDefuzzifier
                }
            }
        }
        return result;
    }

    /**
     Iterates over the Activated terms to find the term with the maximum
     activation degree

     @return the term with the maximum activation degree
     */
    public Activated highestActivatedTerm() {
        Activated maximumTerm = null;
        double maximumActivation = Double.POSITIVE_INFINITY;
        for (Activated activated : terms) {
            if (Op.isGt(activated.getDegree(), maximumActivation)) {
                maximumActivation = activated.getDegree();
                maximumTerm = activated;
            }
        }
        return maximumTerm;
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s[%s]",
                this.name, getClass().getSimpleName(),
                new FllExporter().toString(this.aggregation),
                Op.join(terms, ","));
    }

    /**
     Clears the list of activated terms
     */
    public void clear() {
        this.terms.clear();
    }

    /**
     Returns the list of activated terms

     @return the list of activated terms
     */
    public List<Activated> getTerms() {
        return terms;
    }

    /**
     Sets the activated terms

     @param terms is the activated terms
     */
    public void setTerms(List<Activated> terms) {
        this.terms = terms;
    }

    /**
     Gets the minimum of the range of the fuzzy set

     @return the minimum of the range of the fuzzy set
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     Sets the minimum of the range of the fuzzy set

     @param minimum is the minimum of the range of the fuzzy set
     */
    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    /**
     Gets the maximum of the range of the fuzzy set

     @return the maximum of the range of the fuzzy set
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     Sets the maximum of the range of the fuzzy set

     @param maximum is the maximum of the range of the fuzzy set
     */
    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    /**
     Sets the range of the fuzzy set to `[minimum, maximum]`

     @param minimum is the minimum of the range of the fuzzy set
     @param maximum is the maximum of the range of the fuzzy set
     */
    public void setRange(double minimum, double maximum) {
        setMinimum(minimum);
        setMaximum(maximum);
    }

    /**
     Returns the magnitude of the range of the fuzzy set,

     @return the magnitude of the range of the fuzzy set, i.e., `maximum -
     minimum`
     */
    public double range() {
        return getMaximum() - getMinimum();
    }

    /**
     Gets the aggregation operator

     @return the aggregation operator
     */
    public SNorm getAggregation() {
        return aggregation;
    }

    /**
     Sets the aggregation operator

     @param aggregation is the aggregation operator
     */
    public void setAggregation(SNorm aggregation) {
        this.aggregation = aggregation;
    }

    @Override
    public Aggregated clone() throws CloneNotSupportedException {
        Aggregated result = (Aggregated) super.clone();
        if (this.aggregation != null) {
            result.aggregation = this.aggregation.clone();
        }
        result.terms = new ArrayList<Activated>(this.terms.size());
        for (Activated term : this.terms) {
            result.terms.add(term.clone());
        }
        return result;
    }

}
