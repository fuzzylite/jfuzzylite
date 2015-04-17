/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.term.Accumulated;
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
        Accumulated fuzzyOutput = (Accumulated) term;

        minimum = fuzzyOutput.getMinimum();
        maximum = fuzzyOutput.getMaximum();

        double sum = 0.0;
        double weights = 0.0;

        if (fuzzyOutput.getAccumulation() == null) {
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
                    accumulatedDegree = fuzzyOutput.getAccumulation().compute(
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
