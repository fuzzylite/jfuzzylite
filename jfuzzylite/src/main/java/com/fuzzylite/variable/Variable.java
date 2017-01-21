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

/**
 The Variable class is the base class for linguistic variables.

 @author Juan Rada-Vilela, Ph.D.
 @see InputVariable
 @see OutputVariable
 @see Term
 @since 4.0
 */
public class Variable implements Op.Cloneable {

    /**
     Indicates the type of the variable to avoid `instanceof`
     */
    public enum Type {
        None, Input, Output
    }
    private String name;
    private String description;
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
        this.description = "";
        this.terms = new ArrayList<Term>();
        this.value = Double.NaN;
        this.minimum = minimum;
        this.maximum = maximum;
        this.enabled = true;
        this.lockValueInRange = false;
    }

    /**
     Gets the name of the variable

     @return the name of the variable
     */
    public String getName() {
        return name;
    }

    /**
     Sets the name of the variable

     @param name is the name of the variable
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Gets the description of the variable

     @return the description of the variable
     */
    public String getDescription() {
        return description;
    }

    /**
     Sets the description of the variable

     @param description is the description of the variable
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     Gets the value of the variable

     @return the input value of an InputVariable, or the output value of an
     OutputVariable
     */
    public double getValue() {
        return value;
    }

    /**
     Sets the value of the variable

     @param value is the input value of an InputVariable, or the output value of
     an OutputVariable
     */
    public void setValue(double value) {
        this.value = lockValueInRange
                ? Op.bound(value, minimum, maximum)
                : value;
    }

    /**
     Sets the range of the variable between `[minimum, maximum]`

     @param minimum is the minimum value in range
     @param maximum is the maximum value in range
     */
    public void setRange(double minimum, double maximum) {
        setMinimum(minimum);
        setMaximum(maximum);
    }

    /**
     Gets the magnitude of the range of the variable

     @return `maximum - minimum`
     */
    public double range() {
        return this.maximum - this.minimum;
    }

    /**
     Gets the minimum value of the range of the variable

     @return the minimum value of the range of the variable
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     Sets the minimum value of the range of the variable

     @param minimum is the minimum value of the range
     */
    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    /**
     Gets the maximum value of the range of the variable

     @return the maximum value of the range of the variable
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     Sets the maximum value of the range of the variable

     @param maximum is the maximum value of the range
     */
    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    /**
     Sets whether the variable is enabled

     @param enabled determines whether to enable the variable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     Gets whether the variable is enabled

     @return whether the variable is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     Gets whether the variable locks the current value to the range of the
     variable

     If enabled in an InputVariable @f$i@f$, the input value @f$x_i@f$ will be
     used when computing the Antecedent::activationDegree() as long as @f$x_i
     \in [\mbox{min}, \mbox{max}]@f$. Else, for the case of

     @f$x_i \not\in [\mbox{min}, \mbox{max}]@f$, the range values will be used
     instead but without changing the input value @f$x_i@f$.

     If enabled in an OutputVariable @f$j@f$, the output value @f$y_j@f$ will be
     overriden by the range values when @f$y_j \not\in [\mbox{min},
     \mbox{max}]@f$. See OutputVariable for more information.

     @return whether the variable locks the current value to the range of the
     variable
     */
    public boolean isLockValueInRange() {
        return lockValueInRange;
    }

    /**
     Sets whether the variable locks the current value to the range of the
     variable.

     If enabled in an InputVariable @f$i@f$, the input value @f$x_i@f$ will be
     used when computing the Antecedent::activationDegree() as long as @f$x_i
     \in [\mbox{min}, \mbox{max}]@f$. Else, for the case of

     @f$x_i \not\in [\mbox{min}, \mbox{max}]@f$, the range values will be used
     instead but without changing the input value @f$x_i@f$.

     If enabled in an OutputVariable @f$j@f$, the output value @f$y_j@f$ will be
     overriden by the range values when @f$y_j \not\in [\mbox{min},
     \mbox{max}]@f$. See OutputVariable for more information.

     @param lockValueInRange indicates whether to lock the value to the range of
     the variable
     */
    public void setLockValueInRange(boolean lockValueInRange) {
        this.lockValueInRange = lockValueInRange;
    }

    /**
     Returns the type of the variable

     @return the type of the variable
     */
    public Type type() {
        return Type.None;
    }

    /**
     Gets a string representation of the variable in the FuzzyLite Language

     @return a string representation of the variable in the FuzzyLite Language
     @see FllExporter
     */
    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    /**
     Evaluates the membership function of value @f$x@f$ for each term @f$i@f$,
     resulting in a fuzzy value in the form

     @f$\tilde{x}=\sum_i{\mu_i(x)/i}@f$
     @param x is the value to fuzzify
     @return the fuzzy value expressed as @f$\sum_i{\mu_i(x)/i}@f$
     */
    public String fuzzify(double x) {
        StringBuilder sb = new StringBuilder();

        for (Term term : getTerms()) {
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

    /**
     Gets the term which has the highest membership function value for

     @f$x@f$.

     @param x is the value of interest
     @return a pair containing the highest membership function value and the
     term @f$i@f$ that maximizes @f$\mu_i(x)@f$.
     */
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

    /**
     Gets the highest membership function value for @f$x@f$.

     @param x is the value of interest
     @return the highest membership function value
     */
    public Double highestMembershipValue(double x) {
        return highestMembership(x).getFirst();
    }

    /**
     Gets the term which has the highest membership function value for

     @f$x@f$.

     @param x is the value of interest
     @return the term @f$i@f$ that maximizes @f$\mu_i(x)@f$.
     */
    public Term highestMembershipTerm(double x) {
        return highestMembership(x).getSecond();
    }

    /**
     Ascendantly is a comparator to ascendantly sort pairs of terms by centroid
     */
    public static class Ascendantly implements Comparator<Op.Pair<Term, Double>> {

        @Override
        public int compare(Op.Pair<Term, Double> a, Op.Pair<Term, Double> b) {
            double result = Math.signum(a.getSecond() - b.getSecond());
            return Double.isNaN(result) ? -1 : (int) result;
        }
    }

    /**
     Sorts the terms in ascending order according to their centroids
     */
    public void sort() {
        PriorityQueue<Op.Pair<Term, Double>> termCentroids
                = new PriorityQueue<Op.Pair<Term, Double>>(
                        terms.size(), new Ascendantly());
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
        while (!termCentroids.isEmpty()) {
            sortedTerms.add(termCentroids.poll().getFirst());
        }
        setTerms(sortedTerms);
    }

    /**
     Adds a term to the variable

     @param term is the term to add
     */
    public void addTerm(Term term) {
        this.terms.add(term);
    }

    /**
     Inserts the term in the variable

     @param term is the term to insert
     @param index is the index where the term will be inserted
     */
    public void insert(Term term, int index) {
        this.terms.add(index, term);
    }

    /**
     Gets the term at the given index

     @param index is the position of the term in the vector
     @return the term at the given index
     */
    public Term getTerm(int index) {
        return this.terms.get(index);
    }

    /**
     Gets the term of the given name.

     @param name is the name of the term to retrieve
     @return the term of the given name
     */
    public Term getTerm(String name) {
        for (Term term : this.terms) {
            if (name.equals(term.getName())) {
                return term;
            }
        }
        return null;
    }

    /**
     Gets whether a term of the given name has been added

     @param name the name of the term
     @return whether the term of the given name is found
     */
    public boolean hasTerm(String name) {
        return this.getTerm(name) != null;
    }

    /**
     Removes the term

     @param term is the term to remove
     @return whether the term existed in the list
     */
    public boolean removeTerm(Term term) {
        return this.terms.remove(term);
    }

    /**
     Removes the term

     @param name is the name of the term to remove
     @return the removed term or null if not found
     */
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

    /**
     Gets the number of terms added to the variable

     @return the number of terms in the variable
     */
    public int numberOfTerms() {
        return this.terms.size();
    }

    /**
     Gets the list of terms

     @return the list of terms
     */
    public List<Term> getTerms() {
        return this.terms;
    }

    /**
     Sets the terms of the variable

     @param terms is a vector of terms
     */
    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    /**
     Creates a clone of the variable

     @return a clone of the variable
     @throws CloneNotSupportedException
     */
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
