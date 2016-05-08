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
import java.util.Iterator;
import java.util.List;

public class Spike extends Term {

    private double center, width;

    public Spike() {
        this("");
    }

    public Spike(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Spike(String name, double center, double width) {
        this(name, center, width, 1.0);
    }

    public Spike(String name, double center, double width, double height) {
        super(name, height);
        this.center = center;
        this.width = width;
    }

    @Override
    public String parameters() {
        return Op.join(" ", center, width)
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
        Iterator<String> it = values.iterator();
        setCenter(Op.toDouble(it.next()));
        setWidth(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return height * Math.exp(-Math.abs(10.0 / width * (x - center)));
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

    @Override
    public Spike clone() throws CloneNotSupportedException {
        return (Spike) super.clone();
    }

}
