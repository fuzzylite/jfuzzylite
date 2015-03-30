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

    public static void updateReference(Term term, Engine engine) {
        if (term instanceof Linear) {
            ((Linear) term).setEngine(engine);
        } else if (term instanceof Function) {
            Function function = (Function) term;
            function.setEngine(engine);
            try {
                function.load();
            } finally {
            }
        }
    }

}
