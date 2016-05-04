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

        minimum = fuzzyOutput.getMinimum();
        maximum = fuzzyOutput.getMaximum();

        double sum = 0.0;

        if (fuzzyOutput.getAggregation() == null) {
            WeightedDefuzzifier.Type type = getType();
            for (Activated activated : fuzzyOutput.getTerms()) {
                double w = activated.getDegree();
                if (type == WeightedDefuzzifier.Type.Automatic) {
                    type = inferType(activated.getTerm());
                }

                double z = (type == WeightedDefuzzifier.Type.TakagiSugeno)
                        ? activated.getTerm().membership(w)
                        : tsukamoto(activated.getTerm(), w, minimum, maximum);
                sum += w * z;
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

            WeightedDefuzzifier.Type type = getType();
            Iterator<Term> it = groups.keySet().iterator();
            while (it.hasNext()) {
                Term activatedTerm = it.next();
                double accumulatedDegree = 0.0;
                for (Activated t : groups.get(activatedTerm)) {
                    accumulatedDegree = fuzzyOutput.getAggregation().compute(
                            accumulatedDegree, t.getDegree());
                }

                if (type == WeightedDefuzzifier.Type.Automatic) {
                    type = inferType(activatedTerm);
                }

                double z = (type == WeightedDefuzzifier.Type.TakagiSugeno)
                        ? activatedTerm.membership(accumulatedDegree)
                        : tsukamoto(activatedTerm, accumulatedDegree, minimum, maximum);

                sum += accumulatedDegree * z;
            }
        }

        return sum;
    }

    @Override
    public WeightedSum clone() throws CloneNotSupportedException {
        return (WeightedSum) super.clone();
    }

}
