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

package com.fuzzylite.hedge;

import com.fuzzylite.Op;

public class Seldom extends Hedge {

    @Override
    public double hedge(double x) {
        return Op.isLE(x, 0.5)
                ? Math.sqrt(x / 2.0)
                : 1.0 - Math.sqrt((1.0 - x) / 2.0);
    }

    @Override
    public Seldom clone() throws CloneNotSupportedException {
        return (Seldom) super.clone();
    }
}
