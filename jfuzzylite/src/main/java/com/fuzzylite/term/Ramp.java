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
 The Ramp class is an edge Term that represents the ramp membership function.

 @image html ramp.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Ramp extends Term {

    /**
     Direction is an enumerator that indicates the direction of the ramp.
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

    private double start, end;

    public Ramp() {
        this("");
    }

    public Ramp(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Ramp(String name, double start, double end) {
        this(name, start, end, 1.0);
    }

    public Ramp(String name, double start, double end, double height) {
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
     @return
     @f$\begin{cases}

     0h & \mbox{if $x = e$}\cr

     \begin{cases} 0h & \mbox{if $x \leq s$}\cr 1h & \mbox{if $x \geq e$}\cr h
     (x - s) / (e - s) & \mbox{otherwise}\cr \end{cases} & \mbox{if $s < e$}\cr

     \begin{cases}
     0h & \mbox{if $x \geq s$}\cr
     1h & \mbox{if $x \leq e$}\cr
     h (s - x) / (s - e) & \mbox{otherwise}
     \end{cases} & \mbox{if $s > e$}\cr \end{cases}@f$

     where @f$h@f$ is the height of the Term,
     @f$s@f$ is the start of the Ramp,
     @f$e@f$ is the end of the Ramp
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isEq(start, end)) {
            return height * 0.0;
        }

        if (Op.isLt(start, end)) {
            if (Op.isLE(x, start)) {
                return height * 0.0;
            }
            if (Op.isGE(x, end)) {
                return height * 1.0;
            }
            return height * (x - start) / (end - start);
        } else {
            if (Op.isGE(x, start)) {
                return height * 0.0;
            }
            if (Op.isLE(x, end)) {
                return height * 1.0;
            }
            return height * (start - x) / (start - end);
        }
    }

    /**
     Gets the start of the ramp

     @return the start of the ramp
     */
    public double getStart() {
        return start;
    }

    /**
     Sets the start of the ramp

     @param start is the start of the ramp
     */
    public void setStart(double start) {
        this.start = start;
    }

    /**
     Gets the end of the ramp

     @return the end of the ramp
     */
    public double getEnd() {
        return end;
    }

    /**
     Sets the end of the ramp

     @param end is the end of the ramp
     */
    public void setEnd(double end) {
        this.end = end;
    }

    /**
     Returns the direction of the ramp

     @return the direction of the ramp
     */
    public Direction direction() {
        double range = this.end - this.start;
        if (!Op.isFinite(range) || Op.isEq(range, 0.0)) {
            return Direction.Zero;
        }
        if (Op.isGt(range, 0.0)) {
            return Direction.Positive;
        }
        return Direction.Negative;
    }

    @Override
    public Ramp clone() throws CloneNotSupportedException {
        return (Ramp) super.clone();
    }

}
