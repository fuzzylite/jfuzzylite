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
import static com.fuzzylite.Op.str;

/**
 *
 * @author jcrada
 */
public class Trapezoid extends Term {

    protected double a, b, c, d;

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
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isLt(x, a) || Op.isGt(x, d)) {
            return 0.0;
        } else if (Op.isLE(x, b)) {
            return Math.min(1.0, (x - a) / (b - a));
        } else if (Op.isLE(x, c)) {
            return 1.0;
        } else if (Op.isLE(x, d)) {
            return (d - x) / (d - c);
        }
        return 0.0;
    }

    @Override
    public String toString() {
        String result = Trapezoid.class.getSimpleName();
        result += "(" + Op.join(", ", str(a), str(b), str(c), str(d)) + ")";
        return result;
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

    @Override
    public void configure(double[] parameters) {
        int required = 4;
        if (parameters.length < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setA(parameters[0]);
        setB(parameters[1]);
        setC(parameters[2]);
        setD(parameters[3]);
    }
}
