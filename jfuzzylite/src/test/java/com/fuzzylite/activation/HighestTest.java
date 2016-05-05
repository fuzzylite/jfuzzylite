/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.activation;

import com.fuzzylite.Console;
import com.fuzzylite.Engine;
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

/**
 *
 * @author juan
 */
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
