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
package com.fuzzylite.rule;

/**
 The Expression class is the base class to build an expression tree.

 @author Juan Rada-Vilela, Ph.D.
 @see Antecedent
 @see Consequent
 @see Rule
 @since 4.0
 */
public abstract class Expression {

    public enum Type {
        Proposition, Operator
    }

    /**
     Returns the type of the expression

     @return the type of the expression
     */
    public abstract Type type();

    @Override
    public abstract String toString();
}
