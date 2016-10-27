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
 The ZShape class is an edge Term that represents the Z-shaped membership
 function.

 @image html zShape.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class ZShape extends Term {

    private double start, end;

    public ZShape() {
        this("");
    }

    public ZShape(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public ZShape(String name, double start, double end) {
        this(name, start, end, 1.0);
    }

    public ZShape(String name, double start, double end, double height) {
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
     @return @f$ \begin{cases} 1h & \mbox{if $x \leq s$} \cr h(1 - 2\left((x -
     s) / (e-s)\right)^2) & \mbox{if $x \leq 0.5(s+e)$}\cr h(2 \left((x - e) /
     (e-s)\right)^2) & \mbox{if $x < e$}\cr 0h & \mbox{otherwise} \end{cases}@f$

     where @f$h@f$ is the height of the Term, @f$s@f$ is the start of the
     ZShape, @f$e@f$ is the end of the ZShape.
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Op.isLE(x, start)) {
            return height * 1.0;
        } else if (Op.isLE(x, 0.5 * (start + end))) {
            return height * (1.0 - 2.0 * Math.pow((x - start) / (end - start), 2));
        } else if (Op.isLt(x, end)) {
            return height * (2.0 * Math.pow((x - end) / (end - start), 2));
        }
        return height * 0.0;
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
    public ZShape clone() throws CloneNotSupportedException {
        return (ZShape) super.clone();
    }

}
