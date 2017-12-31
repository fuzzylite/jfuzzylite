package com.fuzzylite.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fuzzylite.Console;
import com.fuzzylite.Engine;

public class RuleWeightTest {

    private static final int NEW_RULE_INDEX = 3;
    private static String RULE_TEXT = "if ambient is DARK then power is HIGH";



    @Test
    public void testAddRuleWithWeightUsingParse() throws Exception {
        Engine engine = Console.mamdani();

        RuleBlock ruleBlock = engine.getRuleBlock(0);
        ruleBlock.addRule(Rule.parse(RULE_TEXT + " with 0.5", engine));

        Assert.assertThat(ruleBlock.getRule(NEW_RULE_INDEX).getText(), startsWith(RULE_TEXT));
        Assert.assertThat(ruleBlock.getRule(NEW_RULE_INDEX).getWeight(), is(0.5));
    }

    @Test
    public void testAddRuleWithWeightInRule() throws Exception {
        Engine engine = Console.mamdani();

        RuleBlock ruleBlock = engine.getRuleBlock(0);

        Rule rule = new Rule(RULE_TEXT, 0.5);

        ruleBlock.addRule(rule);
        rule.load(engine);

        Assert.assertThat(ruleBlock.getRule(NEW_RULE_INDEX).getText(), startsWith(RULE_TEXT));
        Assert.assertThat(ruleBlock.getRule(NEW_RULE_INDEX).getWeight(), is(0.5));
    }

    @Test
    public void testAddRuleWithWeightInRuleText() throws Exception {
        Engine engine = Console.mamdani();

        RuleBlock ruleBlock = engine.getRuleBlock(0);

        Rule rule = new Rule(RULE_TEXT + " with 0.5");

        ruleBlock.addRule(rule);
        rule.load(engine);

        Assert.assertThat(ruleBlock.getRule(NEW_RULE_INDEX).getText(), startsWith(RULE_TEXT));
        Assert.assertThat(ruleBlock.getRule(NEW_RULE_INDEX).getWeight(), is(0.5));

    }

    @Test
    public void testRuleChangeWeight() throws Exception {
        Engine engine = Console.mamdani();

        RuleBlock ruleBlock = engine.getRuleBlock(0);

        ruleBlock.getRule(0).setWeight(0.3);

        Assert.assertThat(ruleBlock.getRule(0).getWeight(), is(0.3));
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
}
