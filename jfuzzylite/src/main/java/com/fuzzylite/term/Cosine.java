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

import java.util.Iterator;
import java.util.List;

/**
 The Cosine class is an extended Term that represents the cosine membership
 function.

 @image html cosine.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 5.0
 */
public class Cosine extends Term {

    private double center, width;

    public Cosine() {
        this("");
    }

    public Cosine(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Cosine(String name, double center, double width) {
        this(name, center, width, 1.0);
    }

    public Cosine(String name, double center, double width, double height) {
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
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$\begin{cases} 0h & \mbox{if $x < c - 0.5w \vee x > c + 0.5w$}
     \cr 0.5h \times ( 1 + \cos(2.0 / w\pi(x-c))) & \mbox{otherwise}
     \end{cases}@f$

     where @f$h@f$ is the height of the Term,
     @f$c@f$ is the center of the Cosine,
     @f$w@f$ is the width of the Cosine
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Op.isLt(x, center - 0.5 * width)
                || Op.isGt(x, center + 0.5 * width)) {
            return height * 0.0;
        }
        return height * (0.5 * (1.0 + Math.cos(2.0 / width * Math.PI * (x - center))));
    }

    /**
     Gets the center of the cosine

     @return the center of the cosine
     */
    public double getCenter() {
        return center;
    }

    /**
     Sets the center of the cosine

     @param center is the center of the cosine
     */
    public void setCenter(double center) {
        this.center = center;
    }

    /**
     Gets the width of the cosine

     @return the width of the cosine
     */
    public double getWidth() {
        return width;
    }

    /**
     Sets the width of the cosine

     @param width is the width of the cosine
     */
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public Cosine clone() throws CloneNotSupportedException {
        return (Cosine) super.clone();
    }

}
