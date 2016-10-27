/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.hedge;

import com.fuzzylite.Op;

import java.util.Locale;

/**
 The Hedge class is the abstract class for hedges. Hedges are utilized within
 the Antecedent and Consequent of a Rule in order to modify the membership
 function of a linguistic Term.

 @author Juan Rada-Vilela, Ph.D.
 @see Antecedent
 @see Consequent
 @see Rule
 @see HedgeFactory
 @since 4.0
 */
public abstract class Hedge implements Op.Cloneable {

    /**
     Computes the hedge for the membership function value @f$x@f$

     @param x is a membership function value
     @return the hedge of @f$x@f$
     */
    public abstract double hedge(double x);

    /**
     Returns the name of the hedge

     @return the name of the hedge
     */
    public String getName() {
        return getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

    /**
     Creates a clone of the hedge

     @return a clone of the hedge.
     */
    @Override
    public Hedge clone() throws CloneNotSupportedException {
        return (Hedge) super.clone();
    }

}
