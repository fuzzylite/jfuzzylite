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
package com.fuzzylite.variable;

import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Term;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jcrada
 */
public class Variable {

    protected String name;
    protected double minimum, maximum;
    protected List<Term> terms;
    protected boolean enabled;

    public Variable(String name) {
        this(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public Variable(String name, double minimum, double maximum) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.terms = new ArrayList<>();
        this.enabled = true;
    }

    public String fuzzify(double x) {
        String result = "";
        for (int i = 0; i < terms.size(); ++i) {
            Term term = terms.get(i);
            double fx = term.membership(x);
            if (i == 0) {
                result += str(fx);
            } else {
                if (Double.isNaN(fx) || Op.isGE(fx, 0.0)) {
                    result += " + " + str(fx);
                } else {
                    result += " - " + str(fx);
                }
            }
            result += "/" + term.getName();
        }
        return result;
    }

    @Override
    public String toString() {
        return new FllExporter("", "; ").toString(this);
    }

    public void sort(final Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            Collections.sort(terms, new Comparator<Term>() {
                @Override
                public int compare(Term o1, Term o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        } else {
            final Map<Term, Double> map = new HashMap<>();
            for (Term term : terms) {
                map.put(term, defuzzifier.defuzzify(term, minimum, maximum));
            }

            Collections.sort(terms, new Comparator<Term>() {
                @Override
                public int compare(Term o1, Term o2) {
                    return map.get(o1).compareTo(map.get(o2));
                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRange(double minimum, double maximum) {
        setMinimum(minimum);
        setMaximum(maximum);
    }

    public double range() {
        return this.maximum - this.minimum;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    /*
     * Terms
     */
    public Term getTerm(String name) {
        for (Term term : this.terms) {
            if (name.equals(term.getName())) {
                return term;
            }
        }
        return null;
    }

    public Term getTerm(int index) {
        return this.terms.get(index);
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    public Term removeTerm(Term term) {
        return this.terms.remove(term) ? term : null;
    }

    public Term removeTerm(String name) {
        for (Iterator<Term> it = this.terms.iterator(); it.hasNext();) {
            Term term = it.next();
            if (name.equals(term.getName())) {
                it.remove();
                return term;
            }
        }
        return null;
    }

    public boolean hasTerm(String name) {
        return this.getTerm(name) != null;
    }

    public int numberOfTerms() {
        return this.terms.size();
    }

    public List<Term> getTerms() {
        return this.terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

}
