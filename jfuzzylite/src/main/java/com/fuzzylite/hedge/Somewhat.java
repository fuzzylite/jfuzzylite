/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */

package com.fuzzylite.hedge;

public class Somewhat extends Hedge {

    @Override
    public double hedge(double x) {
        return Math.sqrt(x);
    }

    @Override
    public Somewhat clone() throws CloneNotSupportedException {
        return (Somewhat) super.clone();
    }

}
