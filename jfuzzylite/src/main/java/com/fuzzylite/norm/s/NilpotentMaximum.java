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

package com.fuzzylite.norm.s;

import com.fuzzylite.Op;
import com.fuzzylite.norm.SNorm;

/**
 *
 * @author jcrada
 */
public class NilpotentMaximum extends SNorm {

    @Override
    public double compute(double a, double b) {
        if (Op.isLt(a + b, 1.0)) {
            return Math.max(a, b);
        }
        return 1.0;
    }

    @Override
    public NilpotentMaximum clone() throws CloneNotSupportedException {
        return (NilpotentMaximum) super.clone();
    }

}
