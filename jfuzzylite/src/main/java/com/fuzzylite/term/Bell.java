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
 The Bell class is an extended Term that represents the generalized bell curve
 membership function.

 @image html bell.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Bell extends Term {

    private double center, width, slope;

    public Bell() {
        this("");
    }

    public Bell(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN);
    }

    public Bell(String name, double center, double width, double slope) {
        this(name, center, width, slope, 1.0);
    }

    public Bell(String name, double center, double width, double slope, double height) {
        super(name, height);
        this.center = center;
        this.width = width;
        this.slope = slope;
    }

    /**
     Returns the parameters of the term

     @return `"center width slope [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", center, width, slope)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"center width slope [height]"`
     */
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
        Iterator<String> it = values.iterator();
        setCenter(Op.toDouble(it.next()));
        setWidth(Op.toDouble(it.next()));
        setSlope(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$h / (1 + \left(|x-c|/w\right)^{2s}@f$

     where @f$h@f$ is the height of the Term,
     @f$c@f$ is the center of the Bell,
     @f$w@f$ is the width of the Bell,
     @f$s@f$ is the slope of the Bell
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return height * 1.0 / (1.0 + Math.pow(Math.abs((x - center) / width), 2.0 * slope));
    }

    /**
     Gets the center of the bell curve

     @return the center of the bell curve
     */
    public double getCenter() {
        return center;
    }

    /**
     Sets the center of the bell curve

     @param center is the center of the bell curve
     */
    public void setCenter(double center) {
        this.center = center;
    }

    /**
     Gets the width of the bell curve

     @return the width of the bell curve
     */
    public double getWidth() {
        return width;
    }

    /**
     Sets the width of the bell curve

     @param width is the width of the bell curve
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     Gets the slope of the bell curve

     @return the slope of the bell curve
     */
    public double getSlope() {
        return slope;
    }

    /**
     Sets the slope of the bell curve

     @param slope is the slope of the bell curve
     */
    public void setSlope(double slope) {
        this.slope = slope;
    }

    @Override
    public Bell clone() throws CloneNotSupportedException {
        return (Bell) super.clone();
    }

}
