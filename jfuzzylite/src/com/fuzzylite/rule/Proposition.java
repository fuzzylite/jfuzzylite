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

import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.Variable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class Proposition extends Expression {

    protected Variable variable;
    protected List<Hedge> hedges;
    protected Term term;

    public Proposition() {
        this(null, null);
    }

    public Proposition(Variable variable, Term term) {
        this(variable, new ArrayList<Hedge>(), term);
    }

    public Proposition(Variable variable, List<Hedge> hedges, Term term) {
        this.variable = variable;
        this.hedges = hedges;
        this.term = term;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public List<Hedge> getHedges() {
        return hedges;
    }

    public void setHedges(List<Hedge> hedges) {
        this.hedges = hedges;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Override
    public String toString() {
        String result = variable.getName() + " " + Rule.FL_IS + " ";
        for (Hedge hedge : hedges) {
            result += hedge.getName() + " ";
        }
        if (term != null) { //term is null when hedge is any
            result += term.getName();
        }
        return result;
    }

}
