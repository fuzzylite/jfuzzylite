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
 The MeanOfMaximum class is an IntegralDefuzzifier that computes the mean value
 of the maximum membership function of a fuzzy set represented in a Term.

 @author Juan Rada-Vilela, Ph.D.
 @see SmallestOfMaximum
 @see MeanOfMaximum
 @see IntegralDefuzzifier
 @see Defuzzifier
 @since 4.0
 */
public class MeanOfMaximum extends IntegralDefuzzifier {

    public MeanOfMaximum() {
        super();
    }

    public MeanOfMaximum(int resolution) {
        super(resolution);
    }

    /**
     Computes the mean value of the maximum membership function of a fuzzy set.
     The mean value is computed while integrating over the fuzzy set. The
     integration algorithm is the midpoint rectangle method
     (https://en.wikipedia.org/wiki/Rectangle_method).

     @param term is the fuzzy set
     @param minimum is the minimum value of the fuzzy set
     @param maximum is the maximum value of the fuzzy set
     @return the mean `x`-coordinate of the maximum membership function
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
        double ymax = -1.0;
        double xsmallest = minimum;
        double xlargest = maximum;
        boolean samePlateau = false;
        for (int i = 0; i < resolution; ++i) {
            x = minimum + (i + 0.5) * dx;
            y = term.membership(x);

            if (Op.isGt(y, ymax)) {
                ymax = y;

                xsmallest = x;
                xlargest = x;

                samePlateau = true;
            } else if (samePlateau && Op.isEq(y, ymax)) {
                xlargest = x;
            } else if (Op.isLt(y, ymax)) {
                samePlateau = false;
            }
        }

        return (xlargest + xsmallest) / 2.0;
    }

    @Override
    public MeanOfMaximum clone() throws CloneNotSupportedException {
        return (MeanOfMaximum) super.clone();
    }

}
