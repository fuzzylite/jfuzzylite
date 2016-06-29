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
package com.fuzzylite.activation;

import com.fuzzylite.Op;
import com.fuzzylite.rule.RuleBlock;

public abstract class Activation implements Op.Cloneable {

    public Activation() {

    }

    public abstract String parameters();

    public abstract void configure(String parameters);

    public abstract void activate(RuleBlock ruleBlock);

    @Override
    public Activation clone() throws CloneNotSupportedException {
        return (Activation) super.clone();
    }

}
