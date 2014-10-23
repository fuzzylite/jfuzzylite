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
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.Activated;
import com.fuzzylite.term.ZShape;

public class Tsukamoto {

    public static double tsukamoto(Activated term, double minimum, double maximum) {
        Term monotonic = term.getTerm();
        double w = term.getDegree();
        double z = Double.NaN;

        if (monotonic instanceof Ramp) {
            Ramp ramp = (Ramp) monotonic;
            z = Op.scale(w, 0, 1, ramp.getStart(), ramp.getEnd());

        } else if (monotonic instanceof Sigmoid) {
            Sigmoid sigmoid = (Sigmoid) monotonic;
            if (Op.isEq(w, 1.0)) {
                if (Op.isGE(sigmoid.getSlope(), 0.0)) {
                    z = maximum;
                } else {
                    z = minimum;
                }

            } else if (Op.isEq(w, 0.0)) {
                if (Op.isGE(sigmoid.getSlope(), 0.0)) {
                    z = minimum;
                } else {
                    z = maximum;
                }

            } else {
                double a = sigmoid.getSlope();
                double b = sigmoid.getInflection();
                z = b + (Math.log(1.0 / w - 1.0) / -a);
            }

        } else if (monotonic instanceof SShape) {
            SShape sshape = (SShape) monotonic;
            double difference = sshape.getEnd() - sshape.getStart();
            double a = sshape.getStart() + Math.sqrt(w * difference * difference / 2.0);
            double b = sshape.getEnd() + Math.sqrt(difference * difference * (w - 1.0) / -2.0);
            if (Math.abs(w - monotonic.membership(a))
                    < Math.abs(w - monotonic.membership(b))) {
                z = a;
            } else {
                z = b;
            }

        } else if (monotonic instanceof ZShape) {
            ZShape zshape = (ZShape) monotonic;
            double difference = zshape.getEnd() - zshape.getStart();
            double a = zshape.getStart() + Math.sqrt(difference * difference * (w - 1) / -2.0);
            double b = zshape.getEnd() + Math.sqrt(w * difference * difference / 2.0);
            if (Math.abs(w - monotonic.membership(a))
                    < Math.abs(w - monotonic.membership(b))) {
                z = a;
            } else {
                z = b;
            }
        }

        if (!Double.isNaN(z)) {
            double fz = monotonic.membership(z);
            //Compare difference between estimated and true value
            if (!Op.isEq(w, fz, 0.1)) {
                FuzzyLite.logger().warning(String.format(
                        "[tsukamoto warning] difference <%s> might suggest an inaccurate "
                        + "computation of z because it is expected w=f(z) in %s term <%s>, "
                        + "but w=%s f(z)=%s and z=%s", Op.str(Math.abs(w - fz)),
                        monotonic.getClass().getSimpleName(), monotonic.getName(),
                        Op.str(w), Op.str(fz), Op.str(z)));
            }
        } else {
            // else if it is not a Tsukamoto controller, then fallback to the inverse Tsukamoto
            z = monotonic.membership(term.getDegree());
        }

        return z;
    }

}
