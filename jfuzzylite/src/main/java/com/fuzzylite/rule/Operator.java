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
 The Operator class is an Expression that represents a non-terminal node in the
 expression tree as a binary operator (i.e., `and` or `or`) on two Expression
 nodes.

 @author Juan Rada-Vilela, Ph.D.
 @see Antecedent
 @see Consequent
 @see Rule
 @since 4.0
 */
public class Operator extends Expression {

    private String name;
    private Expression left, right;

    public Operator() {
        this("");
    }

    public Operator(String name) {
        this(name, null, null);
    }

    public Operator(String name, Expression left, Expression right) {
        this.name = name;
        this.left = left;
        this.right = right;
    }

    /**
     Gets the name of the operator

     @return the name of the operator
     */
    public String getName() {
        return name;
    }

    /**
     Sets the name of the operator

     @param name is the name of the operator
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Gets the left expression in the binary tree

     @return the left expression in the binary tree
     */
    public Expression getLeft() {
        return left;
    }

    /**
     Sets the left expression in the binary tree

     @param left is the left expression in the binary tree
     */
    public void setLeft(Expression left) {
        this.left = left;
    }

    /**
     Gets the right expression in the binary tree

     @return the right expression in the binary tree
     */
    public Expression getRight() {
        return right;
    }

    /**
     Sets the right expression in the binary tree

     @param right is the right expression in the binary tree
     */
    public void setRight(Expression right) {
        this.right = right;
    }

    @Override
    public Type type() {
        return Type.Operator;
    }

    /**
     Returns the name of the operator

     @return the name of the operator
     */
    @Override
    public String toString() {
        return this.name;
    }
}
