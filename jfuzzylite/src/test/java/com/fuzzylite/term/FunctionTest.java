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
package com.fuzzylite.term;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 @author juan */
public class FunctionTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public FunctionTest() {
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

    @Test
    public void testFunctionArithmetic() {
        Function f = new Function();
        String text = "3+4*2/(1-5)^2^3";
        //String formula = "3+4*2/2";
        Assert.assertThat("Postfix is correct", f.toPostfix(text),
                is("3 4 2 * 1 5 - 2 3 ^ ^ / +"));
        Assert.assertThat("Infix from postfix is correct", f.parse(text).toInfix(),
                is("3.000 ^ 2.000 ^ 5.000 - 1.000 / 2.000 * 4.000 + 3.000"));
        Assert.assertThat("Result is 3", Op.str(f.parse(text).evaluate(f.getVariables())),
                is("3.000"));
        f.load(text);
        Assert.assertThat("Result is 3",
                Op.str(f.evaluate()), is("3.000"));
    }

    @Test
    public void testFunctionTrigonometry() {
        Function f = new Function();
        f.getVariables().put("y", 1.0);
        String text = "sin (y*x)^2/x";
        Assert.assertThat("Postfix is correct", f.toPostfix(text), is("y x * sin 2 ^ x /"));
        Assert.assertThat("Prefix is correct", f.parse(text).toPrefix(), is("/ x ^ 2.000 sin * x y"));
        Assert.assertThat("Infix is correct", f.parse(text).toInfix(), is("x / 2.000 ^ x * y sin"));
        //@todo: Fix this:
        Assert.assertThat("Postfix is correct", f.parse(text).toPostfix(), is("x ^ 2.000 sin * x y /"));

        f.load(text);
        Assert.assertThat("Result is 0.708",
                Op.str(f.membership(1)), is("0.708"));
    }

    @Test
    public void testFunctionMissingVariable() {
        Function f = new Function();

        String text = "~5 *4/sin(~pi/2)";
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(containsString("variable <pi> not registered"));
        FuzzyLite.logger().info(Op.str(f.parse(text).evaluate(f.getVariables())));
    }

    @Test
    public void testFunctionNegations() {
        Function f = new Function();
        String text = "~5 *4/sin(~pi/2)";
        f.getVariables().put("pi", Math.PI);
        double result = f.parse(text).evaluate(f.getVariables());

        Assert.assertThat("Result is 20",
                Op.isEq(result, 20.0), is(true));
    }

    @Test
    public void testFunctionNegationsFuture() {
        Function f = new Function();
        String text = "-5 *4/sin(-pi/2)";
        f.getVariables().put("pi", Math.PI);

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(containsString("operator </> has arity <2>, but <1> elements"));

        double result = f.parse(text).evaluate(f.getVariables());
        Assert.assertThat("Result is 20",
                Op.isEq(result, 20.0), is(true));
    }

    @Test
    public void testFunctionText() {
        Function f = new Function();
        String text = "(Temperature is High and Oxigen is Low) or "
                + "(Temperature is Low and (Oxigen is Low or Oxigen is High))";
        Assert.assertThat("Postfix is correct", f.toPostfix(text),
                is("Temperature is High Oxigen is Low and "
                        + "Temperature is Low Oxigen is Low "
                        + "Oxigen is High or and or"));

        text = "term1 is t1 or term2 is t2 and term3 is t3";
        Assert.assertThat("Postfix is correct", f.toPostfix(text),
                is("term1 is t1 term2 is t2 term3 is t3 and or"));
    }

    @Test
    public void testDoubleParenthesisInPostfix() {
        new Function().toPostfix("if ((Ambient is DARK)) then Power is HIGH");
        new Function().toPostfix("if ((((Ambient is DARK)))) then (Power is HIGH)");
    }
}
