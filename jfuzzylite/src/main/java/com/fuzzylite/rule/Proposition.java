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
package com.fuzzylite.rule;

import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 The Proposition class is an Expression that represents a terminal node in the
 expression tree as `variable is [hedge]* term`.

 @author Juan Rada-Vilela, Ph.D.
 @see Antecedent
 @see Consequent
 @see Rule
 @since 4.0
 */
public class Proposition extends Expression {

    private Variable variable;
    private List<Hedge> hedges;
    private Term term;

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

    /**
     Gets the variable in `variable is [hedge]* term`

     @return the variable in `variable is [hedge]* term`
     */
    public Variable getVariable() {
        return variable;
    }

    /**
     Sets the variable in `variable is [hedge]* term`

     @param variable is the variable in `variable is [hedge]* term`
     */
    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    /**
     Gets the Hedge%s in `variable is [hedge]* term`

     @return the Hedge%s in `variable is [hedge]* term`
     */
    public List<Hedge> getHedges() {
        return hedges;
    }

    /**
     Sets the Hedge%s in `variable is [hedge]* term`

     @param hedges is the Hedge%s in `variable is [hedge]* term`
     */
    public void setHedges(List<Hedge> hedges) {
        this.hedges = hedges;
    }

    /**
     Gets the Term in `variable is [hedge]* term`

     @return the Term in `variable is [hedge]* term`
     */
    public Term getTerm() {
        return term;
    }

    /**
     Sets the Term in `variable is [hedge]* term`

     @param term is the Term in `variable is [hedge]* term`
     */
    public void setTerm(Term term) {
        this.term = term;
    }

    @Override
    public Type type() {
        return Type.Proposition;
    }

    /**
     Returns a string representation of the proposition

     @return a string representation of the proposition
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append(variable.getName()).append(" ").append(Rule.FL_IS).append(" ");
        for (Hedge hedge : hedges) {
            result.append(hedge.getName()).append(" ");
        }
        if (term != null) { //term is null when hedge is any
            result.append(term.getName());
        }
        return result.toString();
    }

}
