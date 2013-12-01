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
public class Gaussian extends Term {

    protected double mean, standardDeviation;

    public Gaussian(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Gaussian(String name, double mean, double standardDeviation) {
        this.name = name;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return Math.exp((-(x - mean) * (x - mean))
                / (2 * standardDeviation * standardDeviation));
    }

    @Override
    public String toString() {
        String result = Gaussian.class.getSimpleName();
        result += "(" + Op.join(", ", str(mean), str(standardDeviation)) + ")";
        return result;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    @Override
    public void configure(double[] parameters) {
        int required = 2;
        if (parameters.length < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setMean(parameters[0]);
        setStandardDeviation(parameters[1]);
    }
}
