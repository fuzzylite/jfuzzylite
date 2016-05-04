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

import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Activated;
import com.fuzzylite.term.Term;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        if (fuzzyOutput.getAggregation() == null) {
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
        } else {
            Map<Term, List<Activated>> groups = new HashMap<Term, List<Activated>>();
            for (Activated value : fuzzyOutput.getTerms()) {
                Term key = value.getTerm();
                if (groups.containsKey(key)) {
                    groups.get(key).add(value);
                } else {
                    List<Activated> list = new ArrayList<Activated>();
                    list.add(value);
                    groups.put(key, list);
                }
            }

            Type type = getType();
            Iterator<Term> it = groups.keySet().iterator();
            while (it.hasNext()) {
                Term activatedTerm = it.next();
                double accumulatedDegree = 0.0;
                for (Activated t : groups.get(activatedTerm)) {
                    accumulatedDegree = fuzzyOutput.getAggregation().compute(
                            accumulatedDegree, t.getDegree());
                }

                if (type == Type.Automatic) {
                    type = inferType(activatedTerm);
                }

                double z = (type == Type.TakagiSugeno)
                        ? activatedTerm.membership(accumulatedDegree)
                        : tsukamoto(activatedTerm, accumulatedDegree, minimum, maximum);

                sum += accumulatedDegree * z;
                weights += accumulatedDegree;
            }
        }

        return sum / weights;
    }

    @Override
    public WeightedAverage clone() throws CloneNotSupportedException {
        return (WeightedAverage) super.clone();
    }

}
