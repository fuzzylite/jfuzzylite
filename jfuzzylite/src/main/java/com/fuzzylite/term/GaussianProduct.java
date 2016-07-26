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

    @Override
    public String parameters() {
        return Op.join(" ", meanA, standardDeviationA, meanB, standardDeviationB)
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
        setMeanA(Op.toDouble(it.next()));
        setStandardDeviationA(Op.toDouble(it.next()));
        setMeanB(Op.toDouble(it.next()));
        setStandardDeviationB(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double a = 1.0, b = 1.0;
        if (Op.isLt(x, meanA)) {
            a = Math.exp((-(x - meanA) * (x - meanA)) /
                    (2.0 * standardDeviationA * standardDeviationA));
        }
        if (Op.isGt(x, meanB)) {
            b = Math.exp((-(x - meanB) * (x - meanB)) /
                    (2.0 * standardDeviationB * standardDeviationB));
        }
        return height * a * b;
    }

    public double getMeanA() {
        return meanA;
    }

    public void setMeanA(double meanA) {
        this.meanA = meanA;
    }

    public double getStandardDeviationA() {
        return standardDeviationA;
    }

    public void setStandardDeviationA(double standardDeviationA) {
        this.standardDeviationA = standardDeviationA;
    }

    public double getMeanB() {
        return meanB;
    }

    public void setMeanB(double meanB) {
        this.meanB = meanB;
    }

    public double getStandardDeviationB() {
        return standardDeviationB;
    }

    public void setStandardDeviationB(double standardDeviationB) {
        this.standardDeviationB = standardDeviationB;
    }

    @Override
    public GaussianProduct clone() throws CloneNotSupportedException {
        return (GaussianProduct) super.clone();
    }

}
