/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.term.Activated;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Term;

/**
 The WeightedSum class is a WeightedDefuzzifier that computes the weighted sum
 of a fuzzy set represented in an Aggregated Term.

 @author Juan Rada-Vilela, Ph.D.
 @see WeightedSumCustom
 @see WeightedAverage
 @see WeightedAverageCustom
 @see WeightedDefuzzifier
 @see Defuzzifier
 @since 4.0
 */
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

    /**
     Computes the weighted sum of the given fuzzy set represented as an
     Aggregated Term as @f$y = \sum_i{w_iz_i} @f$, where @f$w_i@f$ is the
     activation degree of term @f$i@f$, and @f$z_i = \mu_i(w_i) @f$.

     From version 6.0, the implication and aggregation operators are not
     utilized for defuzzification. Also, for better performance, the term is
     assumed to be Aggregated without type checking, for which unexpected
     operation may occur if the term is not an instance of Aggregated.

     @param term is the fuzzy set represented as an AggregatedTerm
     @param minimum is the minimum value of the range (only used for Tsukamoto)
     @param maximum is the maximum value of the range (only used for Tsukamoto)
     @return the weighted sum of the given fuzzy set
     */
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
                z = activated.getTerm().tsukamoto(w, minimum, maximum);
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
