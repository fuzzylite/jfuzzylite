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

import com.fuzzylite.imex.FllExporter;

/**
 *
 * @author jcrada
 */
public class InputVariable extends Variable {

    protected double inputValue;

    public InputVariable() {
        this("");
    }

    public InputVariable(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public InputVariable(String name, double minimum, double maximum) {
        super(name, minimum, maximum);
        this.inputValue = Double.NaN;
    }

    public double getInputValue() {
        return inputValue;
    }

    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }

    @Override
    public String toString() {
        return new FllExporter("", "; ").toString(this);
    }

}
