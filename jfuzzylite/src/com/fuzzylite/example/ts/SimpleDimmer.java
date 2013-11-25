package com.fuzzylite.example.ts;

import com.fuzzylite.*;
import com.fuzzylite.defuzzifier.*;
import com.fuzzylite.hedge.*;
import com.fuzzylite.norm.s.*;
import com.fuzzylite.norm.t.*;
import com.fuzzylite.rule.*;
import com.fuzzylite.term.*;
import com.fuzzylite.variable.*;

public class SimpleDimmer extends Engine {

    public SimpleDimmer() {
        initialize();
    }

    private void initialize() {
        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setName("Ambient");
        inputVariable1.setRange(0.000, 1.000);

        inputVariable1.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        inputVariable1.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        inputVariable1.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        addInputVariable(inputVariable1);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setName("Power");
        outputVariable1.setRange(0.000, 1.000);
        outputVariable1.setLockOutputRange(false);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setLockValidOutput(false);
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.getOutput().setAccumulation(new Maximum());

        outputVariable1.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
        outputVariable1.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        outputVariable1.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
        addOutputVariable(outputVariable1);

        RuleBlock ruleblock1 = new RuleBlock();
        ruleblock1.setName("");
        ruleblock1.setConjunction(new Minimum());
        ruleblock1.setDisjunction(new Maximum());
        ruleblock1.setActivation(new Minimum());

        ruleblock1.addRule(Rule.parse("if Ambient is DARK then Power is HIGH", this));
        ruleblock1.addRule(Rule.parse("if Ambient is MEDIUM then Power is MEDIUM", this));
        ruleblock1.addRule(Rule.parse("if Ambient is BRIGHT then Power is LOW", this));
        addRuleBlock(ruleblock1);
    }

}
