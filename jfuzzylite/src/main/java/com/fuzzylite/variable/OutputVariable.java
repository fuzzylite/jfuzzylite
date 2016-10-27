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
package com.fuzzylite.variable;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.term.Aggregated;
import com.fuzzylite.term.Term;

import java.util.Iterator;

/**
 The OutputVariable class is a Variable that represents an output of the fuzzy
 logic controller. During the activation of a RuleBlock, the Activated terms of
 each Rule will be Aggregated in the OutputVariable::fuzzyOutput(), which
 represents a fuzzy set hereinafter referred to as @f$\tilde{y}@f$. The
 defuzzification of @f$\tilde{y}@f$ translates the fuzzy output value

 @f$\tilde{y}@f$ into a crisp output value @f$y@f$, which can be retrieved using
 Variable::getValue(). The value of the OutputVariable is computed and
 automatically stored when calling OutputVariable::defuzzify(), but the value
 depends on the following properties (expressed in the FuzzyLite Language):

 - Property `default: scalar` overrides the output value @f$y@f$ with the given
 fl::scalar whenever the defuzzification process results in a non-finite value
 (i.e., fl::nan and fl::inf). For example, considering `default: 0.0`, if
 RuleBlock::activate() does not activate any rules whose Consequent contribute
 to the OutputVariable, then the fuzzy output value is empty, the Defuzzifier
 does not operate, and hence @f$y=0.0@f$. By default, `default: NaN`. Relevant
 methods are OutputVariable::getDefaultValue() and
 OutputVariable::setDefaultValue().

 - Property `lock-previous: boolean`, if enabled, overrides the output value
 @f$y^t@f$ at time @f$t@f$ with the previously defuzzified valid output value
 @f$y^{t-1}@f$ if defuzzification process results in a non-finite value (i.e.,
 fl::nan and fl::inf). When enabled, the property takes precedence over
 `default` if @f$y^{t-1}@f$ is a finite value. By default, `lock-previous:
 false`, @f$y^{t-1}=\mbox{NaN}@f$ for @f$t=0@f$, and @f$y^{t-1}=\mbox{NaN}@f$
 when OutputVariable::clear(). Relevant methods are
 OutputVariable::lockPreviousValue(), OutputVariable::isLockPreviousValue,
 OutputVariable::getPreviousValue(), and OutputVariable::setPreviousValue().

 - Property `lock-range: boolean` overrides the output value @f$y@f$ to enforce
 it lies within the range of the variable determined by Variable::getMinimum()
 and Variable::getMaximum(). When enabled, this property takes precedence over
 `lock-previous` and `default`. For example, considering `range: -1.0 1.0` and
 `lock-range: true`,

 @f$y=-1.0@f$ if the result from the Defuzzifier is smaller than `-1.0`, and
 @f$y=1.0@f$ if the result from the Defuzzifier is greater than `1.0`. The
 property `lock-range` was introduced in version 5.0 to substitute the property
 `lock-valid` in version 4.0. By default, `lock-range: false`. Relevant methods
 are Variable::lockValueInRange(), Variable::isLockValueInRange(),
 Variable::getMinimum(), and Variable::getMaximum()


 @author Juan Rada-Vilela, Ph.D.
 @see Variable
 @see InputVariable
 @see RuleBlock::activate()
 @see Term
 @since 4.0
 */
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

    /**
     Gets the fuzzy output value @f$\tilde{y}@f$

     @return the fuzzy output value @f$\tilde{y}@f$
     @todo rename to fuzzyValue
     */
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

    /**
     Gets the defuzzifier of the output variable

     @return the defuzzifier of the output variable
     */
    public Defuzzifier getDefuzzifier() {
        return defuzzifier;
    }

    /**
     Sets the defuzzifier of the output variable

     @param defuzzifier is the defuzzifier of the output variable
     */
    public void setDefuzzifier(Defuzzifier defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    /**
     Gets the previous value of the output variable

     @return the previous value of the output variable
     */
    public double getPreviousValue() {
        return previousValue;
    }

    /**
     Sets the previous value of the output variable

     @param previousValue is the previous value of the output variable
     */
    public void setPreviousValue(double previousValue) {
        this.previousValue = previousValue;
    }

    /**
     Gets the default value of the output variable

     @return the default value of the output variable
     */
    public double getDefaultValue() {
        return defaultValue;
    }

    /**
     Sets the default value of the output variable

     @param defaultValue is the default value of the output variable
     */
    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     Gets whether to lock the previous value of the output variable

     @return whether the previous output value of the output variable is locked
     */
    public boolean isLockPreviousValue() {
        return lockPreviousValue;
    }

    /**
     Sets whether to lock the previous value of the output variable

     @param lockPreviousValue indicates whether to lock the previous value of
     the output variable
     */
    public void setLockPreviousValue(boolean lockPreviousValue) {
        this.lockPreviousValue = lockPreviousValue;
    }

    /**
     Defuzzifies the output variable and stores the output value and the
     previous output value
     */
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

    /**
     Gets a string representation of the fuzzy output value @f$\tilde{y}@f$

     @return a string representation of the fuzzy output value
     @f$\tilde{y}@f$
     */
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

    /**
     Clears the output variable by setting @f$\tilde{y}=\{\}@f$,

     @f$y^{t}=\mbox{NaN}@f$, @f$y^{t-1}=\mbox{NaN}@f$
     */
    public void clear() {
        fuzzyOutput().clear();
        setValue(Double.NaN);
        setPreviousValue(Double.NaN);
    }

    @Override
    public Type type() {
        return Type.Output;
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
