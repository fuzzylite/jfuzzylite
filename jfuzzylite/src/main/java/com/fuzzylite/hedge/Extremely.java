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
package com.fuzzylite.hedge;

import com.fuzzylite.Op;

/**
 The Extremely class is a Hedge located fifth in the ordered set (Not, Seldom,
 Somewhat, Very, Extremely, Any).

 @author Juan Rada-Vilela, Ph.D.
 @see Hedge
 @see HedgeFactory
 @since 4.0
 */
public class Extremely extends Hedge {

    /**
     Computes the hedge for the membership function value @f$x@f$

     @param x is a membership function value
     @return @f$ \begin{cases} 2x^2 & \mbox{if $x \le 0.5$} \cr 1-2(1-x)^2 &
     \mbox{otherwise} \cr \end{cases}@f$
     */
    @Override
    public double hedge(double x) {
        return Op.isLE(x, 0.5)
                ? 2.0 * x * x
                : 1.0 - 2.0 * (1.0 - x) * (1.0 - x);
    }

    @Override
    public Extremely clone() throws CloneNotSupportedException {
        return (Extremely) super.clone();
    }
}
