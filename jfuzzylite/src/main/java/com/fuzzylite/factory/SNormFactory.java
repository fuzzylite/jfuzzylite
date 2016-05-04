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

import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.s.AlgebraicSum;
import com.fuzzylite.norm.s.BoundedSum;
import com.fuzzylite.norm.s.DrasticSum;
import com.fuzzylite.norm.s.EinsteinSum;
import com.fuzzylite.norm.s.HamacherSum;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.s.NilpotentMaximum;
import com.fuzzylite.norm.s.NormalizedSum;

public class SNormFactory extends ConstructionFactory<SNorm> {

    public SNormFactory() {
        register("", null);
        register(AlgebraicSum.class);
        register(BoundedSum.class);
        register(DrasticSum.class);
        register(EinsteinSum.class);
        register(HamacherSum.class);
        register(Maximum.class);
        register(NilpotentMaximum.class);
        register(NormalizedSum.class);
    }

    @Override
    public SNormFactory clone() throws CloneNotSupportedException {
        return (SNormFactory) super.clone();
    }
}
