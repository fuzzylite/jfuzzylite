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

package com.fuzzylite.defuzzifier;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.term.Term;

public class Bisector extends IntegralDefuzzifier {

    public Bisector() {
        super();
    }

    public Bisector(int resolution) {
        super(resolution);
    }

    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        if (!Op.isFinite(minimum + maximum)) {
            return Double.NaN;
        }
        if (maximum - minimum > getResolution()) {
            FuzzyLite.logger().warning(String.format(
                    "[accuracy warning] resolution (%d)"
                    + "is smaller than the range (%s, %s). "
                    + "Improve the accuracy by increasing the resolution to a value "
                    + "greater or equal to the range.",
                    getResolution(), str(minimum), str(maximum)));
        }
        double dx = (maximum - minimum) / getResolution();

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
        double bisector = (leftArea * xRight + rightArea * xLeft) / (leftArea + rightArea);
        return bisector;
    }

    @Override
    public Bisector clone() throws CloneNotSupportedException {
        return (Bisector) super.clone();
    }
}
