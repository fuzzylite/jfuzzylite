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
package com.fuzzylite.factory;

/**
 The FactoryManager class is a central class grouping different factories of
 objects, together with a singleton instance to access each of the factories
 throughout the library.


 @author Juan Rada-Vilela, Ph.D.
 @see TermFactory
 @see TNormFactory
 @see SNormFactory
 @see HedgeFactory
 @see ActivationFactory
 @see DefuzzifierFactory
 @see FunctionFactory
 @since 4.0
 */
public class FactoryManager {

    protected static final FactoryManager INSTANCE = new FactoryManager();

    /**
     Gets the static instance of the manager

     @return the static instance of the manager
     */
    public static FactoryManager instance() {
        return INSTANCE;
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

    /**
     Gets the factory of TNorm%s

     @return the factory of TNorm%s
     */
    public TNormFactory tnorm() {
        return tnorm;
    }

    /**
     Sets the factory of TNorm%s

     @param tnorm is the factory of TNorm%s
     */
    public void setTNorm(TNormFactory tnorm) {
        this.tnorm = tnorm;
    }

    /**
     Gets the factory of SNorm%s

     @return the factory of SNorm%s
     */
    public SNormFactory snorm() {
        return snorm;
    }

    /**
     Sets the factory of SNorm%s

     @param snorm is the factory of SNorm%s
     */
    public void setSNorm(SNormFactory snorm) {
        this.snorm = snorm;
    }

    /**
     Gets the factory of Activation methods

     @return the factory of Activation methods
     */
    public ActivationFactory activation() {
        return activation;
    }

    /**
     Sets the factory of Activation methods

     @param activation is the factory of Activation methods
     */
    public void setActivation(ActivationFactory activation) {
        this.activation = activation;
    }

    /**
     Gets the factory of Defuzzifier%s

     @return the factory of Defuzzifier%s
     */
    public DefuzzifierFactory defuzzifier() {
        return defuzzifier;
    }

    /**
     Sets the factory of Defuzzifier%s

     @param defuzzifier is the factory of Defuzzifier%s
     */
    public void setDefuzzifier(DefuzzifierFactory defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    /**
     Gets the factory of Term%s

     @return the factory of Term%s
     */
    public TermFactory term() {
        return term;
    }

    /**
     Sets the factory of Term%s

     @param term is the factory of Term%s
     */
    public void setTerm(TermFactory term) {
        this.term = term;
    }

    /**
     Gets the factory of Hedge%s

     @return the factory of Hedge%s
     */
    public HedgeFactory hedge() {
        return hedge;
    }

    /**
     Sets the factory of Hedge%s

     @param hedge is the factory of Hedge%s
     */
    public void setHedge(HedgeFactory hedge) {
        this.hedge = hedge;
    }

    /**
     Gets the factory of Function Element%s

     @return the factory of Function Element%s
     */
    public FunctionFactory function() {
        return function;
    }

    /**
     Sets the factory of Function Element%s

     @param function is the factory of Function Element%s
     */
    public void setFunction(FunctionFactory function) {
        this.function = function;
    }

}
