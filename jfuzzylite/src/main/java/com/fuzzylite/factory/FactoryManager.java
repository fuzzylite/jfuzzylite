/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.factory;

public class FactoryManager {

    protected static final FactoryManager instance = new FactoryManager();

    public static FactoryManager instance() {
        return instance;
    }

    private TNormFactory tnorm;
    private SNormFactory snorm;
    private ActivationFactory activation;
    private DefuzzifierFactory defuzzifier;
    private TermFactory term;
    private HedgeFactory hedge;
    private FunctionFactory function;

    private FactoryManager() {
        this(new TNormFactory(), new SNormFactory(), new ActivationFactory(),
                new DefuzzifierFactory(), new TermFactory(), new HedgeFactory(),
                new FunctionFactory());
    }

    private FactoryManager(TNormFactory tnorm, SNormFactory snorm,
            ActivationFactory activation, DefuzzifierFactory defuzzifier,
            TermFactory term, HedgeFactory hedge, FunctionFactory function) {
        this.tnorm = tnorm;
        this.snorm = snorm;
        this.activation = activation;
        this.defuzzifier = defuzzifier;
        this.term = term;
        this.hedge = hedge;
        this.function = function;
    }

    public TNormFactory tnorm() {
        return tnorm;
    }

    public void setTNorm(TNormFactory tnorm) {
        this.tnorm = tnorm;
    }

    public SNormFactory snorm() {
        return snorm;
    }

    public void setSNorm(SNormFactory snorm) {
        this.snorm = snorm;
    }

    public ActivationFactory activation() {
        return activation;
    }

    public void setActivation(ActivationFactory activation) {
        this.activation = activation;
    }

    public DefuzzifierFactory defuzzifier() {
        return defuzzifier;
    }

    public void setDefuzzifier(DefuzzifierFactory defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public TermFactory term() {
        return term;
    }

    public void setTerm(TermFactory term) {
        this.term = term;
    }

    public HedgeFactory hedge() {
        return hedge;
    }

    public void setHedge(HedgeFactory hedge) {
        this.hedge = hedge;
    }

    public FunctionFactory function() {
        return function;
    }

    public void setFunction(FunctionFactory function) {
        this.function = function;
    }

}
