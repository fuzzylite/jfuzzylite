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

package com.fuzzylite.variable;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Term;
import java.util.Iterator;

public class OutputVariable extends Variable {

    private Aggregated fuzzyOutput;
    private Defuzzifier defuzzifier;
    private double previousValue;
    private double defaultValue;
    private boolean lockPreviousValue;

    public OutputVariable() {
        this("");
    }

    public OutputVariable(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public OutputVariable(String name, double minimum, double maximum) {
        super(name, minimum, maximum);
        this.fuzzyOutput = new Aggregated("fuzzyOutput", minimum, maximum);
        this.defuzzifier = null;
        this.previousValue = Double.NaN;
        this.defaultValue = Double.NaN;
        this.lockPreviousValue = false;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.fuzzyOutput.setName(name);
    }

    public Aggregated fuzzyOutput() {
        return fuzzyOutput;
    }

    @Override
    public void setMinimum(double minimum) {
        super.setMinimum(minimum);
        fuzzyOutput().setMinimum(minimum);
    }

    @Override
    public void setMaximum(double maximum) {
        super.setMaximum(maximum);
        fuzzyOutput().setMaximum(maximum);
    }

    public Defuzzifier getDefuzzifier() {
        return defuzzifier;
    }

    public void setDefuzzifier(Defuzzifier defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(double previousValue) {
        this.previousValue = previousValue;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isLockPreviousValue() {
        return lockPreviousValue;
    }

    public void setLockPreviousValue(boolean lockPreviousValue) {
        this.lockPreviousValue = lockPreviousValue;
    }

    public void defuzzify() {
        if (!isEnabled()) {
            return;
        }

        if (Op.isFinite(getValue())) {
            setPreviousValue(getValue());
        }

        String exception = null;

        double result = Double.NaN;
        boolean isValid = isEnabled() && !fuzzyOutput().getTerms().isEmpty();
        if (isValid) {
            /* Checks whether the variable can be defuzzified without exceptions.
             * If it cannot be defuzzified, be that due to a missing defuzzifier  
             * or aggregation operator, the expected behaviour is to leave the 
             * variable in a state that reflects an invalid defuzzification, 
             * that is, apply logic of default values and previous values.*/
            isValid = false;
            if (getDefuzzifier() != null) {
                try {
                    result = getDefuzzifier().defuzzify(fuzzyOutput(),
                            getMinimum(), getMaximum());
                    isValid = true;
                } catch (Exception ex) {
                    exception = ex.toString();
                }
            } else {
                exception = String.format("[defuzzifier error] defuzzifier needed "
                        + "to defuzzify output variable <%s>", getName());
            }
        }

        if (!isValid) {
            //if a previous defuzzification was successfully performed and
            //and the output value is supposed not to change when the output is empty
            if (isLockPreviousValue() && !Double.isNaN(getPreviousValue())) {
                result = getPreviousValue();
            } else {
                result = getDefaultValue();
            }
        }

        setValue(result);

        if (exception != null) {
            throw new RuntimeException(exception);
        }
    }

    public String fuzzyOutputValue() {
        StringBuilder sb = new StringBuilder();
        Iterator<Term> it = getTerms().iterator();
        if (it.hasNext()) {
            Term term = it.next();
            double degree = fuzzyOutput.activationDegree(term);
            sb.append(Op.str(degree)).append("/").append(term.getName());
            while (it.hasNext()) {
                term = it.next();
                degree = fuzzyOutput.activationDegree(term);
                if (Double.isNaN(degree) || Op.isGE(degree, 0.0)) {
                    sb.append(" + ").append(Op.str(degree));
                } else {
                    sb.append(" - ").append(Op.str(Math.abs(degree)));
                }
                sb.append("/").append(term.getName());
            }
        }
        return sb.toString();
    }

    public void clear() {
        fuzzyOutput().clear();
        setValue(Double.NaN);
        setPreviousValue(Double.NaN);
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    @Override
    public OutputVariable clone() throws CloneNotSupportedException {
        OutputVariable result = (OutputVariable) super.clone();
        result.fuzzyOutput = this.fuzzyOutput.clone();
        if (this.defuzzifier != null) {
            result.defuzzifier = this.defuzzifier.clone();
        }
        return result;
    }

}
