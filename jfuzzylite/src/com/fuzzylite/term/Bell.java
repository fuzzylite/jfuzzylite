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
public class Bell extends Term {

    protected double center, width, slope;

    public Bell() {
        this("");
    }

    public Bell(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN);
    }

    public Bell(String name, double center, double width, double slope) {
        super.name = name;
        this.center = center;
        this.width = width;
        this.slope = slope;
    }

    @Override
    public String parameters() {
        return Op.join(" ", center, width, slope);
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 3;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setCenter(Op.toDouble(values.get(0)));
        setWidth(Op.toDouble(values.get(1)));
        setSlope(Op.toDouble(values.get(2)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        //from octave: gbellmf.m
        return 1.0 / (1.0 + Math.pow(Math.abs((x - center) / width), 2 * slope));
    }

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

}
