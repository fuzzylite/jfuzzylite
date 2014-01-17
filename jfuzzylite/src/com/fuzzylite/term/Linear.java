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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcrada
 */
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
        this.inputVariables = new ArrayList<>(inputVariables);
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
        List<Double> coefficientsList = new ArrayList<>();
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
        this.inputVariables = new ArrayList<>(inputVariables);
    }

}
