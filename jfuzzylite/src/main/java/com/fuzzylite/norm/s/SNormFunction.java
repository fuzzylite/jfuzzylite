/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.norm.s;

import com.fuzzylite.norm.SNorm;
import com.fuzzylite.term.Function;

/**
 The SNormFunction class is a customizable SNorm via Function, which computes
 any function based on the `a` and `b` values. This SNorm is not
 registered with the SNormFactory.

 @author Juan Rada-Vilela, Ph.D.
 @see Function
 @see SNorm
 @see Norm
 @see SNormFactory
 @since 6.0
 */
public final class SNormFunction extends SNorm {

    private Function function;

    public SNormFunction() {
        this("");
    }

    public SNormFunction(String formula) {
        this.function = new Function();
        this.function.getVariables().put("a", Double.NaN);
        this.function.getVariables().put("b", Double.NaN);
        if (!formula.isEmpty()) {
            this.function.load(formula);
        }
    }

    /**
     Computes the S-Norm utilizing the given function via
     SNormFunction::setFormula(), which automatically assigns the values of

     `a` and `b`.

     @param a is a membership function value
     @param b is a membership function value
     @return the evaluation of the function
     */
    @Override
    public double compute(double a, double b) {
        this.function.getVariables().put("a", a);
        this.function.getVariables().put("b", b);
        return this.function.evaluate();
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

    @Override
    public SNormFunction clone() throws CloneNotSupportedException {
        SNormFunction result = (SNormFunction) super.clone();
        if (this.function != null) {
            result.function = this.function.clone();
            try {
                result.function.load(this.function.getFormula());
            } catch (Exception ex) {
                //ignore...
            }
        }
        return result;
    }

}
