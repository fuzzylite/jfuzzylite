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

import com.fuzzylite.hedge.Any;
import com.fuzzylite.hedge.Extremely;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.hedge.Not;
import com.fuzzylite.hedge.Seldom;
import com.fuzzylite.hedge.Somewhat;
import com.fuzzylite.hedge.Very;

public class HedgeFactory extends ConstructionFactory<Hedge> {

    public HedgeFactory() {
        register("", null);
        register(new Any().getName(), Any.class);
        register(new Extremely().getName(), Extremely.class);
        register(new Not().getName(), Not.class);
        register(new Seldom().getName(), Seldom.class);
        register(new Somewhat().getName(), Somewhat.class);
        register(new Very().getName(), Very.class);
    }

    @Override
    public HedgeFactory clone() throws CloneNotSupportedException {
        return (HedgeFactory) super.clone();
    }
}
