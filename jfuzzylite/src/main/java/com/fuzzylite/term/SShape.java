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
 The SShape class is an edge Term that represents the S-shaped membership
 function.

 @image html sShape.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class SShape extends Term {

    private double start, end;

    public SShape() {
        this("");
    }

    public SShape(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public SShape(String name, double start, double end) {
        this(name, start, end, 1.0);
    }

    public SShape(String name, double start, double end, double height) {
        super(name, height);
        this.start = start;
        this.end = end;
    }

    /**
     Returns the parameters of the term

     @return `"start end [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", start, end)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"start end [height]"`
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
        setStart(Op.toDouble(it.next()));
        setEnd(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$\begin{cases} 0h & \mbox{if $x \leq s$} \cr h(2 \left((x - s) /
     (e-s)\right)^2) & \mbox{if $x \leq 0.5(s+e)$}\cr h(1 - 2\left((x - e) /
     (e-s)\right)^2) & \mbox{if $x < e$}\cr 1h & \mbox{otherwise} \end{cases}@f$

     where @f$h@f$ is the height of the Term, @f$s@f$ is the start of the
     SShape, @f$e@f$ is the end of the SShape.
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLE(x, start)) {
            return height * 0.0;
        } else if (Op.isLE(x, 0.5 * (start + end))) {
            return height * 2.0 * Math.pow((x - start) / (end - start), 2);
        } else if (Op.isLt(x, end)) {
            return height * (1.0 - 2.0 * Math.pow((x - end) / (end - start), 2));
        }

        return height * 1.0;
    }

    @Override
    public double tsukamoto(double activationDegree, double minimum, double maximum) {
        double w = activationDegree;
        double z;

        double difference = end - start;
        double a = start + Math.sqrt(0.5 * w * difference * difference);
        double b = end + Math.sqrt(-0.5 * (w - 1.0) * difference * difference);
        if (Math.abs(w - membership(a)) < Math.abs(w - membership(b))) {
            z = a;
        } else {
            z = b;
        }
        return z;
    }

    @Override
    public boolean isMonotonic() {
        return true;
    }

    /**
     Gets the start of the edge

     @return the start of the edge
     */
    public double getStart() {
        return start;
    }

    /**
     Sets the start of the edge

     @param start is the start of the edge
     */
    public void setStart(double start) {
        this.start = start;
    }

    /**
     Gets the end of the edge

     @return the end of the edge
     */
    public double getEnd() {
        return end;
    }

    /**
     Sets the end of the edge

     @param end is the end of the edge
     */
    public void setEnd(double end) {
        this.end = end;
    }

    @Override
    public SShape clone() throws CloneNotSupportedException {
        return (SShape) super.clone();
    }

}
