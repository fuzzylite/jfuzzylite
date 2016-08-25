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
package com.fuzzylite.activation;

import com.fuzzylite.Console;
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.variable.InputVariable;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class HighestTest {

    public HighestTest() {
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
     * Test of activate method, of class Highest.
     */
    @Test
    public void testActivate() {
        Engine engine = Console.mamdani();
        InputVariable ambient = engine.getInputVariable(0);
        ambient.setValue(0.3);
        RuleBlock ruleBlock = engine.getRuleBlock(0);
        ruleBlock.setActivation(new Highest(1));
        engine.process();

        List<Rule> rules = engine.getRuleBlock(0).getRules();
        Assert.assertThat("First rule was activated",
                rules.get(0).isActivated(), is(true));
        Assert.assertThat("Second rule was not activated",
                rules.get(1).isActivated(), is(false));
        Assert.assertThat("Third rule was not activated",
                rules.get(2).isActivated(), is(false));

        ruleBlock.setActivation(new Highest(2));
        engine.process();
        Assert.assertThat("First rule was activated",
                rules.get(0).isActivated(), is(true));
        Assert.assertThat("Second rule was activated",
                rules.get(1).isActivated(), is(true));
        Assert.assertThat("Third rule was not activated",
                rules.get(2).isActivated(), is(false));
    }

}
