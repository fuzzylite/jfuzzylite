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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;
import java.util.ArrayList;
import java.util.List;

public class Linear extends Term {

    public List<Double> coefficients;
    public List<InputVariable> inputVariables;

    public Linear() {
        this("");
    }

    public Linear(String name) {
        this(name, new ArrayList<Double>(), new ArrayList<InputVariable>());
    }

    //It is safe to pass the InputVariables from the Engine
    public Linear(String name, List<Double> coefficients, List<InputVariable> inputVariables) {
        this.name = name;
        this.coefficients = coefficients;
        //Copy elements to prevent changing the Engine's input variables
        this.inputVariables = new ArrayList<InputVariable>(inputVariables);
    }

    @Override
    public String parameters() {
        return Op.join(coefficients, " ");
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        coefficients.clear();
        List<String> values = Op.split(parameters, " ");
        for (String x : values) {
            coefficients.add(Op.toDouble(x));
        }
    }

    //It is safe to pass the InputVariables from the Engine
    public static Linear create(String name, List<InputVariable> inputVariables,
            double... coefficients) {
        if (coefficients.length != inputVariables.size() + 1) {
            throw new RuntimeException(String.format(
                    "[linear error] number of coefficient must match number of variables plus a constant c (e.g. ax+by+c), "
                    + "but <%d> coefficients were found and <%d> variables are available",
                    coefficients.length, inputVariables.size()));
        }
        List<Double> coefficientsList = new ArrayList<Double>();
        for (double coefficient : coefficients) {
            coefficientsList.add(coefficient);
        }
        return new Linear(name, coefficientsList, inputVariables);
    }

    @Override
    public double membership(double x) {
        if (coefficients.size() != inputVariables.size() + 1) {
            throw new RuntimeException(String.format(
                    "[linear error] number of coefficient must match number of variables plus a constant c (e.g. ax+by+c), "
                    + "but <%d> coefficients were found and <%d> variables are available",
                    this.coefficients.size(), this.inputVariables.size()));
        }
        double result = 0;
        for (int i = 0; i < inputVariables.size(); ++i) {
            result += coefficients.get(i) * inputVariables.get(i).getInputValue();
        }
        if (coefficients.size() > inputVariables.size()) {
            result += coefficients.get(coefficients.size() - 1);
        }
        return result;
    }

    public List<Double> getCoefficients() {
        return coefficients;
    }

    public List<InputVariable> getInputVariables() {
        return inputVariables;
    }

    //It is safe to pass the InputVariables from the Engine
    public void set(List<Double> coefficients, List<InputVariable> inputVariables) {
        this.coefficients = coefficients;
        this.inputVariables = new ArrayList<InputVariable>(inputVariables);
    }

}
