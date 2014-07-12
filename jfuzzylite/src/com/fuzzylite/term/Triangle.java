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

public class Triangle extends Term {

    protected double a, b, c;

    public Triangle() {
        this("");
    }

    public Triangle(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN);
    }

    public Triangle(String name, double a, double c) {
        this(name, a, (a + c) / 2, c);
    }

    public Triangle(String name, double a, double b, double c) {
        this.name = name;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public String parameters() {
        return Op.join(" ", a, b, c);
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
        setA(Op.toDouble(values.get(0)));
        setB(Op.toDouble(values.get(1)));
        setC(Op.toDouble(values.get(2)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLE(x, a) || Op.isGE(x, c)) {
            return 0.0;
        } else if (Op.isEq(x, b)) {
            return 1.0;
        } else if (Op.isLt(x, b)) {
            return (x - a) / (b - a);
        } else {
            return (c - x) / (c - b);
        }
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

}
