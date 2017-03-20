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
 The Bisector class is an IntegralDefuzzifier that computes the bisector of a
 fuzzy set represented in a Term.

 @author Juan Rada-Vilela, Ph.D.
 @see Centroid
 @see IntegralDefuzzifier
 @see Defuzzifier
 @since 4.0
 */
public class Bisector extends IntegralDefuzzifier {

    public Bisector() {
        super();
    }

    public Bisector(int resolution) {
        super(resolution);
    }

    /**
     Computes the bisector of a fuzzy set. The defuzzification process
     integrates over the fuzzy set utilizing the boundaries given as parameters.
     The integration algorithm is the midpoint rectangle method
     (https://en.wikipedia.org/wiki/Rectangle_method).

     @param term is the fuzzy set
     @param minimum is the minimum value of the fuzzy set
     @param maximum is the maximum value of the fuzzy set
     @return the @f$x@f$-coordinate of the bisector of the fuzzy set
     */
    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        if (!Op.isFinite(minimum + maximum)) {
            return Double.NaN;
        }
        final double dx = (maximum - minimum) / getResolution();
        int counter = getResolution();
        int left = 0, right = 0;
        double leftArea = 0, rightArea = 0;
        double xLeft = minimum, xRight = maximum;
        while (counter-- > 0) {
            if (Op.isLE(leftArea, rightArea)) {
                xLeft = minimum + (left + 0.5) * dx;
                leftArea += term.membership(xLeft);
                left++;
            } else {
                xRight = maximum - (right + 0.5) * dx;
                rightArea += term.membership(xRight);
                right++;
            }
        }
        //Inverse weighted average to compensate
        return (leftArea * xRight + rightArea * xLeft) / (leftArea + rightArea);
    }

    @Override
    public Bisector clone() throws CloneNotSupportedException {
        return (Bisector) super.clone();
    }
}
