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
package com.fuzzylite.defuzzifier;

/**
 The IntegralDefuzzifier class is the base class for defuzzifiers which
 integrate over the fuzzy set.

 @author Juan Rada-Vilela, Ph.D.
 @since 4.0
 */
public abstract class IntegralDefuzzifier extends Defuzzifier {

    private static int defaultResolution = 100;
    private int resolution = defaultResolution;

    public IntegralDefuzzifier() {
    }

    public IntegralDefuzzifier(int resolution) {
        this.resolution = resolution;
    }

    /**
     Gets the resolution of the defuzzifier. The resolution refers to the number
     of divisions in which the range `[minimum,maximum]` is divided in order to
     integrate the area under the curve

     @return the resolution of the defuzzifier
     */
    public int getResolution() {
        return resolution;
    }

    /**
     Sets the resolution of the defuzzifier. The resolution refers to the number
     of divisions in which the range `[minimum,maximum]` is divided in order to
     integrate the area under the curve

     @param resolution is the resolution of the defuzzifier
     */
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    /**
     Sets the default resolution for integral-based defuzzifiers

     @param defaultResolution is the default resolution for integral-based
     defuzzifiers
     */
    public static void setDefaultResolution(int defaultResolution) {
        IntegralDefuzzifier.defaultResolution = defaultResolution;
    }

    /**
     Gets the default resolution for integral-based defuzzifiers

     @return the default resolution for integral-based defuzzifiers
     */
    public static int getDefaultResolution() {
        return IntegralDefuzzifier.defaultResolution;
    }

    @Override
    public IntegralDefuzzifier clone() throws CloneNotSupportedException {
        return (IntegralDefuzzifier) super.clone();
    }

}
