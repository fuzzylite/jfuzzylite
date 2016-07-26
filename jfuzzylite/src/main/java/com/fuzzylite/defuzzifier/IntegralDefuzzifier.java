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
package com.fuzzylite.defuzzifier;

public abstract class IntegralDefuzzifier extends Defuzzifier {

    private static int DEFAULT_RESOLUTION = 100;
    private int resolution = DEFAULT_RESOLUTION;

    public IntegralDefuzzifier() {
    }

    public IntegralDefuzzifier(int resolution) {
        this.resolution = resolution;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public static void setDefaultResolution(int defaultResolution) {
        IntegralDefuzzifier.DEFAULT_RESOLUTION = defaultResolution;
    }

    public static int getDefaultResolution() {
        return IntegralDefuzzifier.DEFAULT_RESOLUTION;
    }

    @Override
    public IntegralDefuzzifier clone() throws CloneNotSupportedException {
        return (IntegralDefuzzifier) super.clone();
    }

}
