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
package com.fuzzylite.hedge;

import com.fuzzylite.term.Function;

/**
 The HedgeFunction class is a customizable Hedge via Function, which computes
 any function based on the @f$x@f$ value. This hedge is not registered with the
 HedgeFactory due to issues configuring the formula within. To register the
 hedge, a static method with the constructor needs to be manually created and
 registered.

 @author Juan Rada-Vilela, Ph.D.
 @see Function
 @see Hedge
 @see HedgeFactory
 @since 6.0
 */
public final class HedgeFunction extends Hedge {

    private Function function;

    public HedgeFunction() {
        this("");
    }

    public HedgeFunction(String formula) {
        this.function = new Function();
        this.function.getVariables().put("x", Double.NaN);
        if (!formula.isEmpty()) {
            this.function.load(formula);
        }
    }

    /**
     Returns the reference to the Function

     @return the reference to the Function
     */
    public Function function() {
        return this.function;
    }

    /**
     Loads the function with the given formula

     @param formula is a valid formula in infix notation
     */
    public void setFormula(String formula) {
        this.function.load(formula);
    }

    /**
     Returns the formula loaded into the function

     @return the formula loaded into the function
     */
    public String getFormula() {
        return this.function.getFormula();
    }

    /**
     Computes the hedge for the membership function value @f$x@f$ utilizing the
     given function via HedgeFunction::setFormula()

     @param x is a membership function value
     @return the evaluation of the function
     */
    @Override
    public double hedge(double x) {
        return this.function.membership(x);
    }

    @Override
    public HedgeFunction clone() throws CloneNotSupportedException {
        HedgeFunction result = (HedgeFunction) super.clone();
        if (this.function != null) {
            result.function = this.function.clone();
        }
        return result;
    }

}
