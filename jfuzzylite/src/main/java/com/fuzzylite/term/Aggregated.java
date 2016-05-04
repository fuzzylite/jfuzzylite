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

package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void configure(String parameters) {
        //do nothing
    }

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

    public double activationDegree(Term term) {
        double result = 0.0;
        for (Activated activatedTerm : this.terms) {
            if (activatedTerm.getTerm() == term) {
                if (this.aggregation != null) {
                    result = this.aggregation.compute(result, activatedTerm.getDegree());
                } else {
                    result += activatedTerm.getDegree(); //Default for WeightDefuzzifier
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s[%s]",
                this.name, getClass().getSimpleName(),
                new FllExporter().toString(this.aggregation),
                Op.join(terms, ","));
    }

    public void clear() {
        this.terms.clear();
    }

    public List<Activated> getTerms() {
        return terms;
    }

    public void setTerms(List<Activated> terms) {
        this.terms = terms;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public void setRange(double minimum, double maximum) {
        setMinimum(minimum);
        setMaximum(maximum);
    }

    public SNorm getAggregation() {
        return aggregation;
    }

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
