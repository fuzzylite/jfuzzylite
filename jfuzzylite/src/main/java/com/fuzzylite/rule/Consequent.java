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
import java.util.ListIterator;
import java.util.StringTokenizer;

public class Consequent {

    private String text;
    private List<Proposition> conclusions;

    public Consequent() {
        this.text = "";
        this.conclusions = new ArrayList<Proposition>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Proposition> getConclusions() {
        return conclusions;
    }

    public void setConclusions(List<Proposition> conclusions) {
        this.conclusions = conclusions;
    }

    public void modify(double activationDegree, TNorm activation) {
        if (!isLoaded()) {
            throw new RuntimeException(String.format(
                    "[consequent error] consequent <%s> is not loaded", text));
        }
        for (Proposition proposition : conclusions) {
            if (proposition.getVariable().isEnabled()) {
                if (!proposition.getHedges().isEmpty()) {
                    int lastIndex = proposition.getHedges().size();
                    ListIterator<Hedge> rit = proposition.getHedges().listIterator(lastIndex);
                    while (rit.hasPrevious()) {
                        activationDegree = rit.previous().hedge(activationDegree);
                    }
                }
                Activated term = new Activated(proposition.getTerm(), activationDegree, activation);
                OutputVariable outputVariable = (OutputVariable) proposition.getVariable();
                outputVariable.fuzzyOutput().getTerms().add(term);
                FuzzyLite.logger().fine(String.format("Accumulating %s", term.toString()));
            }
        }
    }

    public boolean isLoaded() {
        return !this.conclusions.isEmpty();
    }

    public void unload() {
        this.conclusions.clear();
    }

    public void load(Rule rule, Engine engine) {
        load(text, rule, engine);
    }

    public void load(String consequent, Rule rule, Engine engine) {
        unload();
        this.text = consequent;
        if (consequent.trim().isEmpty()) {
            throw new RuntimeException("[syntax error] consequent is empty");
        }

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
        final byte S_VARIABLE = 1, S_IS = 2, S_HEDGE = 4, S_TERM = 8, S_AND = 16, S_WITH = 32;
        byte state = S_VARIABLE;

        Proposition proposition = null;

        StringTokenizer tokenizer = new StringTokenizer(consequent);
        String token = "";
        try {
            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();

                if ((state & S_VARIABLE) > 0) {
                    if (engine.hasOutputVariable(token)) {
                        proposition = new Proposition();
                        proposition.setVariable(engine.getOutputVariable(token));
                        this.conclusions.add(proposition);
                        state = S_IS;
                        continue;
                    }
                }

                if ((state & S_IS) > 0) {
                    if (Rule.FL_IS.equals(token)) {
                        state = S_HEDGE | S_TERM;
                        continue;
                    }
                }

                if ((state & S_HEDGE) > 0) {
                    Hedge hedge = null;
                    if (rule.getHedges().containsKey(token)) {
                        hedge = rule.getHedges().get(token);
                    } else {
                        HedgeFactory hedgeFactory = FactoryManager.instance().hedge();
                        if (hedgeFactory.hasConstructor(token)) {
                            hedge = hedgeFactory.constructObject(token);
                            rule.getHedges().put(token, hedge);
                        }
                    }
                    if (hedge != null) {
                        proposition.getHedges().add(hedge);
                        state = S_HEDGE | S_TERM;
                        continue;
                    }
                }

                if ((state & S_TERM) > 0) {
                    if (proposition.getVariable().hasTerm(token)) {
                        proposition.setTerm(proposition.getVariable().getTerm(token));
                        state = S_AND | S_WITH;
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
                            "[syntax error] consequent expected output variable, but found <%s>",
                            token));
                }
                if ((state & S_IS) > 0) {
                    throw new RuntimeException(String.format(
                            "[syntax error] consequent expected keyword <%s>, but found <%s>",
                            Rule.FL_IS, token));
                }
                if ((state & S_HEDGE) > 0 || (state & S_TERM) > 0) {
                    throw new RuntimeException(String.format(
                            "[syntax error] consequent expected hedge or term, but found <%s>",
                            token));
                }
                if ((state & S_AND) > 0 || ((state & S_WITH) > 0)) {
                    throw new RuntimeException(String.format(
                            "[syntax error] consequent expected operator <%s> or keyword <%s>, sbut found <%s>",
                            Rule.FL_AND, Rule.FL_WITH, token));
                }
                throw new RuntimeException(String.format(
                        "[syntax error] unexpected token <%s>", token));
            }

            if (!((state & S_AND) > 0 || ((state & S_WITH) > 0))) { //only acceptable final state
                if ((state & S_VARIABLE) > 0) {
                    throw new RuntimeException(String.format(
                            "[syntax error] consequent expected output variable after <%s>", token));
                }
                if ((state & S_IS) > 0) {
                    throw new RuntimeException(String.format(
                            "[syntax error] consequent expected keyword <%s> after <%s>", Rule.FL_IS, token));
                }

                if ((state & S_HEDGE) > 0 || (state & S_TERM) > 0) {
                    throw new RuntimeException(String.format(
                            "[syntax error] consequent expected hedge or term after <%s>", token));
                }
            }
        } catch (RuntimeException ex) {
            unload();
            throw ex;
        }
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

}
