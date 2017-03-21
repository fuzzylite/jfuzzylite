/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.term;

import com.fuzzylite.Op;

import java.util.Iterator;
import java.util.List;

/**
 The Spike class is an extended Term that represents the spike membership
 function.

 @image html spike.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 5.0
 */
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

    /**
     Returns the parameters of the term

     @return `"center width [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", center, width)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"center width [height]"`
     */
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

    /**
     Computes the membership function evaluated at `x`

     @param x
     @return `h \times \exp(-|10 / w (x - c)|)`

     where `h` is the height of the Term,
     `w` is the width of the Spike,
     `c` is the center of the Spike
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return height * Math.exp(-Math.abs(10.0 / width * (x - center)));
    }

    /**
     Gets the center of the spike

     @return the center of the spike
     */
    public double getCenter() {
        return center;
    }

    /**
     Sets the center of the spike

     @param center is the center of the spike
     */
    public void setCenter(double center) {
        this.center = center;
    }

    /**
     Gets the width of the spike

     @return the width of the spike
     */
    public double getWidth() {
        return width;
    }

    /**
     Sets the width of the spike

     @param width is the width of the spike
     */
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public Spike clone() throws CloneNotSupportedException {
        return (Spike) super.clone();
    }

}
