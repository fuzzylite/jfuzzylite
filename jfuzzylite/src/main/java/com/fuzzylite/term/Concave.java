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
 The Concave class is an edge Term that represents the concave membership
 function.

 @image html concave.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 5.0
 */
public class Concave extends Term {

    private double inflection, end;

    public Concave() {
        this("");
    }

    public Concave(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Concave(String name, double inflection, double end) {
        this(name, inflection, end, 1.0);
    }

    public Concave(String name, double inflection, double end, double height) {
        super(name, height);
        this.inflection = inflection;
        this.end = end;
    }

    /**
     Returns the parameters of the term as

     @return `"inflection end [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", inflection, end)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters given

     @param parameters as `"inflection end [height]"`
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
        setEnd(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$\begin{cases} h \times (e - i) / (2e - i - x) & \mbox{if $i \leq
     e \wedge x < e$
     (increasing concave)} \cr
     h \times (i - e) / (-2e + i + x) & \mbox{if $i > e \wedge x > e$
     (decreasing concave)} \cr h & \mbox{otherwise} \cr \end{cases}@f$

     where @f$h@f$ is the height of the Term,
     @f$i@f$ is the inflection of the Concave,
     @f$e@f$ is the end of the Concave
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Op.isLE(inflection, end)) { //Concave increasing
            if (Op.isLt(x, end)) {
                return height * (end - inflection) / (2.0 * end - inflection - x);
            }
        } else if (Op.isGt(x, end)) { //Concave decreasing
            return height * (inflection - end) / (inflection - 2.0 * end + x);
        }
        return height * 1.0;
    }

    @Override
    public double tsukamoto(double activationDegree, double minimum, double maximum) {
        double i = getInflection();
        double e = getEnd();
        return (i - e) / membership(activationDegree) + 2 * e - i;
    }

    @Override
    public boolean isMonotonic() {
        return true;
    }

    /**
     Gets the inflection of the curve

     @return the inflection of the curve
     */
    public double getInflection() {
        return inflection;
    }

    /**
     Sets the inflection of the curve

     @param inflection is the inflection of the curve
     */
    public void setInflection(double inflection) {
        this.inflection = inflection;
    }

    /**
     Gets the end of the curve

     @return the end of the curve
     */
    public double getEnd() {
        return end;
    }

    /**
     Sets the end of the curve

     @param end is the end of the curve
     */
    public void setEnd(double end) {
        this.end = end;
    }

    @Override
    public Concave clone() throws CloneNotSupportedException {
        return (Concave) super.clone();
    }

}
