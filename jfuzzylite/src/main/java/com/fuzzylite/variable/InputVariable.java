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
package com.fuzzylite.variable;

import com.fuzzylite.imex.FllExporter;

public class InputVariable extends Variable {

    public InputVariable() {
        this("");
    }

    public InputVariable(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public InputVariable(String name, double minimum, double maximum) {
        super(name, minimum, maximum);
    }

    public String fuzzyInputValue() {
        return fuzzify(this.getValue());
    }

    @Override
    public Type type() {
        return Type.InputVariable;
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    @Override
    public InputVariable clone() throws CloneNotSupportedException {
        return (InputVariable) super.clone();
    }

}
