/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.norm.s;

import com.fuzzylite.Op;
import com.fuzzylite.norm.SNorm;

/**
 The BoundedSum class is an SNorm that computes the bounded sum of any two
 values.

 @author Juan Rada-Vilela, Ph.D.
 @see BoundedDifference
 @see SNorm
 @see SNormFactory
 @see Norm
 @since 4.0
 */
public final class BoundedSum extends SNorm {

    /**
     Computes the bounded sum of two membership function values

     @param a is a membership function value
     @param b is a membership function value
     @return `\min(1, a+b)`
     */
    @Override
    public double compute(double a, double b) {
        return Op.min(1.0, a + b);
    }

    @Override
    public BoundedSum clone() throws CloneNotSupportedException {
        return (BoundedSum) super.clone();
    }

}
