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
package com.fuzzylite.defuzzifier;

import com.fuzzylite.term.Accumulated;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.Thresholded;

/**
 *
 * @author jcrada
 */
public class WeightedAverage extends Defuzzifier {

    @Override
    public double defuzzify(Term term, double minimum, double maximum) {
        Accumulated takagiSugeno = (Accumulated) term;
        double sum = 0.0;
        double weights = 0.0;
        for (Term t : takagiSugeno.getTerms()) {
            Thresholded thresholded = (Thresholded) t;

            double w = thresholded.getThreshold();
            double z = Tsukamoto.tsukamoto(thresholded,
                    takagiSugeno.getMinimum(), takagiSugeno.getMaximum());
            //Traditionally, activation is the AlgebraicProduct
            sum += thresholded.getActivation().compute(w, z);
            weights += w;
        }
        return sum / weights;
    }
}
