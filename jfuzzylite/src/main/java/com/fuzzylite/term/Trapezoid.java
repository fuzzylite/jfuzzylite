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
import java.util.List;

public class Trapezoid extends Term {

    private double a, b, c, d;

    public Trapezoid() {
        this("");
    }

    public Trapezoid(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Trapezoid(String name, double a, double d) {
        this.name = name;
        double range = d - a;
        this.a = a;
        this.d = d;
        this.b = a + range * 1.0 / 5.0;
        this.c = a + range * 4.0 / 5.0;
    }

    public Trapezoid(String name, double a, double b, double c, double d) {
        this.name = name;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public String parameters() {
        return Op.join(" ", a, b, c, d)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 4;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setA(Op.toDouble(values.get(0)));
        setB(Op.toDouble(values.get(1)));
        setC(Op.toDouble(values.get(2)));
        setD(Op.toDouble(values.get(3)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLE(x, a) || Op.isGE(x, d)) {
            return 0.0;
        } else if (Op.isLt(x, b)) {
            return Math.min(1.0, (x - a) / (b - a));
        } else if (Op.isLE(x, c)) {
            return 1.0;
        } else if (Op.isLt(x, d)) {
            return (d - x) / (d - c);
        }
        return 0.0;
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

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }
}
