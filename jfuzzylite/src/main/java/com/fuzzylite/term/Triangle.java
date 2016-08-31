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
 The Triangle class is a basic Term that represents the triangular membership
 function.

 @image html triangle.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Triangle extends Term {

    private double vertexA, vertexB, vertexC;

    public Triangle() {
        this("");
    }

    public Triangle(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN);
    }

    public Triangle(String name, double vertexA, double vertexC) {
        this(name, vertexA, (vertexA + vertexC) / 2.0, vertexC);
    }

    public Triangle(String name, double vertexA, double vertexB, double vertexC) {
        this(name, vertexA, vertexB, vertexC, 1.0);
    }

    public Triangle(String name, double vertexA, double vertexB, double vertexC, double height) {
        super(name, height);
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.vertexC = vertexC;
    }

    /**
     Returns the parameters of the term

     @return `"vertexA vertexB vertexC [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", vertexA, vertexB, vertexC)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"vertexA vertexB vertexC [height]"`
     */
    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 3;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        Iterator<String> it = values.iterator();
        setVertexA(Op.toDouble(it.next()));
        setVertexB(Op.toDouble(it.next()));
        setVertexC(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$\begin{cases} 0h & \mbox{if $x \not\in [a,c]$}\cr 1h & \mbox{if
     $x = b$}\cr h (x - a) / (b - a) & \mbox{if $x < b$} \cr h (c - x) / (c - b)
     & \mbox{otherwise} \end{cases}@f$

     where @f$h@f$ is the height of the Term, @f$a@f$ is the first vertex of the
     Triangle, @f$b@f$ is the second vertex of the Triangle, @ f $c@f$ is the
     third vertex of the Triangle
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLt(x, vertexA) || Op.isGt(x, vertexC)) {
            return height * 0.0;
        } else if (Op.isEq(x, vertexB)) {
            return height * 1.0;
        } else if (Op.isLt(x, vertexB)) {
            return height * (x - vertexA) / (vertexB - vertexA);
        } else {
            return height * (vertexC - x) / (vertexC - vertexB);
        }
    }

    /**
     Gets the first vertex of the triangle

     @return the first vertex of the triangle
     */
    public double getVertexA() {
        return vertexA;
    }

    /**
     Sets the first vertex of the triangle

     @param a is the first vertex of the triangle
     */
    public void setVertexA(double a) {
        this.vertexA = a;
    }

    /**
     Gets the second vertex of the triangle

     @return the second vertex of the triangle
     */
    public double getVertexB() {
        return vertexB;
    }

    /**
     Sets the second vertex of the triangle

     @param b is the second vertex of the triangle
     */
    public void setVertexB(double b) {
        this.vertexB = b;
    }

    /**
     Gets the third vertex of the triangle

     @return the third vertex of the triangle
     */
    public double getVertexC() {
        return vertexC;
    }

    /**
     Sets the third vertex of the triangle

     @param c is the third vertex of the triangle
     */
    public void setVertexC(double c) {
        this.vertexC = c;
    }

    @Override
    public Triangle clone() throws CloneNotSupportedException {
        return (Triangle) super.clone();
    }

}
