/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.term.Activated;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Term;

public class WeightedSum extends WeightedDefuzzifier {

    public WeightedSum() {
        super(Type.Automatic);
    }

    public WeightedSum(Type type) {
        super(type);
    }

    public WeightedSum(String type) {
        super(type);
    }

    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        Aggregated fuzzyOutput = (Aggregated) term;
        if (fuzzyOutput.getTerms().isEmpty()) {
            return Double.NaN;
        }
        minimum = fuzzyOutput.getMinimum();
        maximum = fuzzyOutput.getMaximum();

        Type type = getType();
        if (type == Type.Automatic) {
            type = inferType(fuzzyOutput.getTerms().get(0));
        }

        double sum = 0.0;
        if (type == Type.TakagiSugeno) {
            double w, z;
            for (Activated activated : fuzzyOutput.getTerms()) {
                w = activated.getDegree();
                z = activated.getTerm().membership(w);
                sum += w * z;
            }
        } else {
            double w, z;
            for (Activated activated : fuzzyOutput.getTerms()) {
                w = activated.getDegree();
                z = tsukamoto(activated.getTerm(), w, minimum, maximum);
                sum += w * z;
            }
        }
        return sum;
    }

    @Override
    public WeightedSum clone() throws CloneNotSupportedException {
        return (WeightedSum) super.clone();
    }

}
