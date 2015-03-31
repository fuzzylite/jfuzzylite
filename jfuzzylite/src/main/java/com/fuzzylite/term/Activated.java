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
import com.fuzzylite.norm.TNorm;

public class Activated extends Term {

    private Term term;
    private double degree;
    private TNorm activation;

    public Activated() {
        this(null, 1.0, null);
    }

    public Activated(Term term, double degree, TNorm activation) {
        this.term = term;
        this.degree = degree;
        this.activation = activation;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (activation == null) {
            throw new RuntimeException(String.format("[activation error] "
                    + "activation operator needed to activate %s",
                    term.toString()));
        }
        return this.activation.compute(this.term.membership(x), this.degree);
    }

    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter();
        String result = String.format("%s %s %s", Op.str(degree),
                exporter.toString(activation), exporter.toString(term));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%s)",
                new FllExporter().toString(activation),
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

    public TNorm getActivation() {
        return activation;
    }

    public void setActivation(TNorm activation) {
        this.activation = activation;
    }

    @Override
    public Activated clone() throws CloneNotSupportedException {
        return (Activated) super.clone();
    }

}
