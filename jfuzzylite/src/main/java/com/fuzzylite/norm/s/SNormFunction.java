/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of fuzzylite®.

 fuzzylite® is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 fuzzylite®. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.

 */
package com.fuzzylite.norm.s;

import com.fuzzylite.norm.SNorm;
import com.fuzzylite.term.Function;

public class SNormFunction extends SNorm {

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

    @Override
    public double compute(double a, double b) {
        this.function.getVariables().put("a", a);
        this.function.getVariables().put("b", b);
        return this.function.evaluate();
    }

    public void setFormula(String formula) {
        this.function.load(formula);
    }

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
