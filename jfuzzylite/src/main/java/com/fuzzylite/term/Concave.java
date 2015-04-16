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

import com.fuzzylite.Op;
import java.util.Iterator;
import java.util.List;

public class Concave extends Term {

    private double inflection, end;

    public Concave() {
        this("");
    }

    public Concave(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Concave(String name, double inflection, double end) {
        this(name, inflection, end, 1.0);
    }

    public Concave(String name, double inflection, double end, double height) {
        super(name, height);
        this.inflection = inflection;
        this.end = end;
    }

    @Override
    public String parameters() {
        return Op.join(" ", inflection, end)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 2;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        Iterator<String> it = values.iterator();
        setInflection(Op.toDouble(it.next()));
        setEnd(Op.toDouble(it.next()));
        if (values.size() > required) {
            setHeight(Op.toDouble(it.next()));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (Op.isLE(inflection, end)) { //Concave increasing
            if (Op.isLt(x, end)) {
                return height * (end - inflection) / (2 * end - inflection - x);
            }
        } else { //Concave decreasing
            if (Op.isGt(x, end)) {
                return height * (inflection - end) / (inflection - 2 * end + x);
            }
        }
        return height * 1.0;
    }

    public double getInflection() {
        return inflection;
    }

    public void setInflection(double inflection) {
        this.inflection = inflection;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    @Override
    public Concave clone() throws CloneNotSupportedException {
        return (Concave) super.clone();
    }

}
