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
import com.fuzzylite.term.Concave;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.ZShape;

/**
 *
 * @author jcrada
 */
public abstract class WeightedDefuzzifier extends Defuzzifier {

    public enum Type {

        Automatic, TakagiSugeno, Tsukamoto
    }

    private Type type;

    public WeightedDefuzzifier() {
        this(Type.Automatic);
    }

    public WeightedDefuzzifier(String type) {
        this(Type.valueOf(type));
    }

    public WeightedDefuzzifier(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type inferType(Term term) {
        if (term instanceof Constant || term instanceof Linear || term instanceof Function) {
            return Type.TakagiSugeno;
        }
        return Type.Tsukamoto;
    }

    public boolean isMonotonic(Term term) {
        return term instanceof Concave
                || term instanceof Ramp
                || term instanceof Sigmoid
                || term instanceof SShape
                || term instanceof ZShape;
    }

    public double tsukamoto(Term monotonic, double activationDegree,
            double minimum, double maximum) {
        double w = activationDegree;
        double z = Double.NaN;

        boolean isTsukamoto = true;

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

        } else if (monotonic instanceof Concave) {
            Concave concave = (Concave) monotonic;
            double i = concave.getInflection();
            double e = concave.getEnd();
            z = (i - e) / concave.membership(w) + 2 * e - i;

        } else {
            isTsukamoto = false;
        }

        if (isTsukamoto) {
            double fz = monotonic.membership(z);
            //Compare difference between estimated and true value
            if (!Op.isEq(w, fz, 1e-2)) {
                FuzzyLite.logger().fine(String.format(
                        "[tsukamoto warning] difference <%s> might suggest an inaccurate "
                        + "computation of z because it is expected w=f(z) in %s term <%s>, "
                        + "but w=%s f(z)=%s and z=%s", Op.str(Math.abs(w - fz)),
                        monotonic.getClass().getSimpleName(), monotonic.getName(),
                        Op.str(w), Op.str(fz), Op.str(z)));
            }
        } else {
            // else fallback to the regular Takagi-Sugeno or inverse Tsukamoto (according to term)
            z = monotonic.membership(w);
        }
        return z;
    }

    @Override
    public WeightedDefuzzifier clone() throws CloneNotSupportedException {
        return (WeightedDefuzzifier) super.clone();
    }
}
