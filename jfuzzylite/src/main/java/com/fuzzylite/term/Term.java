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

package com.fuzzylite.term;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;

public abstract class Term implements Op.Cloneable {

    protected String name;
    protected double height;

    public Term() {
        this("");
    }

    public Term(String name) {
        this(name, 1.0);
    }

    public Term(String name, double height) {
        this.name = name;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    public abstract String parameters();

    public abstract void configure(String parameters);

    public abstract double membership(double x);

    @Override
    public Term clone() throws CloneNotSupportedException {
        return (Term) super.clone();
    }

    public void updateReference(Engine engine) {
        //do nothing...
    }

}
