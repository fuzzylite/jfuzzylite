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
 The PiShape class is an extended Term that represents the Pi-shaped curve
 membership function.

 @image html piShape.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class PiShape extends Term {

    private double bottomLeft, topLeft;
    private double topRight, bottomRight;

    public PiShape() {
        this("");
    }

    public PiShape(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public PiShape(String name, double bottomLeft, double topLeft,
            double topRight, double bottomRight) {
        this(name, bottomLeft, topLeft, topRight, bottomRight, 1.0);
    }

    public PiShape(String name, double bottomLeft, double topLeft,
            double topRight, double bottomRight, double height) {
        super(name, height);
        this.bottomLeft = bottomLeft;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
    }

    /**
     Returns the parameters of the term

     @return `"bottomLeft topLeft topRight bottomRight [height]"`
     */
    @Override
    public String parameters() {
        return Op.join(" ", bottomLeft, topLeft, topRight, bottomRight)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    /**
     Configures the term with the parameters

     @param parameters as `"bottomLeft topLeft topRight bottomRight [height]"`
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
        setBottomLeft(Op.toDouble(it.next()));
        setTopLeft(Op.toDouble(it.next()));
        setTopRight(Op.toDouble(it.next()));
        setBottomRight(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    /**
     Computes the membership function evaluated at @f$x@f$

     @param x
     @return @f$\begin{cases} 0h & \mbox{if $x \leq b_l$}\cr 2h \left((x - b_l)
     / (t_l-b_l)\right)^2 & \mbox{if $x \leq 0.5(a+b)$}\cr h (1 - 2 \left((x -
     t_l) / (t_l-b_l)\right)^2) & \mbox{if $ x < t_l$}\cr h & \mbox{if $x \leq
     t_r$}\cr h (1 - 2\left((x - t_r) / (b_r - t_r)\right)^2) & \mbox{if $x \leq
     0.5(t_r + b_r)$}\cr 2h \left((x - b_r) / (b_r-t_r)\right)^2 & \mbox{if $x <
     b_r$} \cr 0h & \mbox{otherwise} \end{cases}@f$

     where @f$h@f$ is the height of the Term, @f$b_l@f$ is the bottom left of
     the PiShape, @f$t_l@f$ is the top left of the PiShape, @f$t_r@f$ is the top
     right of the PiShape @f$b_r@f$ is the bottom right of the PiShape,
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double sshape;
        if (Op.isLE(x, bottomLeft)) {
            sshape = 0.0;
        } else if (Op.isLE(x, 0.5 * (bottomLeft + topLeft))) {
            sshape = 2.0 * Math.pow((x - bottomLeft) / (topLeft - bottomLeft), 2);
        } else if (Op.isLt(x, topLeft)) {
            sshape = 1.0 - 2.0 * Math.pow((x - topLeft) / (topLeft - bottomLeft), 2);
        } else {
            sshape = 1.0;
        }

        double zshape;
        if (Op.isLE(x, topRight)) {
            zshape = 1.0;
        } else if (Op.isLE(x, 0.5 * (topRight + bottomRight))) {
            zshape = 1.0 - 2.0 * Math.pow((x - topRight) / (bottomRight - topRight), 2);
        } else if (Op.isLt(x, bottomRight)) {
            zshape = 2.0 * Math.pow((x - bottomRight) / (bottomRight - topRight), 2);
        } else {
            zshape = 0.0;
        }

        return height * sshape * zshape;
    }

    /**
     Gets the bottom-left value of the curve

     @return the bottom-left value of the curve
     */
    public double getBottomLeft() {
        return bottomLeft;
    }

    /**
     Sets the bottom-left value of the curve

     @param bottomLeft is the bottom-left value of the curve
     */
    public void setBottomLeft(double bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    /**
     Gets the top-left value of the curve

     @return the top-left value of the curve
     */
    public double getTopLeft() {
        return topLeft;
    }

    /**
     Sets the top-left value of the curve

     @param topLeft is the top-left value of the curve
     */
    public void setTopLeft(double topLeft) {
        this.topLeft = topLeft;
    }

    /**
     Gets the top-right value of the curve

     @return the top-right value of the curve
     */
    public double getTopRight() {
        return topRight;
    }

    /**
     Sets the top-right value of the curve

     @param topRight is the top-right value of the curve
     */
    public void setTopRight(double topRight) {
        this.topRight = topRight;
    }

    /**
     Gets the top-right value of the curve

     @return the top-right value of the curve
     */
    public double getBottomRight() {
        return bottomRight;
    }

    /**
     Sets the bottom-right value of the curve

     @param bottomRight is the bottom-right value of the curve
     */
    public void setBottomRight(double bottomRight) {
        this.bottomRight = bottomRight;
    }

    @Override
    public PiShape clone() throws CloneNotSupportedException {
        return (PiShape) super.clone();
    }

}
