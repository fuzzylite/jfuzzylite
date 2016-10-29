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

import com.fuzzylite.Op;

/**
 The Norm class is the abstract class for norms.

 @author Juan Rada-Vilela, Ph.D.
 @see TNorm
 @see SNorm
 @see TNormFactory
 @see SNormFactory
 @since 4.0
 */
public abstract class Norm implements Op.Cloneable {

    /**
     Computes the norm for @f$a@f$ and @f$b@f$

     @param a is a membership function value
     @param b is a membership function value
     @return the norm between @f$a@f$ and @f$b@f$
     */
    public abstract double compute(double a, double b);

    /**
     Creates a clone of the norm

     @return a clone of the norm
     */
    @Override
    public Norm clone() throws CloneNotSupportedException {
        return (Norm) super.clone();
    }
}
