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
