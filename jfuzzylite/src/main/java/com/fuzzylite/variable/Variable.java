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
package com.fuzzylite.variable;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Variable implements Op.Cloneable {

    public enum Type{
        None, InputVariable, OutputVariable
    }
    private String name;
    private List<Term> terms;
    private double value;
    private double minimum, maximum;
    private boolean enabled;
    private boolean lockValueInRange;

    public Variable(String name) {
        this(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public Variable(String name, double minimum, double maximum) {
        this.name = name;
        this.terms = new ArrayList<Term>();
        this.value = Double.NaN;
        this.minimum = minimum;
        this.maximum = maximum;
        this.enabled = true;
        this.lockValueInRange = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = lockValueInRange
                ? Op.bound(value, minimum, maximum)
                : value;
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

    public boolean isLockValueInRange() {
        return lockValueInRange;
    }

    public void setLockValueInRange(boolean lockValueInRange) {
        this.lockValueInRange = lockValueInRange;
    }

    public Type type(){
        return Type.None;
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    public String fuzzify(double x) {
        StringBuilder sb = new StringBuilder();
        Iterator<Term> it = getTerms().iterator();

        while (it.hasNext()) {
            Term term = it.next();
            double fx = term.membership(x);

            if (sb.length() == 0) {
                sb.append(Op.str(fx));
            } else if (Double.isNaN(fx) || Op.isGE(fx, 0.0)) {
                sb.append(" + ").append(Op.str(fx));
            } else {
                sb.append(" - ").append(Op.str(fx));
            }
            sb.append("/").append(term.getName());
        }
        return sb.toString();
    }

    public Op.Pair<Double, Term> highestMembership(double x) {
        Op.Pair<Double, Term> result = new Op.Pair<Double, Term>(0.0, null);
        for (Term term : terms) {
            double y = term.membership(x);
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

    static class TermCentroidComparatorAscending
            implements Comparator<Op.Pair<Term, Double>> {

        @Override
        public int compare(Op.Pair<Term, Double> a, Op.Pair<Term, Double> b) {
            double result = Math.signum(a.getSecond() - b.getSecond());
            return Double.isNaN(result) ? -1 : (int) result;
        }
    }

    public void sort() {
        PriorityQueue<Op.Pair<Term, Double>> termCentroids
                = new PriorityQueue<Op.Pair<Term, Double>>(
                        terms.size(), new TermCentroidComparatorAscending());
        Defuzzifier defuzzifier = new Centroid();
        for (Term term : terms) {
            double centroid;
            try {
                if (term instanceof Constant || term instanceof Linear) {
                    centroid = term.membership(0);
                } else {
                    centroid = defuzzifier.defuzzify(term, getMinimum(), getMaximum());
                }
            } catch (Exception ex) {
                centroid = Double.POSITIVE_INFINITY;
            }
            termCentroids.offer(new Op.Pair<Term, Double>(term, centroid));
        }

        List<Term> sortedTerms = new ArrayList<Term>(terms.size());
        while (termCentroids.size() > 0) {
            sortedTerms.add(termCentroids.poll().getFirst());
        }
        setTerms(sortedTerms);
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
