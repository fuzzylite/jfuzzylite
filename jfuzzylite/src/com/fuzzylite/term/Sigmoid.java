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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class Sigmoid extends Term {

    protected double inflection, slope;

    public Sigmoid() {
        this("");
    }

    public Sigmoid(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Sigmoid(String name, double inflection, double slope) {
        this.name = name;
        this.inflection = inflection;
        this.slope = slope;
    }

    @Override
    public String parameters() {
        return Op.join(" ", inflection, slope);
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 2;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setInflection(Op.toDouble(values.get(0)));
        setSlope(Op.toDouble(values.get(1)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return 1.0 / (1.0 + Math.exp(-slope * (x - inflection)));
    }

    public double getInflection() {
        return inflection;
    }

    public void setInflection(double inflection) {
        this.inflection = inflection;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

}
