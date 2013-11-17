/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.variable;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.term.Accumulated;

/**
 *
 * @author jcrada
 */
public class OutputVariable extends Variable {

    protected Accumulated output;
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
        this.output = new Accumulated("output", minimum, maximum);
        this.defaultValue = Double.NaN;
        this.lastValidOutput = Double.NaN;
        this.lockOutputRange = false;
        this.lockValidOutput = false;
    }

    public double defuzzify() {
        double result;
        boolean isValid = this.output.getTerms().size() > 0;
        if (isValid) {
            result = this.defuzzifier.defuzzify(output, minimum, maximum);
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
        if (this.lockValidOutput && isValid) {
            this.lastValidOutput = result;
        }
        return result;
    }

    public double defuzzifyNoLocks() {
        double result;
        boolean isValid = output.getTerms().size() > 0;
        if (isValid) {
            result = this.defuzzifier.defuzzify(output, minimum, maximum);
        } else {
            result = this.defaultValue;
        }
        return result;
    }

    @Override
    public void setMinimum(double minimum) {
        super.setMinimum(minimum);
        this.output.setMinimum(minimum);
    }

    @Override
    public void setMaximum(double maximum) {
        super.setMaximum(maximum);
        this.output.setMaximum(maximum);
    }

    public Accumulated getOutput() {
        return output;
    }

    public void setOutput(Accumulated output) {
        this.output = output;
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

    public boolean isLockOutputRange() {
        return lockOutputRange;
    }

    public void setLockOutputRange(boolean lockOutputRange) {
        this.lockOutputRange = lockOutputRange;
    }

    public boolean isLockValidOutput() {
        return lockValidOutput;
    }

    public void setLockValidOutput(boolean lockValidOutput) {
        this.lockValidOutput = lockValidOutput;
    }
}
