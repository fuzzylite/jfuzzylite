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
import java.util.List;

public class Sigmoid extends Term {

    protected double inflection, slope;

    public Sigmoid() {
        this("");
    }

    public Sigmoid(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Sigmoid(String name, double inflection, double slope) {
        this.name = name;
        this.inflection = inflection;
        this.slope = slope;
    }

    @Override
    public String parameters() {
        return Op.join(" ", inflection, slope)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 2;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setInflection(Op.toDouble(values.get(0)));
        setSlope(Op.toDouble(values.get(1)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return 1.0 / (1.0 + Math.exp(-slope * (x - inflection)));
    }

    public double getInflection() {
        return inflection;
    }

    public void setInflection(double inflection) {
        this.inflection = inflection;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

}
