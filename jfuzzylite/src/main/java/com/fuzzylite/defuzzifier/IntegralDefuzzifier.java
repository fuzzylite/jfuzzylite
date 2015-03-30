/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.defuzzifier;

//TODO: check  http://en.wikipedia.org/wiki/Adaptive_quadrature
public abstract class IntegralDefuzzifier extends Defuzzifier {

    private static int DEFAULT_RESOLUTION = 200;
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
