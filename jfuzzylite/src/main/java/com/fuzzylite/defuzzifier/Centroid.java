/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;

import static com.fuzzylite.Op.str;

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
        if (maximum - minimum > getResolution()) {
            FuzzyLite.logger().warning(String.format(
                    "[accuracy warning] resolution (%d)"
                    + "is smaller than the range (%s, %s). "
                    + "Improve the accuracy by increasing the resolution to a value "
                    + "greater or equal to the range.",
                    getResolution(), str(minimum), str(maximum)));
        }

        double dx = (maximum - minimum) / getResolution();
        double x, y;
		double area = 0;
        double xcentroid = 0;
        @SuppressWarnings("unused")
        double ycentroid = 0;
        for (int i = 0; i < getResolution(); ++i) {
            x = minimum + (i + 0.5) * dx;
            y = term.membership(x);

            xcentroid += y * x;
            ycentroid += y * y;
            area += y;
        }
        xcentroid /= area;
        ycentroid /= 2 * area;
        area *= dx; //total area... unused, but for future reference.
        return xcentroid;
    }

    @Override
    public Centroid clone() throws CloneNotSupportedException {
        return (Centroid) super.clone();
    }
}
