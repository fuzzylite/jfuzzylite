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
import java.util.logging.Level;

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
                FuzzyLite.logger().log(Level.FINE, "Aggregating {0}", term.toString());
            }
        }
    }

    public boolean isLoaded() {
        return !getConclusions().isEmpty();
    }

    public void unload() {
        getConclusions().clear();
    }

    public void load(Engine engine) {
        load(getText(), engine);
    }

    public void load(String consequent, Engine engine) {
        unload();
        setText(consequent);
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
                        getConclusions().add(proposition);
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
                    HedgeFactory hedgeFactory = FactoryManager.instance().hedge();
                    if (hedgeFactory.hasConstructor(token)) {
                        Hedge hedge = hedgeFactory.constructObject(token);
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
