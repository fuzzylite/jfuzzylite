/*
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
 */
package com.fuzzylite.term;

import com.fuzzylite.Op;
import java.util.List;

public class Ramp extends Term {

    protected double start, end;

    public Ramp() {
        this("");
    }

    public Ramp(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Ramp(String name, double start, double end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    @Override
    public String parameters() {
        return Op.join(" ", start, end) 
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
        setStart(Op.toDouble(values.get(0)));
        setEnd(Op.toDouble(values.get(1)));
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }

        if (Op.isEq(start, end)) {
            return 0.0;
        }

        if (Op.isLt(start, end)) {
            if (Op.isLE(x, start)) {
                return 0.0;
            }
            if (Op.isGE(x, end)) {
                return 1.0;
            }
            return (x - start) / (end - start);
        } else {
            if (Op.isGE(x, start)) {
                return 0.0;
            }
            if (Op.isLE(x, end)) {
                return 1.0;
            }
            return (start - x) / (start - end);
        }
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

}
