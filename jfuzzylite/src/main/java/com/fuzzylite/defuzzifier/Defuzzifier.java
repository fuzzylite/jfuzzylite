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

import com.fuzzylite.Op;
import com.fuzzylite.term.Term;

/**
 The Defuzzifier class is the abstract class for defuzzifiers.

 @author Juan Rada-Vilela, Ph.D.
 @see IntegralDefuzzifier
 @see WeightedDefuzzifier
 @since 4.0
 */
public abstract class Defuzzifier implements Op.Cloneable {

    /**
     Defuzzifies the given fuzzy term utilizing the range `[minimum,maximum]`

     @param term is the term to defuzzify, typically an Aggregated term
     @param minimum is the minimum value of the range
     @param maximum is the maximum value of the range
     @return the defuzzified value of the given fuzzy term
     */
    public abstract double defuzzify(Term term, double minimum, double maximum);

    /**
     Creates a clone of the defuzzifier

     @return a clone of the defuzzifier
     */
    @Override
    public Defuzzifier clone() throws CloneNotSupportedException {
        return (Defuzzifier) super.clone();
    }
}
