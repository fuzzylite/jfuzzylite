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

public class OutputVariable extends Variable {

    protected Accumulated fuzzyOutput;
    protected Defuzzifier defuzzifier;
    protected double outputValue;
    protected double previousOutputValue;
    protected double defaultValue;

    protected boolean lockOutputValueInRange;
    protected boolean lockPreviousOutputValue;

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

    public boolean isLockedOutputValueInRange() {
        return lockOutputValueInRange;
    }

    public void setLockOutputValueInRange(boolean lockOutputValueInRange) {
        this.lockOutputValueInRange = lockOutputValueInRange;
    }

    public boolean isLockedPreviousOutputValue() {
        return lockPreviousOutputValue;
    }

    public void setLockPreviousOutputValue(boolean lockPreviousOutputValue) {
        this.lockPreviousOutputValue = lockPreviousOutputValue;
    }

    public double defuzzify() {
        if (Op.isFinite(this.outputValue)) {
            this.previousOutputValue = this.outputValue;
        }
        double result;
        boolean isValid = this.enabled && !this.fuzzyOutput.getTerms().isEmpty();
        if (isValid) {
            if (this.defuzzifier == null) {
                throw new RuntimeException(String.format(
                        "[defuzzifier error] defuzzifier needed to defuzzify output variable <%s>", name));
            }
            result = this.defuzzifier.defuzzify(fuzzyOutput, minimum, maximum);
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
            result = Op.bound(result, minimum, maximum);
        }
        return result;
    }

    public String fuzzyOutputValue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terms.size(); ++i) {
            double degree = fuzzyOutput.activationDegree(terms.get(i));
            if (i == 0) {
                sb.append(Op.str(degree));
            } else {
                if (Double.isNaN(degree) || Op.isGE(degree, 0.0)) {
                    sb.append(" + ").append(Op.str(degree));
                } else {
                    sb.append(" - ").append(Op.str(Math.abs(degree)));
                }
            }
            sb.append("/").append(terms.get(i).getName());
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
        return new FllExporter("", "; ").toString(this);
    }

}
