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
package com.fuzzylite.hedge;

import com.fuzzylite.term.Function;

public class HedgeFunction extends Hedge {

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

    public Function function() {
        return this.function;
    }

    public void setFormula(String formula) {
        this.function.setFormula(formula);
    }

    public String getFormula() {
        return this.function.getFormula();
    }

    @Override
    public double hedge(double x) {
        return this.function.membership(x);
    }

    @Override
    public HedgeFunction clone() throws CloneNotSupportedException {
        HedgeFunction result = (HedgeFunction) super.clone();
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
