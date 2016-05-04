/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */

package com.fuzzylite.norm.t;

import com.fuzzylite.Op;
import com.fuzzylite.norm.TNorm;

public class DrasticProduct extends TNorm {

    @Override
    public double compute(double a, double b) {
        if (Op.isEq(Math.max(a, b), 1.0)) {
            return Math.min(a, b);
        }
        return 0.0;
    }

    @Override
    public DrasticProduct clone() throws CloneNotSupportedException {
        return (DrasticProduct) super.clone();
    }

}
