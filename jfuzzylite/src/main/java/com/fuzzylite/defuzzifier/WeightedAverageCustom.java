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

import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.term.Activated;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Term;

/**
 The WeightedAverageCustom class is a WeightedDefuzzifier that computes the
 weighted average of a fuzzy set represented in an Aggregated Term utilizing the
 fuzzy operators for implication and aggregation to compute the weighted
 average.

 @author Juan Rada-Vilela, Ph.D.
 @see WeightedAverage
 @see WeightedSum
 @see WeightedSumCustom
 @see WeightedDefuzzifier
 @see Defuzzifier
 @since 6.0
 */
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

    /**
     Computes the weighted average of the given fuzzy set represented as an
     AggregatedTerm as @f$y = \dfrac{\sum_i w_iz_i}{\sum_i w_i} @f$, where
     @f$w_i@f$ is the activation degree of term @f$i@f$, and

     @f$z_i = \mu_i(w_i) @f$.

     If the implication and aggregation operators are set to fl::null (or set to
     AlgebraicProduct and UnboundedSum, respectively), then the operation of
     WeightedAverageCustom is the same as the WeightedAverage. Otherwise, the
     implication and aggregation operators are utilized to compute the
     multiplications and sums in @f$y@f$, respectively.

     @param term is the fuzzy set represented as an Aggregated Term
     @param minimum is the minimum value of the range (only used for Tsukamoto)
     @param maximum is the maximum value of the range (only used for Tsukamoto)
     @return the weighted average of the given fuzzy set
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
                z = activated.getTerm().tsukamoto(w, minimum, maximum);
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
