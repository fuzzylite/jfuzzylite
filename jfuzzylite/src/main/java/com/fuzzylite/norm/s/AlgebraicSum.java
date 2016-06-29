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
package com.fuzzylite.norm.s;

import com.fuzzylite.norm.SNorm;

public class AlgebraicSum extends SNorm {

    @Override
    public double compute(double a, double b) {
        return a + b - (a * b);
    }

    @Override
    public AlgebraicSum clone() throws CloneNotSupportedException {
        return (AlgebraicSum) super.clone();
    }

}
