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
package com.fuzzylite.factory;

public class FactoryManager {

    protected static FactoryManager instance = new FactoryManager();

    public static FactoryManager instance() {
        return instance;
    }

    private TNormFactory tnorm;
    private SNormFactory snorm;
    private DefuzzifierFactory defuzzifier;
    private TermFactory term;
    private HedgeFactory hedge;
    private FunctionFactory function;

    private FactoryManager() {
        this(new TNormFactory(), new SNormFactory(), new DefuzzifierFactory(),
                new TermFactory(), new HedgeFactory(), new FunctionFactory());
    }

    private FactoryManager(TNormFactory tnorm, SNormFactory snorm,
            DefuzzifierFactory defuzzifier, TermFactory term, HedgeFactory hedge,
            FunctionFactory function) {
        this.tnorm = tnorm;
        this.snorm = snorm;
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
