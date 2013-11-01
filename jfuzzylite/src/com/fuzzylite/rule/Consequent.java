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
package com.fuzzylite.rule;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.term.Thresholded;
import com.fuzzylite.variable.OutputVariable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class Consequent {

    protected List<Proposition> conclusions;

    public Consequent() {
        this.conclusions = new ArrayList<>();
    }

    public void load(String consequent, Engine engine) {
        //TODO: implement
    }

    public void modify(double activationDegree, TNorm activation) {
        for (Proposition proposition : conclusions) {
            double threshold = activationDegree;
            for (Hedge hedge : proposition.getHedges()) {
                threshold = hedge.hedge(threshold);
            }

            Thresholded term = new Thresholded();
            term.setTerm(proposition.getTerm());
            term.setThreshold(threshold);
            term.setActivation(activation);
            OutputVariable outputVariable = (OutputVariable) proposition.getVariable();
            outputVariable.getOutput().getTerms().add(term);
            FuzzyLite.logger().info(String.format("Accumulating %s", term.toString()));
        }
    }

    public List<Proposition> getConclusions() {
        return conclusions;
    }

    public void setConclusions(List<Proposition> conclusions) {
        this.conclusions = conclusions;
    }

}
