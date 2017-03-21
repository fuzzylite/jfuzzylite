/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.Op;
import com.fuzzylite.term.Term;

/**
 The Centroid class is an IntegralDefuzzifier that computes the centroid of a
 fuzzy set represented in a Term.

 @author Juan Rada-Vilela, Ph.D.
 @see Bisector
 @see IntegralDefuzzifier
 @see Defuzzifier
 @since 4.0
 */
public class Centroid extends IntegralDefuzzifier {

    public Centroid() {
        super();
    }

    public Centroid(int resolution) {
        super(resolution);
    }

    /**
     Computes the centroid of a fuzzy set. The defuzzification process
     integrates over the fuzzy set utilizing the boundaries given as parameters.
     The integration algorithm is the midpoint rectangle method
     (https://en.wikipedia.org/wiki/Rectangle_method).

     @param term is the fuzzy set
     @param minimum is the minimum value of the fuzzy set
     @param maximum is the maximum value of the fuzzy set
     @return the `x`-coordinate of the centroid of the fuzzy set
     */
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

        //double ycentroid = 0;
        for (int i = 0; i < resolution; ++i) {
            x = minimum + (i + 0.5) * dx;
            y = term.membership(x);

            xcentroid += y * x;
            //ycentroid += y * y;
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
