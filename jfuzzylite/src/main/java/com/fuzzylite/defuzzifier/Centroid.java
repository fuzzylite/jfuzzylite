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

public class Centroid extends IntegralDefuzzifier {

    public Centroid() {
        super();
    }

    public Centroid(int resolution) {
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
        double area = 0;
        double xcentroid = 0;
//        @SuppressWarnings("unused")
//        double ycentroid = 0;
        for (int i = 0; i < resolution; ++i) {
            x = minimum + (i + 0.5) * dx;
            y = term.membership(x);

            xcentroid += y * x;
//            ycentroid += y * y;
            area += y;
        }
        //Final results not computed for efficiency
        //xcentroid /= area;
        //ycentroid /= 2 * area;
        //area *= dx;
        return xcentroid / area;
    }

    @Override
    public Centroid clone() throws CloneNotSupportedException {
        return (Centroid) super.clone();
    }
}
