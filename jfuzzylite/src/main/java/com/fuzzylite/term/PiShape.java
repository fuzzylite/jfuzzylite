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

    @Override
    public String parameters() {
        return Op.join(" ", bottomLeft, topLeft, topRight, bottomRight)
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
        setBottomLeft(Op.toDouble(it.next()));
        setTopLeft(Op.toDouble(it.next()));
        setTopRight(Op.toDouble(it.next()));
        setBottomRight(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double sshape;
        if (Op.isLE(x, bottomLeft))
            sshape = 0.0;
        else if (Op.isLE(x, 0.5 * (bottomLeft + topLeft)))
            sshape = 2.0 * Math.pow((x - bottomLeft) / (topLeft - bottomLeft), 2);
        else if (Op.isLt(x, topLeft))
            sshape = 1.0 - 2.0 * Math.pow((x - topLeft) / (topLeft - bottomLeft), 2);
        else sshape = 1.0;

        double zshape;
        if (Op.isLE(x, topRight))
            zshape = 1.0;
        else if (Op.isLE(x, 0.5 * (topRight + bottomRight)))
            zshape = 1.0 - 2.0 * Math.pow((x - topRight) / (bottomRight - topRight), 2);
        else if (Op.isLt(x, bottomRight))
            zshape = 2.0 * Math.pow((x - bottomRight) / (bottomRight - topRight), 2);
        else zshape = 0.0;

        return height * sshape * zshape;
    }

    public double getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(double bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public double getTopLeft() {
        return topLeft;
    }

    public void setTopLeft(double topLeft) {
        this.topLeft = topLeft;
    }

    public double getTopRight() {
        return topRight;
    }

    public void setTopRight(double topRight) {
        this.topRight = topRight;
    }

    public double getBottomRight() {
        return bottomRight;
    }

    public void setBottomRight(double bottomRight) {
        this.bottomRight = bottomRight;
    }

    @Override
    public PiShape clone() throws CloneNotSupportedException {
        return (PiShape) super.clone();
    }

}
