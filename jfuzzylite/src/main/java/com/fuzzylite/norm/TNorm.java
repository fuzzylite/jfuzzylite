/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
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
