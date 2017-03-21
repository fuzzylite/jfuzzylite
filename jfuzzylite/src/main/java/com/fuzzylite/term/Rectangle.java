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
 The Rectangle class is a basic Term that represents the rectangle membership
 function.

 @image html rectangle.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Rectangle extends Term {

    private double start, end;

    public Rectangle() {
        this("");
    }

    public Rectangle(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Rectangle(String name, double start, double end) {
        this(name, start, end, 1.0);
    }

    public Rectangle(String name, double start, double end, double height) {
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
     Computes the membership function evaluated at `x`

     @param x
     @return `\begin{cases} 1h & \mbox{if $x \in [s, e]$} \cr 0h &
     \mbox{otherwise} \end{cases}`

     where `h` is the height of the Term,
     `s` is the start of the Rectangle,
     `e` is the end of the Rectangle.
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Op.isGE(x, start) && Op.isLE(x, end)) {
            return height * 1.0;
        }
        return height * 0.0;
    }

    /**
     Gets the start of the rectangle

     @return the start of the rectangle
     */
    public double getStart() {
        return start;
    }

    /**
     Sets the start of the rectangle

     @param start is the start of the rectangle
     */
    public void setStart(double start) {
        this.start = start;
    }

    /**
     Gets the end of the rectangle

     @return the end of the rectangle
     */
    public double getEnd() {
        return end;
    }

    /**
     Sets the end of the rectangle

     @param end is the end of the rectangle
     */
    public void setEnd(double end) {
        this.end = end;
    }

    @Override
    public Rectangle clone() throws CloneNotSupportedException {
        return (Rectangle) super.clone();
    }

}
