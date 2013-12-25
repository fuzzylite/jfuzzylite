/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.Thresholded;
import com.fuzzylite.term.ZShape;

/**
 *
 * @author jcrada
 */
public class Tsukamoto {

    public static double tsukamoto(Thresholded term, double minimum, double maximum) {
        Term monotonic = term.getTerm();
        double w = term.getThreshold();
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
            if (Math.abs(w - monotonic.membership(a)) <
                    Math.abs(w - monotonic.membership(b))) {
                z = a;
            } else {
                z = b;
            }

        } else if (monotonic instanceof ZShape) {
            ZShape zshape = (ZShape) monotonic;
            double difference = zshape.getEnd() - zshape.getStart();
            double a = zshape.getStart() + Math.sqrt(difference * difference * (w - 1) / -2.0);
            double b = zshape.getEnd() + Math.sqrt(w * difference * difference / 2.0);
            if (Math.abs(w - monotonic.membership(a)) < 
                    Math.abs(w - monotonic.membership(b))){
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
                        + "but w=%s f(z)=%s and z=%s", Op.str(Math.abs(w-fz)),
                        monotonic.getClass().getSimpleName(), monotonic.getName(),
                        Op.str(w), Op.str(fz), Op.str(z)));
            }
        } else {
            // else if it is not a Tsukamoto controller, then fallback to the inverse Tsukamoto
            z = monotonic.membership(term.getThreshold());
        }

        return z;
    }

}
