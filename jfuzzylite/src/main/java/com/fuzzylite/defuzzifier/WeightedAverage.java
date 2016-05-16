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

public class WeightedAverage extends WeightedDefuzzifier {

    public WeightedAverage() {
        super(Type.Automatic);
    }

    public WeightedAverage(Type type) {
        super(type);
    }

    public WeightedAverage(String type) {
        super(type);
    }

    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        Aggregated fuzzyOutput = (Aggregated) term;

        minimum = fuzzyOutput.getMinimum();
        maximum = fuzzyOutput.getMaximum();

        double sum = 0.0;
        double weights = 0.0;

        Type type = getType();
        for (Activated activated : fuzzyOutput.getTerms()) {
            double w = activated.getDegree();
            if (type == Type.Automatic) {
                type = inferType(activated.getTerm());
            }

            double z = (type == Type.TakagiSugeno)
                    ? activated.getTerm().membership(w)
                    : tsukamoto(activated.getTerm(), w, minimum, maximum);
            sum += w * z;
            weights += w;
        }
        return sum / weights;
    }

    @Override
    public WeightedAverage clone() throws CloneNotSupportedException {
        return (WeightedAverage) super.clone();
    }

}
