/*
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
 */
package com.fuzzylite.variable;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Accumulated;

public class OutputVariable extends Variable {

    protected Accumulated fuzzyOutput;
    protected Defuzzifier defuzzifier;
    protected double defaultValue;
    protected double lastValidOutput;
    protected boolean lockOutputRange;
    protected boolean lockValidOutput;

    public OutputVariable() {
        this("");
    }

    public OutputVariable(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public OutputVariable(String name, double minimum, double maximum) {
        super(name, minimum, maximum);
        this.fuzzyOutput = new Accumulated("fuzzyOutput", minimum, maximum);
        this.defaultValue = Double.NaN;
        this.lastValidOutput = Double.NaN;
        this.lockOutputRange = false;
        this.lockValidOutput = false;
    }

    public double defuzzify() {
        double result;
        boolean isValid = this.enabled && !this.fuzzyOutput.getTerms().isEmpty();
        if (isValid) {
            result = this.defuzzifier.defuzzify(fuzzyOutput, minimum, maximum);
        } else {
            //if a previous defuzzification was successfully performed and
            //and the output is supposed to not change when the output is empty
            if (this.lockValidOutput && !Double.isNaN(this.lastValidOutput)) {
                result = this.lastValidOutput;
            } else {
                result = this.defaultValue;
            }
        }
        if (this.lockOutputRange) {
            if (Op.isLt(result, minimum)) {
                result = minimum;
            }
            if (Op.isGt(result, maximum)) {
                result = maximum;
            }
        }
        if (isValid) {
            this.lastValidOutput = result;
        }
        return result;
    }

    public double defuzzifyNoLocks() {
        double result;
        boolean isValid = this.enabled && !fuzzyOutput.getTerms().isEmpty();
        if (isValid) {
            result = this.defuzzifier.defuzzify(fuzzyOutput, minimum, maximum);
        } else {
            result = this.defaultValue;
        }
        return result;
    }

    @Override
    public String toString() {
        return new FllExporter("", "; ").toString(this);
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

    public Accumulated fuzzyOutput() {
        return fuzzyOutput;
    }

    public Defuzzifier getDefuzzifier() {
        return defuzzifier;
    }

    public void setDefuzzifier(Defuzzifier defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getLastValidOutput() {
        return lastValidOutput;
    }

    public void setLastValidOutput(double lastValidOutput) {
        this.lastValidOutput = lastValidOutput;
    }

    public boolean isLockingOutputRange() {
        return lockOutputRange;
    }

    public void setLockOutputRange(boolean lockOutputRange) {
        this.lockOutputRange = lockOutputRange;
    }

    public boolean isLockingValidOutput() {
        return lockValidOutput;
    }

    public void setLockValidOutput(boolean lockValidOutput) {
        this.lockValidOutput = lockValidOutput;
    }
}
