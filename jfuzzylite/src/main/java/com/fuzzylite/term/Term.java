/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.term;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;

/**
 The Term class is the abstract class for linguistic terms. The linguistic terms
 in this library can be divided in four groups as: `basic`, `extended`, `edge`,
 and `function`. The `basic` terms are Triangle, Trapezoid, Rectangle, and
 Discrete. The `extended` terms are Bell, Binary, Cosine, Gaussian,
 GaussianProduct, PiShape, SigmoidDifference, SigmoidProduct, and Spike. The
 `edge` terms are Concave, Ramp, Sigmoid, SShape, and ZShape. The `function`
 terms are Constant, Linear, and Function.

 In the figure below, the `basic` terms are represented in the first column, and
 the `extended` terms in the second and third columns. The `edge` terms are
 represented in the fifth and sixth rows, and the `function` terms in the last
 row.

 @image html terms.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Variable
 @see InputVariable
 @see OutputVariable
 @since 4.0
 */
public abstract class Term implements Op.Cloneable {

    protected String name;
    protected double height;

    public Term() {
        this("");
    }

    public Term(String name) {
        this(name, 1.0);
    }

    public Term(String name, double height) {
        this.name = name;
        this.height = height;
    }

    /**
     Gets the name of the term

     @return the name of the term
     */
    public String getName() {
        return name;
    }

    /**
     Sets the name of the term

     @param name is the name of term
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Gets the height of the term

     @return the height of the term
     */
    public double getHeight() {
        return height;
    }

    /**
     Sets the height of the term

     @param height is the height of the term
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     Returns the representation of the term in the FuzzyLite Language

     @return the representation of the term in FuzzyLite Language
     @see FllExporter
     */
    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    /**
     Returns the parameters to configure the term. The parameters are separated
     by spaces. If there is one additional parameter, the parameter will be
     considered as the height of the term; otherwise, the height will be set to

     `1.0`

     @return the parameters to configure the term (@see Term::configure())
     */
    public abstract String parameters();

    /**
     Configures the term with the given parameters. The parameters are separated
     by spaces. If there is one additional parameter, the parameter will be
     considered as the height of the term; otherwise, the height will be set to

     `1.0`

     @param parameters is the parameters to configure the term
     */
    public abstract void configure(String parameters);

    /**
     Computes the membership function value at `x`

     @param x
     @return the membership function value `\mu(x)`
     */
    public abstract double membership(double x);

    /**
     Creates a clone of the term

     @return a clone of the term
     @throws CloneNotSupportedException
     */
    @Override
    public Term clone() throws CloneNotSupportedException {
        return (Term) super.clone();
    }

    /**
     Updates the references (if any) to point to the current engine (useful when
     cloning engines or creating terms within Importer objects

     @param engine is the engine to which this term belongs to
     */
    public void updateReference(Engine engine) {
        //do nothing...
    }

    /**
     For monotonic terms, computes the tsukamoto value of the term for the given
     activation degree `\alpha`, that is,

     ` g_j(\alpha) = \{ z \in\mathbb{R} : \mu_j(z) = \alpha \} $@f. If the
     term is not monotonic (or does not override this method) the method
     computes the membership function `\mu(\alpha)`.
     @param activationDegree is the activationDegree
     @param minimum is the minimum value of the range of the term
     @param maximum is the maximum value of the range of the term
     @return the tsukamoto value of the term for the given activation degree if
     the term is monotonic (or overrides this method), or the membership
     function for the activation degree otherwise.
     */
    public double tsukamoto(double activationDegree, double minimum, double maximum) {
        return membership(activationDegree);
    }

    /**
     Indicates whether the term is monotonic.

     @return whether the term is monotonic.
     */
    public boolean isMonotonic() {
        return false;
    }
}
