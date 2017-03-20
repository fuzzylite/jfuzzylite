/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.variable;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.term.Triangle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.hamcrest.CoreMatchers.is;

public class VariableTest {

    public VariableTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        FuzzyLite.setDecimals(3);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of sort method, of class Variable.
     */
    @org.junit.Test
    public void testSort() {
        System.out.println("sort");
        Variable instance = new Variable("Test");
        instance.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        instance.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        instance.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        instance.sort();

        Assert.assertThat("DARK is first", instance.getTerm(0).getName(), is("DARK"));
        Assert.assertThat("MEDIUM is middle", instance.getTerm(1).getName(), is("MEDIUM"));
        Assert.assertThat("BRIGHT is last", instance.getTerm(2).getName(), is("BRIGHT"));
    }

}
