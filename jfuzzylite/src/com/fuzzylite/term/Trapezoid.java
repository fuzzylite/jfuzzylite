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
public class Trapezoid extends Term {

    protected double a, b, c, d;

    public Trapezoid() {
        this("");
    }

    public Trapezoid(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public Trapezoid(String name, double a, double b, double c, double d) {
        this.name = name;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public String parameters() {
        return Op.join(" ", a, b, c, d);
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 4;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setA(Op.toDouble(values.get(0)));
        setB(Op.toDouble(values.get(1)));
        setC(Op.toDouble(values.get(2)));
        setD(Op.toDouble(values.get(3)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLE(x, a) || Op.isGE(x, d)) {
            return 0.0;
        } else if (Op.isLt(x, b)) {
            return Math.min(1.0, (x - a) / (b - a));
        } else if (Op.isLE(x, c)) {
            return 1.0;
        } else if (Op.isLt(x, d)) {
            return (d - x) / (d - c);
        }
        return 0.0;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }
}
