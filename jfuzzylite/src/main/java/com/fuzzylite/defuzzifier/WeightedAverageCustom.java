/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.term.Activated;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Term;

public class WeightedAverageCustom extends WeightedDefuzzifier {

    public WeightedAverageCustom() {
        super(Type.Automatic);
    }

    public WeightedAverageCustom(Type type) {
        super(type);
    }

    public WeightedAverageCustom(String type) {
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

        SNorm aggregation = fuzzyOutput.getAggregation();
        TNorm implication = null;

        double sum = 0.0;
        double weights = 0.0;
        if (type == Type.TakagiSugeno) {
            double w, z, wz;
            for (Activated activated : fuzzyOutput.getTerms()) {
                w = activated.getDegree();
                z = activated.getTerm().membership(w);
                implication = activated.getImplication();
                wz = implication != null
                        ? implication.compute(w, z)
                        : w * z;
                if (aggregation != null) {
                    sum = aggregation.compute(sum, wz);
                    weights = aggregation.compute(weights, w);
                } else {
                    sum += wz;
                    weights += w;
                }
            }
        } else {
            double w, z;
            for (Activated activated : fuzzyOutput.getTerms()) {
                w = activated.getDegree();
                z = tsukamoto(activated.getTerm(), w, minimum, maximum);
                sum += w * z;
                weights += w;
            }
        }
        return sum / weights;
    }

    @Override
    public WeightedAverageCustom clone() throws CloneNotSupportedException {
        return (WeightedAverageCustom) super.clone();
    }

}
