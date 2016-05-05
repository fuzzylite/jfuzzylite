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

import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Activated;
import com.fuzzylite.term.Term;

public class WeightedSumCustom extends WeightedDefuzzifier {

    public WeightedSumCustom() {
        super(Type.Automatic);
    }

    public WeightedSumCustom(Type type) {
        super(type);
    }

    public WeightedSumCustom(String type) {
        super(type);
    }

    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        Aggregated fuzzyOutput = (Aggregated) term;

        minimum = fuzzyOutput.getMinimum();
        maximum = fuzzyOutput.getMaximum();

        double sum = 0.0;

        SNorm aggregation = fuzzyOutput.getAggregation();
        WeightedDefuzzifier.Type type = getType();
        for (Activated activated : fuzzyOutput.getTerms()) {
            double w = activated.getDegree();
            TNorm implication = activated.getImplication();
            if (type == WeightedDefuzzifier.Type.Automatic) {
                type = inferType(activated.getTerm());
            }

            double z = (type == WeightedDefuzzifier.Type.TakagiSugeno)
                    ? activated.getTerm().membership(w)
                    : tsukamoto(activated.getTerm(), w, minimum, maximum);

            double wz = implication != null
                    ? implication.compute(w, z)
                    : w * z;
            sum = aggregation != null
                    ? aggregation.compute(sum, wz)
                    : sum + wz;
        }
        return sum;
    }

    @Override
    public WeightedSumCustom clone() throws CloneNotSupportedException {
        return (WeightedSumCustom) super.clone();
    }

}
