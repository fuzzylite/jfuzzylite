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
 The Trapezoid class is a basic Term that represents the trapezoidal membership
 function.

 @image html trapezoid.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Trapezoid extends Term {

    private double vertexA, vertexB, vertexC, vertexD;

    public Trapezoid() {
        this("");
    }

    public Trapezoid(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Trapezoid(String name, double vertexA, double vertexD) {
        this(name, vertexA,
                vertexA + (vertexD - vertexA) * 1.0 / 5.0,
                vertexA + (vertexD - vertexA) * 4.0 / 5.0,
                vertexD);
    }

    public Trapezoid(String name, double vertexA, double vertexB,
            double vertexC, double vertexD) {
        this(name, vertexA, vertexB, vertexC, vertexD, 1.0);
    }

    public Trapezoid(String name, double vertexA, double vertexB,
            double vertexC, double vertexD, double height) {
        super(name, height);
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.vertexC = vertexC;
        this.vertexD = vertexD;
    }

    /**
     Returns the parameters of the term

     @return `"vertexA vertexB vertexC vertexD [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", vertexA, vertexB, vertexC, vertexD)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"vertexA vertexB vertexC vertexD [height]"`
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
        setVertexA(Op.toDouble(it.next()));
        setVertexB(Op.toDouble(it.next()));
        setVertexC(Op.toDouble(it.next()));
        setVertexD(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$\begin{cases} 0h & \mbox{if $x \not\in[a,d]$}\cr h \times
     \min(1, (x - a) / (b - a)) & \mbox{if $x < b$}\cr 1h & \mbox{if $x \leq
     c$}\cr h (d - x) / (d - c) & \mbox{if $x < d$}\cr 0h & \mbox{otherwise}
     \end{cases}@f$

     where @f$h@f$ is the height of the Term, @f$a@f$ is the first vertex of the
     Trapezoid, @f$b@f$ is the second vertex of the Tr a pezoid, @f$c@f$ is the
     third vertex of the Trapezoid, @f$d@f$ is the fourth vertex of the
     Trapezoid
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLt(x, vertexA) || Op.isGt(x, vertexD)) {
            return height * 0.0;
        } else if (Op.isLt(x, vertexB)) {
            return height * Math.min(1.0, (x - vertexA) / (vertexB - vertexA));
        } else if (Op.isLE(x, vertexC)) {
            return height * 1.0;
        } else if (Op.isLt(x, vertexD)) {
            return height * (vertexD - x) / (vertexD - vertexC);
        }
        return height * 0.0;
    }

    /**
     Gets the first vertex of the trapezoid

     @return the first vertex of the trapezoid
     */
    public double getVertexA() {
        return vertexA;
    }

    /**
     Sets the first vertex of the trapezoid

     @param a is the first vertex of the trapezoid
     */
    public void setVertexA(double a) {
        this.vertexA = a;
    }

    /**
     Gets the second vertex of the trapezoid

     @return the second vertex of the trapezoid
     */
    public double getVertexB() {
        return vertexB;
    }

    /**
     Sets the second vertex of the trapezoid

     @param b is the second vertex of the trapezoid
     */
    public void setVertexB(double b) {
        this.vertexB = b;
    }

    /**
     Gets the third vertex of the trapezoid

     @return the third vertex of the trapezoid
     */
    public double getVertexC() {
        return vertexC;
    }

    /**
     Sets the third vertex of the trapezoid

     @param c is the third vertex of the trapezoid
     */
    public void setVertexC(double c) {
        this.vertexC = c;
    }

    /**
     Gets the fourth vertex of the trapezoid

     @return the fourth vertex of the trapezoid
     */
    public double getVertexD() {
        return vertexD;
    }

    /**
     Sets the fourth vertex of the trapezoid

     @param d is the fourth vertex of the trapezoid
     */
    public void setVertexD(double d) {
        this.vertexD = d;
    }

    @Override
    public Trapezoid clone() throws CloneNotSupportedException {
        return (Trapezoid) super.clone();
    }

}
