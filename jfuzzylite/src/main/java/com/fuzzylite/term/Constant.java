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

public class Constant extends Term {

    private double value;

    public Constant() {
        this("");
    }

    public Constant(String name) {
        this(name, Double.NaN);
    }

    public Constant(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String parameters() {
        return Op.str(value);
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        setValue(Op.toDouble(parameters));
    }

    @Override
    public double membership(double x) {
        return this.value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public Constant clone() throws CloneNotSupportedException {
        return (Constant) super.clone();
    }

}
