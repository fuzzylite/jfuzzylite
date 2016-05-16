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
package com.fuzzylite.rule;

import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.Variable;
import java.util.ArrayList;
import java.util.List;

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
    public Type type() {
        return Type.Proposition;
    }

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
