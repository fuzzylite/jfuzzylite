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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import java.util.ArrayList;
import java.util.List;

public class Accumulated extends Term {

    private List<Activated> terms;
    private double minimum;
    private double maximum;
    private SNorm accumulation;

    public Accumulated() {
        this("");
    }

    public Accumulated(String name) {
        this(name, Double.NaN, Double.NaN, null);
    }

    public Accumulated(String name, double minimum, double maximum) {
        this(name, minimum, maximum, null);
    }

    public Accumulated(String name, double minimum, double maximum, SNorm accumulation) {
        this.terms = new ArrayList<Activated>();
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.accumulation = accumulation;
    }

    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter();
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s %s %s",
                Op.str(minimum), Op.str(maximum),
                exporter.toString(accumulation)));
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
            mu = this.accumulation.compute(mu, term.membership(x));
        }
        return mu;
    }

    public double activationDegree(Term term) {
        double result = 0.0;
        for (Activated activatedTerm : this.terms) {
            if (activatedTerm.getTerm() == term) {
                if (this.accumulation != null) {
                    result = this.accumulation.compute(result, activatedTerm.getDegree());
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
                new FllExporter().toString(this.accumulation),
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

    public SNorm getAccumulation() {
        return accumulation;
    }

    public void setAccumulation(SNorm accumulation) {
        this.accumulation = accumulation;
    }

    @Override
    public Accumulated clone() throws CloneNotSupportedException {
        Accumulated result = (Accumulated) super.clone();
        if (this.accumulation != null) {
            result.accumulation = this.accumulation.clone();
        }
        result.terms = new ArrayList<Activated>(this.terms.size());
        for (Activated term : this.terms) {
            result.terms.add(term.clone());
        }
        return result;
    }

}
