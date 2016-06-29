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
package com.fuzzylite.defuzzifier;

import com.fuzzylite.Op;
import com.fuzzylite.term.Term;

public class SmallestOfMaximum extends IntegralDefuzzifier {

    public SmallestOfMaximum() {
        super();
    }

    public SmallestOfMaximum(int resolution) {
        super(resolution);
    }

    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        if (!Op.isFinite(minimum + maximum)) {
            return Double.NaN;
        }

        final int resolution = getResolution();
        final double dx = (maximum - minimum) / resolution;
        double x, y;
        double ymax = -1.0, xsmallest = minimum;
        for (int i = 0; i < resolution; ++i) {
            x = minimum + (i + 0.5) * dx;
            y = term.membership(x);

            if (Op.isGt(y, ymax)) {
                xsmallest = x;
                ymax = y;
            }
        }
        return xsmallest;
    }

    @Override
    public SmallestOfMaximum clone() throws CloneNotSupportedException {
        return (SmallestOfMaximum) super.clone();
    }

}
