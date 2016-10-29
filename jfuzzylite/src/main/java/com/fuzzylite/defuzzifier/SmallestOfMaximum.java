/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
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
 The SmallestOfMaximum class is an IntegralDefuzzifier that computes the
 smallest value of the maximum membership function of a fuzzy set represented in
 a Term.

 @author Juan Rada-Vilela, Ph.D.
 @see LargestOfMaximum
 @see MeanOfMaximum
 @see IntegralDefuzzifier
 @see Defuzzifier
 @since 4.0
 */
public class SmallestOfMaximum extends IntegralDefuzzifier {

    public SmallestOfMaximum() {
        super();
    }

    public SmallestOfMaximum(int resolution) {
        super(resolution);
    }

    /**
     Computes the smallest value of the maximum membership function in the fuzzy
     set. The smallest value is computed while integrating over the fuzzy set.
     The integration algorithm is the midpoint rectangle method
     (https://en.wikipedia.org/wiki/Rectangle_method).

     @param term is the fuzzy set
     @param minimum is the minimum value of the fuzzy set
     @param maximum is the maximum value of the fuzzy set
     @return the smallest @f$x@f$-coordinate of the maximum membership function
     value in the fuzzy set
     */
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
