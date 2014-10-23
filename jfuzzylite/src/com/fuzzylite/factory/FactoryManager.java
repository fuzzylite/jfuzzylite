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

    protected static FactoryManager instance;

    public synchronized static FactoryManager instance() {
        if (instance == null) {
            instance = new FactoryManager();
            instance.setDefuzzifier(new DefuzzifierFactory());
            instance.setHedge(new HedgeFactory());
            instance.setSNorm(new SNormFactory());
            instance.setTNorm(new TNormFactory());
            instance.setTerm(new TermFactory());
        }
        return instance;
    }

    protected DefuzzifierFactory defuzzifier;
    protected HedgeFactory hedge;
    protected SNormFactory sNorm;
    protected TNormFactory tNorm;
    protected TermFactory term;

    protected FactoryManager() {
    }

    public DefuzzifierFactory defuzzifier() {
        return defuzzifier;
    }

    public void setDefuzzifier(DefuzzifierFactory defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public HedgeFactory hedge() {
        return hedge;
    }

    public void setHedge(HedgeFactory hedge) {
        this.hedge = hedge;
    }

    public SNormFactory snorm() {
        return sNorm;
    }

    public void setSNorm(SNormFactory sNorm) {
        this.sNorm = sNorm;
    }

    public TNormFactory tnorm() {
        return tNorm;
    }

    public void setTNorm(TNormFactory tNorm) {
        this.tNorm = tNorm;
    }

    public TermFactory term() {
        return term;
    }

    public void setTerm(TermFactory term) {
        this.term = term;
    }
}
