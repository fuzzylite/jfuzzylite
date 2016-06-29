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

import com.fuzzylite.Op;
import com.fuzzylite.norm.SNorm;

public class DrasticSum extends SNorm {

    @Override
    public double compute(double a, double b) {
        if (Op.isEq(Op.min(a, b), 0.0)) {
            return Op.max(a, b);
        }
        return 1.0;
    }

    @Override
    public DrasticSum clone() throws CloneNotSupportedException {
        return (DrasticSum) super.clone();
    }

}
