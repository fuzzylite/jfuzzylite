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

public class Bell extends Term {

    protected double center, width, slope;

    public Bell() {
        this("");
    }

    public Bell(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN);
    }

    public Bell(String name, double center, double width, double slope) {
        super.name = name;
        this.center = center;
        this.width = width;
        this.slope = slope;
    }

    @Override
    public String parameters() {
        return Op.join(" ", center, width, slope);
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 3;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setCenter(Op.toDouble(values.get(0)));
        setWidth(Op.toDouble(values.get(1)));
        setSlope(Op.toDouble(values.get(2)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        //from octave: gbellmf.m
        return 1.0 / (1.0 + Math.pow(Math.abs((x - center) / width), 2 * slope));
    }

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

}
