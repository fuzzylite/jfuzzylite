/*
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
 */
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.imex.FllExporter;

public class Thresholded extends Term {

    protected Term term;
    protected double threshold;
    protected TNorm activation;

    public Thresholded() {
        this(null, 1.0, null);
    }

    public Thresholded(Term term, double threshold, TNorm activation) {
        this.term = term;
        this.threshold = threshold;
        this.activation = activation;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return this.activation.compute(this.term.membership(x), this.threshold);
    }

    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter();
        String result = String.format("%s %s %s", Op.str(threshold),
                exporter.toString(activation), exporter.toString(term));
        return result;
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

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public TNorm getActivation() {
        return activation;
    }

    public void setActivation(TNorm activation) {
        this.activation = activation;
    }

}
