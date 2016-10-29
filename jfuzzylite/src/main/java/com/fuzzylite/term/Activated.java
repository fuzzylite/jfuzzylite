/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.TNorm;

/**
 The Activated class is a special Term that contains pointers to the necessary
 information of a term that has been activated as part of the Antecedent of a
 Rule. The ownership of the pointers is not transferred to objects of this
 class. The Activated class was named `Thresholded` in versions 4.0 and earlier.

 @author Juan Rada-Vilela, Ph.D.
 @see OutputVariable
 @see Term
 @since 5.0
 */
public class Activated extends Term {

    private Term term;
    private double degree;
    private TNorm implication;

    public Activated() {
        this(null, 1.0, null);
    }

    public Activated(Term term, double degree, TNorm implication) {
        this.term = term;
        this.degree = degree;
        this.implication = implication;
    }

    /**
     Computes the implication of the activation degree and the membership
     function value of @f$x@f$

     @param x is a value
     @return @f$d \otimes \mu(x)@f$, where @f$d@f$ is the activation degree
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (implication == null) {
            throw new RuntimeException(String.format("[implication error] "
                    + "implication operator needed to activate %s",
                    getTerm().toString()));
        }
        return implication.compute(term.membership(x), degree);
    }

    /**
     Returns the parameters of the term

     @return `"degree implication term"`
     */
    @Override
    public String parameters() {
        FllExporter exporter = new FllExporter();
        String result = String.format("%s %s %s", Op.str(degree),
                exporter.toString(implication), exporter.toString(term));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%s)",
                new FllExporter().toString(implication),
                Op.str(degree), term.getName());
    }

    /**
     Does nothing.

     @param parameters are irrelevant
     */
    @Override
    public void configure(String parameters) {
        //do nothing
    }

    /**
     Gets the activated term

     @return the activated term
     */
    public Term getTerm() {
        return term;
    }

    /**
     Sets the activated term

     @param term is the activated term
     */
    public void setTerm(Term term) {
        this.term = term;
    }

    /**
     Gets the activation degree of the term

     @return the activation degree of the term
     */
    public double getDegree() {
        return degree;
    }

    /**
     Sets the activation degree of the term

     @param degree is the activation degree of the term
     */
    public void setDegree(double degree) {
        this.degree = degree;
    }

    /**
     Gets the implication operator

     @return the implication operator
     */
    public TNorm getImplication() {
        return implication;
    }

    /**
     Sets the implication operator

     @param implication is the implication operator
     */
    public void setImplication(TNorm implication) {
        this.implication = implication;
    }

    @Override
    public Activated clone() throws CloneNotSupportedException {
        return (Activated) super.clone();
    }

}
