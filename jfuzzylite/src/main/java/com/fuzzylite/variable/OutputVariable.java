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
package com.fuzzylite.variable;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Accumulated;
import com.fuzzylite.term.Term;
import java.util.Iterator;

public class OutputVariable extends Variable {

    private Accumulated fuzzyOutput;
    private Defuzzifier defuzzifier;
    private double outputValue;
    private double previousOutputValue;
    private double defaultValue;
    private boolean lockOutputValueInRange;
    private boolean lockPreviousOutputValue;

    public OutputVariable() {
        this("");
    }

    public OutputVariable(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public OutputVariable(String name, double minimum, double maximum) {
        super(name, minimum, maximum);
        this.fuzzyOutput = new Accumulated("fuzzyOutput", minimum, maximum);
        this.outputValue = Double.NaN;
        this.previousOutputValue = Double.NaN;
        this.defaultValue = Double.NaN;
        this.lockOutputValueInRange = false;
        this.lockPreviousOutputValue = false;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.fuzzyOutput.setName(name);
    }

    public Accumulated fuzzyOutput() {
        return fuzzyOutput;
    }

    @Override
    public void setMinimum(double minimum) {
        super.setMinimum(minimum);
        this.fuzzyOutput.setMinimum(minimum);
    }

    @Override
    public void setMaximum(double maximum) {
        super.setMaximum(maximum);
        this.fuzzyOutput.setMaximum(maximum);
    }

    public Defuzzifier getDefuzzifier() {
        return defuzzifier;
    }

    public void setDefuzzifier(Defuzzifier defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public double getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(double outputValue) {
        this.outputValue = outputValue;
    }

    public double getPreviousOutputValue() {
        return previousOutputValue;
    }

    public void setPreviousOutputValue(double previousOutputValue) {
        this.previousOutputValue = previousOutputValue;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isLockOutputValueInRange() {
        return lockOutputValueInRange;
    }

    public void setLockOutputValueInRange(boolean lockOutputValueInRange) {
        this.lockOutputValueInRange = lockOutputValueInRange;
    }

    public boolean isLockPreviousOutputValue() {
        return lockPreviousOutputValue;
    }

    public void setLockPreviousOutputValue(boolean lockPreviousOutputValue) {
        this.lockPreviousOutputValue = lockPreviousOutputValue;
    }

    public void defuzzify() {
        if (Op.isFinite(this.outputValue)) {
            this.previousOutputValue = this.outputValue;
        }
        double result;
        boolean isValid = this.isEnabled() && !this.fuzzyOutput.getTerms().isEmpty();
        if (isValid) {
            if (this.defuzzifier == null) {
                throw new RuntimeException(String.format(
                        "[defuzzifier error] defuzzifier needed to defuzzify output variable <%s>", getName()));
            }
            result = this.defuzzifier.defuzzify(fuzzyOutput, this.getMinimum(), this.getMaximum());
        } else {
            //if a previous defuzzification was successfully performed and
            //and the output is supposed to not change when the output is empty
            if (this.lockPreviousOutputValue && !Double.isNaN(this.previousOutputValue)) {
                result = this.previousOutputValue;
            } else {
                result = this.defaultValue;
            }
        }
        if (this.lockOutputValueInRange) {
            result = Op.bound(result, this.getMinimum(), this.getMaximum());
        }
        this.outputValue = result;
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
        fuzzyOutput.clear();
        setPreviousOutputValue(Double.NaN);
        setOutputValue(Double.NaN);
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
