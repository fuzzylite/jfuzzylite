/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
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
 The Sigmoid class is an edge Term that represents the sigmoid membership
 function.

 @image html sigmoid.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Sigmoid extends Term {

    /**
     Direction is an enumerator that indicates the direction of the sigmoid.
     */
    public enum Direction {
        /**
         `(_/)` increases to the right
         */
        Positive,
        /**
         `(--)` slope is zero
         */
        Zero,
        /**
         `(\\_)` increases to the left
         */
        Negative
    }
    private double inflection, slope;

    public Sigmoid() {
        this("");
    }

    public Sigmoid(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Sigmoid(String name, double inflection, double slope) {
        this(name, inflection, slope, 1.0);
    }

    public Sigmoid(String name, double inflection, double slope, double height) {
        super(name, height);
        this.inflection = inflection;
        this.slope = slope;
    }

    /**
     Returns the parameters of the term

     @return `"inflection slope [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", inflection, slope)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"inflection slope [height]"`
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
        setInflection(Op.toDouble(it.next()));
        setSlope(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$ h / (1 + \exp(-s(x-i)))@f$

     where @f$h@f$ is the height of the Term,
     @f$s@f$ is the slope of the Sigmoid,
     @f$i@f$ is the inflection of the Sigmoid
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return height * 1.0 / (1.0 + Math.exp(-slope * (x - inflection)));
    }

    /**
     Gets the inflection of the sigmoid

     @return the inflection of the sigmoid
     */
    public double getInflection() {
        return inflection;
    }

    /**
     Sets the inflection of the sigmoid

     @param inflection is the inflection of the sigmoid
     */
    public void setInflection(double inflection) {
        this.inflection = inflection;
    }

    /**
     Gets the slope of the sigmoid

     @return the slope of the sigmoid
     */
    public double getSlope() {
        return slope;
    }

    /**
     Sets the slope of the sigmoid

     @param slope is the slope of the sigmoid
     */
    public void setSlope(double slope) {
        this.slope = slope;
    }

    /**
     Returns the direction of the sigmoid

     @return the direction of the sigmoid
     */
    public Direction direction() {
        if (!Op.isFinite(slope) || Op.isEq(slope, 0.0)) {
            return Direction.Zero;
        }
        if (Op.isGt(slope, 0.0)) {
            return Direction.Positive;
        }
        return Direction.Negative;
    }

    @Override
    public Sigmoid clone() throws CloneNotSupportedException {
        return (Sigmoid) super.clone();
    }
}
