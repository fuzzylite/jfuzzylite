
package com.fuzzylite.example.ts;

import com.fuzzylite.Engine;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.hedge.Any;
import com.fuzzylite.hedge.Extremely;
import com.fuzzylite.hedge.Not;
import com.fuzzylite.hedge.Seldom;
import com.fuzzylite.hedge.Somewhat;
import com.fuzzylite.hedge.Very;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

public class SimpleDimmer {

    public static void main(String args[]) {
//        /* Setup engine */
//        Engine engine = new Engine("simple-dimmer");
//        engine.addHedge(new Any());
//        engine.addHedge(new Extremely());
//        engine.addHedge(new Not());
//        engine.addHedge(new Seldom());
//        engine.addHedge(new Somewhat());
//        engine.addHedge(new Very());
//
//        /* Setup input variables */
//        InputVariable inputVariable1 = new InputVariable("Ambient", 0.0, 1.0);
//        inputVariable1.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
//        inputVariable1.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
//        inputVariable1.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
//        engine.addInputVariable(inputVariable1);
//
//        /* Setup output variables */
//        OutputVariable outputVariable1 = new OutputVariable("Power", 0.0, 1.0);
//        outputVariable1.setLockOutputRange(false);
//        outputVariable1.setDefaultValue(Double.NaN);
//        outputVariable1.setLockValidOutput(false);
//        outputVariable1.setDefuzzifier(new WeightedAverage());
//        outputVariable1.getOutput().setAccumulation(null);
//
//        outputVariable1.addTerm(new Constant("LOW", 0.250));
//        outputVariable1.addTerm(new Constant("MEDIUM", 0.500));
//        outputVariable1.addTerm(new Constant("HIGH", 0.750));
//        engine.addOutputVariable(outputVariable1);
//
//        RuleBlock ruleblock1 = new RuleBlock(new Minimum(), new Maximum(), new Minimum());
//
////		ruleblock1.addRule(engine.parse("if Ambient is DARK then Power is HIGH")).
////		           addRule(engine.parse("if Ambient is MEDIUM then Power is MEDIUM")).
////		           addRule(engine.parse("if Ambient is BRIGHT then Power is LOW"));
//        engine.addRuleBlock(ruleblock1);

    }
}
