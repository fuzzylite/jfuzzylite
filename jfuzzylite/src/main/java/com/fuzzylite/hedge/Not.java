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
package com.fuzzylite.hedge;

/**
 The Not class is a Hedge located first in the ordered set (Not, Seldom,
 Somewhat, Very, Extremely, Any).

 @author Juan Rada-Vilela, Ph.D.
 @see Hedge
 @see HedgeFactory
 @since 4.0
 */
public class Not extends Hedge {

    /**
     Computes the hedge for the membership function value @f$x@f$

     @param x is a membership function value
     @return @f$1-x@f$
     */
    @Override
    public double hedge(double x) {
        return 1.0 - x;
    }

    @Override
    public Not clone() throws CloneNotSupportedException {
        return (Not) super.clone();
    }
}
