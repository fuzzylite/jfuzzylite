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
package com.fuzzylite.activation;

import com.fuzzylite.Console;
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.variable.InputVariable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class LowestTest {

    public LowestTest() {
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
     * Test of activate method, of class Lowest.
     */
    @Test
    public void testActivate() {
        Engine engine = Console.mamdani();
        InputVariable ambient = engine.getInputVariable(0);
        ambient.setValue(0.3);
        RuleBlock ruleBlock = engine.getRuleBlock(0);
        ruleBlock.setActivation(new Lowest(1));
        System.out.println(engine.toString());
        engine.process();

        List<Rule> rules = engine.getRuleBlock(0).getRules();
        Assert.assertThat("First rule was not fired",
                rules.get(0).isFired(), is(false));
        Assert.assertThat("Second rule was fired",
                rules.get(1).isFired(), is(true));
        Assert.assertThat("Third rule was not fired",
                rules.get(2).isFired(), is(false));

        ruleBlock.setActivation(new Lowest(2));
        engine.process();
        Assert.assertThat("First rule was fired",
                rules.get(0).isFired(), is(true));
        Assert.assertThat("Second rule was fired",
                rules.get(1).isFired(), is(true));
        Assert.assertThat("Third rule was not fired",
                rules.get(2).isFired(), is(false));
    }

}
