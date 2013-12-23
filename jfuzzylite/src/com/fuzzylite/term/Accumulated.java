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
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class Accumulated extends Term {

    protected List<Term> terms;
    protected double minimum;
    protected double maximum;
    protected SNorm accumulation;

    public Accumulated() {
        this("");
    }

    public Accumulated(String name) {
        this(name, Double.NaN, Double.NaN, null);
        this.terms = new ArrayList<>();
    }

    public Accumulated(String name, double minimum, double maximum) {
        this(name, minimum, maximum, null);
        this.terms = new ArrayList<>();
    }

    public Accumulated(String name, double minimum, double maximum, SNorm accumulation) {
        this.terms = new ArrayList<>();
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.accumulation = accumulation;
    }

    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter("", "; ");
        StringBuilder result = new StringBuilder();
        result.append(String.format("%s %s %s",
                Op.str(minimum), Op.str(maximum),
                exporter.toString(accumulation)));
        for (Term term : terms){
            result.append(" ").append(exporter.toString(term));
        }
        return result.toString();
    }

    @Override
    public void configure(String parameters) {
        //do nothing
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double mu = 0.0;
        for (Term term : this.terms) {
            mu = this.accumulation.compute(mu, term.membership(x));
        }
        return mu;
    }

    public void clear() {
        this.terms.clear();
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
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

    public SNorm getAccumulation() {
        return accumulation;
    }

    public void setAccumulation(SNorm accumulation) {
        this.accumulation = accumulation;
    }

}
