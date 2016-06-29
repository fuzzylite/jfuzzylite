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

public class Binary extends Term {

    private double start, direction;

    public Binary() {
        this("");
    }

    public Binary(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Binary(String name, double start, double direction) {
        this(name, start, direction, 1.0);
    }

    public Binary(String name, double start, double direction, double height) {
        super(name, height);
        this.start = start;
        this.direction = direction;
    }

    @Override
    public String parameters() {
        return Op.join(" ", start, direction)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

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
        setDirection(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (direction == Double.POSITIVE_INFINITY
                && Op.isGE(x, start)) {
            return height * 1.0;
        }
        if (direction == Double.NEGATIVE_INFINITY
                && Op.isLE(x, start)) {
            return height * 1.0;
        }
        return height * 0.0;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    @Override
    public Binary clone() throws CloneNotSupportedException {
        return (Binary) super.clone();
    }

}
