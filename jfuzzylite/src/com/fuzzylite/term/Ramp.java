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
public class Ramp extends Term {

    protected double start, end;

    public Ramp() {
        this("");
    }

    public Ramp(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Ramp(String name, double start, double end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public String parameters() {
        return Op.join(" ", start, end);
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
        setStart(Op.toDouble(values.get(0)));
        setEnd(Op.toDouble(values.get(1)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isEq(start, end)) {
            return 0.0;
        }

        if (Op.isLt(start, end)) {
            if (Op.isLE(x, start)) {
                return 0.0;
            }
            if (Op.isGE(x, end)) {
                return 1.0;
            }
            return (x - start) / (end - start);
        } else {
            if (Op.isGE(x, start)) {
                return 0.0;
            }
            if (Op.isLE(x, end)) {
                return 1.0;
            }
            return (start - x) / (start - end);
        }
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

}
