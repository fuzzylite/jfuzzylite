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
 The SigmoidProduct class is an extended Term that represents the product of two
 sigmoidal membership functions.

 @image html sigmoidProduct.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class SigmoidProduct extends Term {

    private double left, rising;
    private double falling, right;

    public SigmoidProduct() {
        this("");
    }

    public SigmoidProduct(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public SigmoidProduct(String name, double left, double rising,
            double falling, double right) {
        this(name, left, rising, falling, right, 1.0);
    }

    public SigmoidProduct(String name, double left, double rising,
            double falling, double right, double height) {
        super(name, height);
        this.left = left;
        this.rising = rising;
        this.falling = falling;
        this.right = right;
    }

    /**
     Returns the parameters of the term

     @return `"left rising falling right [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", left, rising, falling, right)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"left rising falling right [height]"`
     */
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
        setLeft(Op.toDouble(it.next()));
        setRising(Op.toDouble(it.next()));
        setFalling(Op.toDouble(it.next()));
        setRight(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$ h (a \times b)@f$

     where @f$h@f$ is the height,
     @f$a= 1 / (1 + \exp(-s_l *\times (x - i_l))) @f$,
     @f$b = 1 / (1 + \exp(-s_r \times (x - i_r)))@f$,
     @f$i_l@f$ is the left inflection of the SigmoidProduct,
     @f$s_l@f$ is the left slope of the SigmoidProduct,
     @f$i_r@f$ is the right inflection of the SigmoidProduct,
     @f$s_r@f$ is the right slope of the SigmoidProduct
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double a = 1.0 + Math.exp(-rising * (x - left));
        double b = 1.0 + Math.exp(-falling * (x - right));
        return height * 1.0 / (a * b);
    }

    /**
     Gets the inflection of the left sigmoidal curve

     @return the inflection of the left sigmoidal curve
     */
    public double getLeft() {
        return left;
    }

    /**
     Sets the inflection of the left sigmoidal curve

     @param leftInflection is the inflection of the left sigmoidal curve
     */
    public void setLeft(double leftInflection) {
        this.left = leftInflection;
    }

    public double getRising() {
        return rising;
    }

    /**
     Sets the slope of the left sigmoidal curve

     @param risingSlope is the slope of the left sigmoidal curve
     */
    public void setRising(double risingSlope) {
        this.rising = risingSlope;
    }

    /**
     Gets the slope of the right sigmoidal curve

     @return the slope of the right sigmoidal curve
     */
    public double getFalling() {
        return falling;
    }

    /**
     Sets the slope of the right sigmoidal curve

     @param fallingSlope is the slope of the right sigmoidal curve
     */
    public void setFalling(double fallingSlope) {
        this.falling = fallingSlope;
    }

    /**
     Gets the inflection of the right sigmoidal curve

     @return the inflection of the right sigmoidal curve
     */
    public double getRight() {
        return right;
    }

    /**
     Sets the inflection of the right sigmoidal curve

     @param rightInflection is the inflection of the right sigmoidal curve
     */
    public void setRight(double rightInflection) {
        this.right = rightInflection;
    }

    @Override
    public SigmoidProduct clone() throws CloneNotSupportedException {
        return (SigmoidProduct) super.clone();
    }

}
