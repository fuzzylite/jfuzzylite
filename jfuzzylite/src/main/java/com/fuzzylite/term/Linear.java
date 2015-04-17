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

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;
import java.util.ArrayList;
import java.util.List;

public class Linear extends Term {

    private List<Double> coefficients;
    private Engine engine;

    public Linear() {
        this("");
    }

    public Linear(String name) {
        this(name, new ArrayList<Double>(), null);
    }

    public Linear(String name, List<Double> coefficients) {
        this(name, coefficients, null);
    }

    public Linear(String name, List<Double> coefficients, Engine engine) {
        super(name);
        this.coefficients = coefficients;
        this.engine = engine;
    }

    @Override
    public String parameters() {
        return Op.join(coefficients, " ");
    }

    @Override
    public void configure(String parameters) {
        coefficients.clear();
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        for (String x : values) {
            coefficients.add(Op.toDouble(x));
        }
    }

    public static Linear create(String name, Engine engine,
            double... coefficients) {
        List<Double> coefficientsList = new ArrayList<Double>(coefficients.length);
        for (double coefficient : coefficients) {
            coefficientsList.add(coefficient);
        }
        return new Linear(name, coefficientsList, engine);
    }

    @Override
    public double membership(double x) {
        double result = 0;
        List<InputVariable> inputVariables = engine.getInputVariables();
        for (int i = 0; i < inputVariables.size(); ++i) {
            if (i < coefficients.size()) {
                result += coefficients.get(i) * inputVariables.get(i).getInputValue();
            }
        }
        if (coefficients.size() > inputVariables.size()) {
            result += coefficients.get(coefficients.size() - 1);
        }
        return result;
    }

    public List<Double> getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(List<Double> coefficients) {
        this.coefficients = coefficients;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void set(List<Double> coefficients, Engine engine) {
        setCoefficients(coefficients);
        setEngine(engine);
    }

    @Override
    public Linear clone() throws CloneNotSupportedException {
        Linear result = (Linear) super.clone();
        result.coefficients = new ArrayList<Double>(this.coefficients);
        return result;
    }

}
