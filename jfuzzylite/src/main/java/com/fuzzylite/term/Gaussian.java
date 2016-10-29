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
 The Gaussian class is an extended Term that represents the %Gaussian curve
 membership function.

 @image html gaussian.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Gaussian extends Term {

    private double mean, standardDeviation;

    public Gaussian() {
        this("");
    }

    public Gaussian(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Gaussian(String name, double mean, double standardDeviation) {
        this(name, mean, standardDeviation, 1.0);
    }

    public Gaussian(String name, double mean, double standardDeviation, double height) {
        super(name, height);
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    /**
     Returns the parameters of the term

     @return `"mean standardDeviation [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", mean, standardDeviation)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"mean standardDeviation [height]"`
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
        setMean(Op.toDouble(it.next()));
        setStandardDeviation(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$ h \times \exp(-(x-\mu)^2/(2\sigma^2))@f$

     where @f$h@f$ is the height of the Term,
     @f$\mu@f$ is the mean of the Gaussian,
     @f$\sigma@f$ is the standard deviation of the Gaussian
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return height * Math.exp((-(x - mean) * (x - mean))
                / (2.0 * standardDeviation * standardDeviation));
    }

    /**
     Gets the mean of the Gaussian curve

     @return the mean of the Gaussian curve
     */
    public double getMean() {
        return mean;
    }

    /**
     Sets the mean of the Gaussian curve

     @param mean is the mean of the Gaussian curve
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     Gets the standard deviation of the Gaussian curve

     @return the standard deviation of the Gaussian curve
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     Sets the standard deviation of the Gaussian curve

     @param standardDeviation is the standard deviation of the Gaussian curve
     */
    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    @Override
    public Gaussian clone() throws CloneNotSupportedException {
        return (Gaussian) super.clone();
    }

}
