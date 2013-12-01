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

import com.fuzzylite.norm.TNorm;
import static com.fuzzylite.Op.str;

/**
 *
 * @author jcrada
 */
public class Thresholded extends Term {

    protected Term term;
    protected double threshold;
    protected TNorm activation;

    public Thresholded() {
        this(null, 1.0, null);
    }

    public Thresholded(Term term, double threshold, TNorm activation) {
        this.term = term;
        this.threshold = threshold;
        this.activation = activation;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return this.activation.compute(this.term.membership(x), this.threshold);
    }

    @Override
    public String toString() {
        String result = Thresholded.class.getSimpleName();
        result += String.format("(%s) thresholded to %s using <%s> activation",
                term.toString(), str(threshold), activation.getClass().getSimpleName());
        return result;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public TNorm getActivation() {
        return activation;
    }

    public void setActivation(TNorm activation) {
        this.activation = activation;
    }

    @Override
    public void configure(double[] parameters) {
        //do nothing
    }

}
