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
package com.fuzzylite.norm.t;

import com.fuzzylite.Op;
import com.fuzzylite.norm.TNorm;

/**
 The BoundedDifference class is a TNorm that computes the bounded difference
 between any two values.

 @author Juan Rada-Vilela, Ph.D.
 @see BoundedSum
 @see TNorm
 @see TNormFactory
 @see Norm
 @since 4.0
 */
public class BoundedDifference extends TNorm {

    /**
     Computes the bounded difference between two membership function values

     @param a is a membership function value
     @param b is a membership function value
     @return @f$\max(0, a+b - 1)@f$
     */
    @Override
    public double compute(double a, double b) {
        return Op.max(0.0, a + b - 1);
    }

    @Override
    public BoundedDifference clone() throws CloneNotSupportedException {
        return (BoundedDifference) super.clone();
    }
}
