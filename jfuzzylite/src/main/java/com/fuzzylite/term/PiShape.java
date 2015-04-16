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
import java.util.Iterator;
import java.util.List;

public class PiShape extends Term {

    private double bottomLeft, topLeft;
    private double topRight, bottomRight;

    public PiShape() {
        this("");
    }

    public PiShape(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public PiShape(String name, double bottomLeft, double topLeft,
            double topRight, double bottomRight) {
        this(name, bottomLeft, topLeft, topRight, bottomRight, 1.0);
    }

    public PiShape(String name, double bottomLeft, double topLeft,
            double topRight, double bottomRight, double height) {
        super(name, height);
        this.bottomLeft = bottomLeft;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
    }

    @Override
    public String parameters() {
        return Op.join(" ", bottomLeft, topLeft, topRight, bottomRight)
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
        Iterator<String> it = values.iterator();
        setBottomLeft(Op.toDouble(it.next()));
        setTopLeft(Op.toDouble(it.next()));
        setTopRight(Op.toDouble(it.next()));
        setBottomRight(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        //from Octave smf.m
        double a_b_ave = (bottomLeft + topLeft) / 2.0;
        double b_minus_a = topLeft - bottomLeft;
        double c_d_ave = (topRight + bottomRight) / 2.0;
        double d_minus_c = bottomRight - topRight;

        if (Op.isLE(x, bottomLeft)) {
            return height * 0.0;
        } else if (Op.isLE(x, a_b_ave)) {
            return height * 2.0 * Math.pow((x - bottomLeft) / b_minus_a, 2);
        } else if (Op.isLt(x, topLeft)) {
            return height * (1.0 - 2.0 * Math.pow((x - topLeft) / b_minus_a, 2));
        } else if (Op.isLE(x, topRight)) {
            return height * 1;
        } else if (Op.isLE(x, c_d_ave)) {
            return height * (1 - 2 * Math.pow((x - topRight) / d_minus_c, 2));
        } else if (Op.isLt(x, bottomRight)) {
            return height * (2 * Math.pow((x - bottomRight) / d_minus_c, 2));
        }

        return height * 0.0;
    }

    public double getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(double bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public double getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(double topLeft) {
        this.topLeft = topLeft;
    }

    public double getTopRight() {
        return topRight;
    }

    public void setTopRight(double topRight) {
        this.topRight = topRight;
    }

    public double getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(double bottomRight) {
        this.bottomRight = bottomRight;
    }

    @Override
    public PiShape clone() throws CloneNotSupportedException {
        return (PiShape) super.clone();
    }

}
