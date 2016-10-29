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
 The GaussianProduct class is an extended Term that represents the two-sided
 %Gaussian membership function.

 @image html gaussianProduct.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class GaussianProduct extends Term {

    private double meanA, standardDeviationA;
    private double meanB, standardDeviationB;

    public GaussianProduct() {
        this("");
    }

    public GaussianProduct(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public GaussianProduct(String name, double meanA, double standardDeviationA,
            double meanB, double standardDeviationB) {
        this(name, meanA, standardDeviationA, meanB, standardDeviationB, 1.0);
    }

    public GaussianProduct(String name, double meanA, double standardDeviationA,
            double meanB, double standardDeviationB, double height) {
        super(name, height);
        this.meanA = meanA;
        this.standardDeviationA = standardDeviationA;
        this.meanB = meanB;
        this.standardDeviationB = standardDeviationB;
    }

    /**
     Returns the parameters of the term

     @return `"meanA standardDeviationA meanB standardDeviationB [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", meanA, standardDeviationA, meanB, standardDeviationB)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"meanA standardDeviationA meanB standardDeviationB
     [height]"`
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
        setMeanA(Op.toDouble(it.next()));
        setStandardDeviationA(Op.toDouble(it.next()));
        setMeanB(Op.toDouble(it.next()));
        setStandardDeviationB(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$ h \left((1 - i) + i \times \exp(-(x - \mu_a)^2 /
     (2\sigma_a^2))\right) \left((1 - j) + j \times \exp(-(x - \mu_b)^2 / (2
     \sigma_b)^2)\right)
     @f$

     where @f$h@f$ is the height of the Term,
     @f$\mu_a@f$ is the mean of the first GaussianProduct,
     @f$\sigma_a@f$ is the standard deviation of the first GaussianProduct,
     @f$\mu_b@f$ is the mean of the second GaussianProduct,
     @f$\sigma_b@f$ is the standard deviation of the second GaussianProduct,
     @f$i=\begin{cases}1 & \mbox{if $x \leq \mu_a$} \cr 0
     &\mbox{otherwise}\end{cases}@f$,
     @f$j=\begin{cases}1 & \mbox{if $x \geq \mu_b$} \cr 0
     &\mbox{otherwise}\end{cases}@f$
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double a = 1.0, b = 1.0;
        if (Op.isLt(x, meanA)) {
            a = Math.exp((-(x - meanA) * (x - meanA))
                    / (2.0 * standardDeviationA * standardDeviationA));
        }
        if (Op.isGt(x, meanB)) {
            b = Math.exp((-(x - meanB) * (x - meanB))
                    / (2.0 * standardDeviationB * standardDeviationB));
        }
        return height * a * b;
    }

    /**
     Gets the mean of the first %Gaussian curve

     @return the mean of the first %Gaussian curve
     */
    public double getMeanA() {
        return meanA;
    }

    /**
     Sets the mean of the first %Gaussian curve

     @param meanA is the mean of the first %Gaussian curve
     */
    public void setMeanA(double meanA) {
        this.meanA = meanA;
    }

    /**
     Gets the standard deviation of the first %Gaussian curve

     @return the standard deviation of the first %Gaussian curve
     */
    public double getStandardDeviationA() {
        return standardDeviationA;
    }

    /**
     Sets the standard deviation of the first %Gaussian curve

     @param standardDeviationA is the standard deviation of the first %Gaussian
     curve
     */
    public void setStandardDeviationA(double standardDeviationA) {
        this.standardDeviationA = standardDeviationA;
    }

    /**
     Gets the mean of the second %Gaussian curve

     @return the mean of the second %Gaussian curve
     */
    public double getMeanB() {
        return meanB;
    }

    /**
     Sets the mean of the second %Gaussian curve

     @param meanB is the mean of the second %Gaussian curve
     */
    public void setMeanB(double meanB) {
        this.meanB = meanB;
    }

    /**
     Gets the standard deviation of the second %Gaussian curve

     @return the standard deviation of the second %Gaussian curve
     */
    public double getStandardDeviationB() {
        return standardDeviationB;
    }

    /**
     Sets the standard deviation of the second %Gaussian curve

     @param standardDeviationB is the standard deviation of the second %Gaussian
     curve
     */
    public void setStandardDeviationB(double standardDeviationB) {
        this.standardDeviationB = standardDeviationB;
    }

    @Override
    public GaussianProduct clone() throws CloneNotSupportedException {
        return (GaussianProduct) super.clone();
    }

}
