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

import com.fuzzylite.defuzzifier.Bisector;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.LargestOfMaximum;
import com.fuzzylite.defuzzifier.MeanOfMaximum;
import com.fuzzylite.defuzzifier.SmallestOfMaximum;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.defuzzifier.WeightedAverageCustom;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedSum;
import com.fuzzylite.defuzzifier.WeightedSumCustom;

/**
 The DefuzzifierFactory class is a ConstructionFactory of Defuzzifier%s.

 @author Juan Rada-Vilela, Ph.D.
 @see Defuzzifier
 @see ConstructionFactory
 @see FactoryManager
 @since 4.0
 */
public class DefuzzifierFactory extends ConstructionFactory<Defuzzifier> {

    public DefuzzifierFactory() {
        register(Bisector.class);
        register(Centroid.class);
        register(LargestOfMaximum.class);
        register(MeanOfMaximum.class);
        register(SmallestOfMaximum.class);
        register(WeightedAverage.class);
        register(WeightedAverageCustom.class);
        register(WeightedSum.class);
        register(WeightedSumCustom.class);
    }

    /**
     Creates a Defuzzifier by executing the registered constructor

     @param key is the unique name by which constructors are registered
     @param resolution is the resolution of an IntegralDefuzzifier
     @param type is the type of a WeightedDefuzzifier
     @return a Defuzzifier by executing the registered constructor and setting
     its resolution or type accordingly
     */
    public Defuzzifier constructDefuzzifier(String key, int resolution,
            WeightedDefuzzifier.Type type) {
        Defuzzifier result = constructObject(key);
        if (result instanceof IntegralDefuzzifier) {
            ((IntegralDefuzzifier) result).setResolution(resolution);
        } else if (result instanceof WeightedDefuzzifier) {
            ((WeightedDefuzzifier) result).setType(type);
        }
        return result;
    }

    /**
     Creates a Defuzzifier by executing the registered constructor

     @param key is the unique name by which constructors are registered
     @param resolution is the resolution of an IntegralDefuzzifier
     @return a Defuzzifier by executing the registered constructor and setting
     its resolution
     */
    public Defuzzifier constructDefuzzifier(String key, int resolution) {
        Defuzzifier result = constructObject(key);
        if (result instanceof IntegralDefuzzifier) {
            ((IntegralDefuzzifier) result).setResolution(resolution);
        }
        return result;
    }

    /**
     Creates a Defuzzifier by executing the registered constructor

     @param key is the unique name by which constructors are registered
     @param type is the type of a WeightedDefuzzifier
     @return a Defuzzifier by executing the registered constructor and setting
     its type
     */
    public Defuzzifier constrDefuzzifier(String key, WeightedDefuzzifier.Type type) {
        Defuzzifier result = constructObject(key);
        if (result instanceof WeightedDefuzzifier) {
            ((WeightedDefuzzifier) result).setType(type);
        }
        return result;
    }

    @Override
    public DefuzzifierFactory clone() throws CloneNotSupportedException {
        return (DefuzzifierFactory) super.clone();
    }

}
