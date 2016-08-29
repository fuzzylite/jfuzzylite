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

import com.fuzzylite.norm.TNorm;

/**
 The HamacherProduct class is a TNorm that computes the Hamacher product of any
 two values.

 @author Juan Rada-Vilela, Ph.D.
 @see HamacherSum
 @see TNorm
 @see TNormFactory
 @see Norm
 @since 4.0
 */
public class HamacherProduct extends TNorm {

    /**
     Computes the Hamacher product of two membership function values

     @param a is a membership function value
     @param b is a membership function value
     @return @f$(a \times b) / (a+b- a \times b)@f$
     */
    @Override
    public double compute(double a, double b) {
        return (a * b) / (a + b - a * b);
    }

    @Override
    public HamacherProduct clone() throws CloneNotSupportedException {
        return (HamacherProduct) super.clone();
    }

}
