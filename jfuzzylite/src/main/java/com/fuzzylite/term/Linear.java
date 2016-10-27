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
package com.fuzzylite.term;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;

import java.util.ArrayList;
import java.util.List;

/**
 The Linear class is a linear polynomial Term expressed as @f$f(x)=
 \mathbf{c}\mathbf{v}+k = \sum_i c_iv_i + k@f$, where variable @f$x@f$ is not
 utilized, @f$\mathbf{v}@f$ is a vector of values from the input variables,

 @f$\mathbf{c}@f$ is a vector of coefficients, and @f$k@f$ is a constant.
 Hereinafter, the vector @f$\mathbf{c}^\star=\{c_1, \ldots, c_i, \ldots, c_n,
 k\}@f$ refers to a vector containing the coefficients of

 @f$\mathbf{c}@f$ and the constant @f$k@f$.

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Linear extends Term {

    /**
     Contains the coefficients @f$c_i@f$ and the constant @f$k@f$
     */
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

    /**
     Returns the vector @f$\mathbf{c}^\star@f$

     @return `"c1 ... ci ... cn k"`
     */
    @Override
    public String parameters() {
        return Op.join(coefficients, " ");
    }

    /**
     Configures the term with the values of @f$\mathbf{c}^\star@f$

     @param parameters as `"c1 ... ci ... cn k"`
     */
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

    /**
     Creates a Linear term from a variadic set of coefficients. The number of
     variadic arguments should be the same as the number of input variables in
     the engine plus one in order to match the size of the list

     @f$\mathbf{c}^\star@f$

     @param name is the name of the term
     @param engine is the engine from which the vector @f$\mathbf{v}@f$ will be
     obtained
     @param coefficients is a variadic number of coefficients
     @return a new Linear term with the given parameters
     */
    public static Linear create(String name, Engine engine,
            double... coefficients) {
        List<Double> coefficientsList = new ArrayList<Double>(coefficients.length);
        for (double coefficient : coefficients) {
            coefficientsList.add(coefficient);
        }
        return new Linear(name, coefficientsList, engine);
    }

    /**
     Computes the linear function @f$f(x)=\sum_i c_iv_i +k@f$, where @f$v_i@f$
     is the value of the input variable @f$i@f$ registered in the
     Linear::getEngine()

     @param x is not utilized
     @return @f$\sum_i c_ix_i +k@f$
     */
    @Override
    public double membership(double x) {
        double result = 0;
        List<InputVariable> inputVariables = engine.getInputVariables();
        final int numberOfVariables = inputVariables.size();
        final int numberOfCoefficients = coefficients.size();
        for (int i = 0; i < numberOfVariables; ++i) {
            if (i < numberOfCoefficients) {
                result += coefficients.get(i) * inputVariables.get(i).getValue();
            }
        }
        if (numberOfCoefficients > numberOfVariables) {
            result += coefficients.get(numberOfCoefficients - 1);
        }
        return result;
    }

    /**
     Gets the list of coefficients @f$\mathbf{c}^\star@f$

     @return the list of coefficients @f$\mathbf{c}^\star@f$
     */
    public List<Double> getCoefficients() {
        return coefficients;
    }

    /**
     Sets the list @f$\mathbf{c}^\star@f$ of the linear function

     @param coefficients is the list @f$\mathbf{c}^\star@f$
     */
    public void setCoefficients(List<Double> coefficients) {
        this.coefficients = coefficients;
    }

    /**
     Gets the engine from which the list @f$\mathbf{v}@f$ will be obtained
     upon computing the Linear::membership()

     @return the engine from which the list @f$\mathbf{v}@f$ will be obtained
     */
    public Engine getEngine() {
        return this.engine;
    }

    /**
     Sets the engine from which the list @f$\mathbf{v}@f$ will be obtained
     upon computing the Linear::membership()

     @param engine is the engine from which the list @f$\mathbf{v}@f$ will be
     obtained
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     Sets the list @f$\mathbf{c}^\star@f$ and the Engine from which list
     @f$\mathbf{v}@f$ will be retrieved when necessary

     @param coefficients is the list @f$\mathbf{c}^\star@f$
     @param engine is the engine from which @f$\mathbf{v}@f$ will be retrieved
     when necessary
     */
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

    @Override
    public void updateReference(Engine engine) {
        setEngine(engine);
    }

}
