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
package com.fuzzylite.variable;

import com.fuzzylite.Op;

import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Variable implements Op.Cloneable {

    private String name;
    private double minimum, maximum;
    private List<Term> terms;
    private boolean enabled;

    public Variable(String name) {
        this(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public Variable(String name, double minimum, double maximum) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.terms = new ArrayList<Term>();
        this.enabled = true;
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

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    public String fuzzify(double x) {
        StringBuilder sb = new StringBuilder();
        Iterator<Term> it = getTerms().iterator();
        if (it.hasNext()) {
            Term term = it.next();
            double degree = term.membership(x);
            sb.append(Op.str(degree)).append("/").append(term.getName());
            while (it.hasNext()) {
                term = it.next();
                degree = term.membership(x);
                if (Double.isNaN(degree) || Op.isGE(degree, 0.0)) {
                    sb.append(" + ").append(Op.str(degree));
                } else {
                    sb.append(" - ").append(Op.str(Math.abs(degree)));
                }
                sb.append("/").append(term.getName());
            }
        }
        return sb.toString();
    }

    public Op.Pair<Double, Term> highestMembership(double x) {
        Op.Pair<Double, Term> result = new Op.Pair<Double, Term>(0.0, null);
        for (Term term : terms) {
            double y = Double.NaN;
            try {
                y = term.membership(x);
            } catch (Exception ex) {
                // ignore
            }
            if (Op.isGt(y, result.getFirst())) {
                result.setFirst(y);
                result.setSecond(term);
            }
        }
        return result;
    }

    public Double highestMembershipValue(double x) {
        return highestMembership(x).getFirst();
    }

    public Term highestMembershipTerm(double x) {
        return highestMembership(x).getSecond();
    }

    public void sort(Defuzzifier defuzzifier) {
        final Map<Term, Double> map = new HashMap<Term, Double>();
        for (Term term : terms) {
            try {
                if (term instanceof Constant || term instanceof Linear) {
                    map.put(term, term.membership(0));
                } else {
                    map.put(term, defuzzifier.defuzzify(term, minimum, maximum));
                }
            } catch (Exception ex) {
                map.put(term, Double.POSITIVE_INFINITY);
            }
        }

        Collections.sort(terms, new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return map.get(o1).compareTo(map.get(o2));
            }
        });
    }

    /*
     * Terms
     */
    public void addTerm(Term term) {
        this.terms.add(term);
    }

    public void insert(Term term, int index) {
        this.terms.add(index, term);
    }

    public Term getTerm(int index) {
        return this.terms.get(index);
    }

    public Term getTerm(String name) {
        for (Term term : this.terms) {
            if (name.equals(term.getName())) {
                return term;
            }
        }
        return null;
    }

    public boolean hasTerm(String name) {
        return this.getTerm(name) != null;
    }

    public boolean removeTerm(Term term) {
        return this.terms.remove(term);
    }

    public Term removeTerm(String name) {
        Iterator<Term> it = this.terms.iterator();
        while (it.hasNext()) {
            Term term = it.next();
            if (term.getName().equals(name)) {
                it.remove();
                return term;
            }
        }
        return null;
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

    @Override
    public Variable clone() throws CloneNotSupportedException {
        Variable result = (Variable) super.clone();
        result.terms = new ArrayList<Term>(this.terms.size());
        for (Term term : this.terms) {
            result.terms.add(term.clone());
        }
        return result;
    }

}
