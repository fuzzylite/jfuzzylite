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

import com.fuzzylite.Op;
import com.fuzzylite.norm.SNorm;

/**
 The Maximum class is an SNorm that computes the maximum of any two values.

 @author Juan Rada-Vilela, Ph.D.
 @see Minimum
 @see SNorm
 @see SNormFactory
 @see Norm
 @since 4.0
 */
public class Maximum extends SNorm {

    /**
     Computes the maximum of two membership function values

     @param a is a membership function value
     @param b is a membership function value
     @return @f$\max(a,b)@f$
     */
    @Override
    public double compute(double a, double b) {
        return Op.max(a, b);
    }

    @Override
    public Maximum clone() throws CloneNotSupportedException {
        return (Maximum) super.clone();
    }
}
