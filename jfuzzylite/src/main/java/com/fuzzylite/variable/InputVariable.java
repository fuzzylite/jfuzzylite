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

import com.fuzzylite.imex.FllExporter;

public class InputVariable extends Variable {

    private double inputValue;

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
    
    public String fuzzyInputValue(){
        return fuzzify(this.inputValue);
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    @Override
    public InputVariable clone() throws CloneNotSupportedException {
        return (InputVariable) super.clone();
    }

}
