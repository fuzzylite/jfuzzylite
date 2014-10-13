/*
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
 */
package com.fuzzylite.rule;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.factory.HedgeFactory;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.term.Activated;
import com.fuzzylite.variable.OutputVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class Consequent {

    protected List<Proposition> conclusions;

    public Consequent() {
        this.conclusions = new ArrayList<Proposition>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Proposition> it = this.conclusions.iterator();
                it.hasNext();) {
            sb.append(it.next().toString());
            if (it.hasNext()) {
                sb.append(String.format(" %s ", Rule.FL_AND));
            }
        }
        return sb.toString();
    }

    public void modify(double activationDegree, TNorm activation) {
        for (Proposition proposition : conclusions) {
            if (proposition.variable.isEnabled()) {
                if (!proposition.hedges.isEmpty()) {
                    for (int i = proposition.hedges.size() - 1; i >= 0; --i) {
                        Hedge hedge = proposition.hedges.get(i);
                        activationDegree = hedge.hedge(activationDegree);
                    }
                }

                Activated term = new Activated();
                term.setTerm(proposition.getTerm());
                term.setDegree(activationDegree);
                term.setActivation(activation);
                OutputVariable outputVariable = (OutputVariable) proposition.getVariable();
                outputVariable.fuzzyOutput().getTerms().add(term);
                FuzzyLite.logger().fine(String.format("Accumulating %s", term.toString()));
            }
        }
    }

    public void load(String consequent, Engine engine) {
        /*
         Extracts the list of propositions from the consequent
         The rules are:
         1) After a variable comes 'is' or '=',
         2) After 'is' comes a hedge or a term
         3) After a hedge comes a hedge or a term
         4) After a term comes operators 'and' or 'with'
         5) After operator 'and' comes a variable
         6) After operator 'with' comes a float
         */

        final byte S_VARIABLE = 1, S_IS = 2, S_HEDGE = 4, S_TERM = 8, S_AND = 16;
        byte state = S_VARIABLE;

        this.conclusions.clear();

        Proposition proposition = null;

        StringTokenizer tokenizer = new StringTokenizer(consequent);
        String token = "";
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            if ((state & S_VARIABLE) > 0) {
                proposition = new Proposition();
                proposition.setVariable(engine.getOutputVariable(token));
                this.conclusions.add(proposition);
                state = S_IS;
                continue;
            }

            if ((state & S_IS) > 0) {
                if (Rule.FL_IS.equals(token) || Rule.FL_EQUALS.equals(token)) {
                    state = S_HEDGE | S_TERM;
                    continue;
                }
            }

            if ((state & S_HEDGE) > 0) {
                Hedge hedge = null;
                if (engine.hasHedge(token)) {
                    hedge = engine.getHedge(token);
                } else {
                    HedgeFactory hedgeFactory = FactoryManager.instance().hedge();
                    if (hedgeFactory.isRegistered(token)) {
                        hedge = hedgeFactory.createInstance(token);
                        engine.addHedge(hedge);
                    }
                }
                if (hedge != null) {
                    proposition.hedges.add(hedge);
                    state = S_HEDGE | S_TERM;
                    continue;
                }
            }

            if ((state & S_TERM) > 0) {
                if (proposition.variable.hasTerm(token)) {
                    proposition.term = proposition.variable.getTerm(token);
                    state = S_AND;
                    continue;
                }
            }

            if ((state & S_AND) > 0) {
                if (Rule.FL_AND.equals(token)) {
                    state = S_VARIABLE;
                    continue;
                }
            }

            //if reached this point, there was an error:
            if ((state & S_VARIABLE) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected output variable, but found <%s>",
                        token));
            }
            if ((state & S_IS) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected keyword <%s> or <%s>, but found <%s>",
                        Rule.FL_IS, Rule.FL_EQUALS, token));
            }
            if ((state & S_HEDGE) > 0 || (state & S_TERM) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected hedge or term, but found <%s>",
                        token));
            }
            if ((state & S_AND) > 0) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected operator <%s>, but found <%s>",
                        Rule.FL_AND, token));
            }
            throw new RuntimeException(String.format(
                    "[syntax error] unexpected token <%s>", token));
        }
    }

    public List<Proposition> getConclusions() {
        return conclusions;
    }

    public void setConclusions(List<Proposition> conclusions) {
        this.conclusions = conclusions;
    }

}
