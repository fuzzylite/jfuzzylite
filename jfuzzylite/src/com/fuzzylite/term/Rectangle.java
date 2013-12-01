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
public class Rectangle extends Term {

    protected double minimum, maximum;

    public Rectangle(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Rectangle(String name, double minimum, double maximum) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Op.isLt(x, minimum) || Op.isGt(x, maximum)) {
            return 0.0;
        }
        return 1.0;
    }

    @Override
    public String toString() {
        String result = Rectangle.class.getSimpleName();
        result += "(" + Op.join(", ", str(minimum), str(maximum)) + ")";
        return result;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    @Override
    public void configure(double[] parameters) {
        int required = 2;
        if (parameters.length < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setMinimum(parameters[0]);
        setMaximum(parameters[1]);
    }
    
    
}
