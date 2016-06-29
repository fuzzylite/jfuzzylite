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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.TNorm;

public class Activated extends Term {

    private Term term;
    private double degree;
    private TNorm implication;

    public Activated() {
        this(null, 1.0, null);
    }

    public Activated(Term term, double degree, TNorm implication) {
        this.term = term;
        this.degree = degree;
        this.implication = implication;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (implication == null) {
            throw new RuntimeException(String.format("[implication error] "
                    + "implication operator needed to activate %s",
                    getTerm().toString()));
        }
        return implication.compute(term.membership(x), degree);
    }

    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter();
        String result = String.format("%s %s %s", Op.str(degree),
                exporter.toString(implication), exporter.toString(term));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%s)",
                new FllExporter().toString(implication),
                Op.str(degree), term.getName());
    }

    @Override
    public void configure(String parameters) {
        //do nothing
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public TNorm getImplication() {
        return implication;
    }

    public void setImplication(TNorm implication) {
        this.implication = implication;
    }

    @Override
    public Activated clone() throws CloneNotSupportedException {
        return (Activated) super.clone();
    }

}
