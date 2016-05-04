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

import com.fuzzylite.defuzzifier.Bisector;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.LargestOfMaximum;
import com.fuzzylite.defuzzifier.MeanOfMaximum;
import com.fuzzylite.defuzzifier.SmallestOfMaximum;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.defuzzifier.WeightedSum;

public class DefuzzifierFactory extends ConstructionFactory<Defuzzifier> {

    public DefuzzifierFactory() {
        register(Bisector.class);
        register(Centroid.class);
        register(LargestOfMaximum.class);
        register(MeanOfMaximum.class);
        register(SmallestOfMaximum.class);
        register(WeightedAverage.class);
        register(WeightedSum.class);
    }

    @Override
    public DefuzzifierFactory clone() throws CloneNotSupportedException {
        return (DefuzzifierFactory) super.clone();
    }

}
