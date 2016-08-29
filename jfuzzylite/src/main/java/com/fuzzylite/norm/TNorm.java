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
package com.fuzzylite.norm;

/**
 The TNorm class is the base class for T-Norms, and it is utilized as the
 conjunction fuzzy logic operator and as the implication (or `activation` in
 versions 5.0 and earlier) fuzzy logic operator.

 @author Juan Rada-Vilela, Ph.D.
 @see RuleBlock::getConjunction()
 @see RuleBlock::getImplication()
 @see TNormFactory
 @see Norm
 @since 4.0
 */
public abstract class TNorm extends Norm {

    @Override
    public TNorm clone() throws CloneNotSupportedException {
        return (TNorm) super.clone();
    }
}
