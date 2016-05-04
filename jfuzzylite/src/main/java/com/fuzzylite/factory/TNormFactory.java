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

import com.fuzzylite.norm.TNorm;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.norm.t.BoundedDifference;
import com.fuzzylite.norm.t.DrasticProduct;
import com.fuzzylite.norm.t.EinsteinProduct;
import com.fuzzylite.norm.t.HamacherProduct;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.norm.t.NilpotentMinimum;

public class TNormFactory extends ConstructionFactory<TNorm> {

    public TNormFactory() {
        register("", null);
        register(AlgebraicProduct.class);
        register(BoundedDifference.class);
        register(DrasticProduct.class);
        register(EinsteinProduct.class);
        register(HamacherProduct.class);
        register(Minimum.class);
        register(NilpotentMinimum.class);
    }

    @Override
    public TNormFactory clone() throws CloneNotSupportedException {
        return (TNormFactory) super.clone();
    }
}
