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
package com.fuzzylite.norm.s;

import com.fuzzylite.norm.SNorm;

/**
 The UnboundedSum class is an SNorm that computes the sum of any two values.

 @author Juan Rada-Vilela, Ph.D.
 @see BoundedSum
 @see SNorm
 @see SNormFactory
 @see Norm
 @since 4.0
 */
public class UnboundedSum extends SNorm {

    /**
     Computes the bounded sum of two membership function values

     @param a is a membership function value
     @param b is a membership function value
     @return @f$\min(1, a+b)@f$
     */
    @Override
    public double compute(double a, double b) {
        return a + b;
    }

    @Override
    public UnboundedSum clone() throws CloneNotSupportedException {
        return (UnboundedSum) super.clone();
    }
}
