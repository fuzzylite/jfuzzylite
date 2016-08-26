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

import com.fuzzylite.Op;

/**
 The Seldom class is a Hedge located second in the ordered set (Not, Seldom,
 Somewhat, Very, Extremely, Any).

 @author Juan Rada-Vilela, Ph.D.
 @see Hedge
 @see HedgeFactory
 @since 4.0
 */
public class Seldom extends Hedge {

    /**
     Computes the hedge for the membership function value @f$x@f$

     @param x is a membership function value
     @return @f$ \begin{cases} \sqrt{0.5x} & \mbox{if $x \le 0.5$} \cr
     1-\sqrt{0.5(1-x)} & \mbox{otherwise}\cr \end{cases}
     @f$
     */
    @Override
    public double hedge(double x) {
        return Op.isLE(x, 0.5)
                ? Math.sqrt(x / 2.0)
                : 1.0 - Math.sqrt((1.0 - x) / 2.0);
    }

    @Override
    public Seldom clone() throws CloneNotSupportedException {
        return (Seldom) super.clone();
    }
}
