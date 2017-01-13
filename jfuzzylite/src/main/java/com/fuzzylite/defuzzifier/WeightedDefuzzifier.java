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
package com.fuzzylite.defuzzifier;

import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;

/**
 The WeightedDefuzzifier class is the base class for defuzzifiers which compute
 a weighted function on the fuzzy set without requiring to integrate over the
 fuzzy set.

 @author Juan Rada-Vilela, Ph.D.
 @since 5.0
 */
public abstract class WeightedDefuzzifier extends Defuzzifier {

    /**
     The Type enum indicates the type of the WeightedDefuzzifier based the terms
     included in the fuzzy set.
     */
    public enum Type {
        /**
         Automatic: Automatically inferred from the terms
         */
        Automatic,
        /**
         TakagiSugeno: Manually set to TakagiSugeno (or Inverse Tsukamoto)
         */
        TakagiSugeno,
        /**
         Tsukamoto: Manually set to Tsukamoto
         */
        Tsukamoto
    }

    private Type type;

    public WeightedDefuzzifier() {
        this(Type.Automatic);
    }

    public WeightedDefuzzifier(String type) {
        this(Type.valueOf(type));
    }

    public WeightedDefuzzifier(Type type) {
        this.type = type;
    }

    /**
     Gets the type of the weighted defuzzifier

     @return the type of the weighted defuzzifier
     */
    public Type getType() {
        return type;
    }

    /**
     Sets the type of the weighted defuzzifier

     @param type is the type of the weighted defuzzifier
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     Infers the type of the defuzzifier based on the given term. If the given
     term is Constant, Linear or Function, then the type is TakagiSugeno;
     otherwise, the type is Tsukamoto

     @param term is the given term
     @return the inferred type of the defuzzifier based on the given term
     */
    public Type inferType(Term term) {
        if (term instanceof Constant || term instanceof Linear || term instanceof Function) {
            return Type.TakagiSugeno;
        }
        return Type.Tsukamoto;
    }

    @Override
    public WeightedDefuzzifier clone() throws CloneNotSupportedException {
        return (WeightedDefuzzifier) super.clone();
    }
}
