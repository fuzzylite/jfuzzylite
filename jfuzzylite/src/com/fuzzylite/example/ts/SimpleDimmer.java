package com.fuzzylite.example.ts;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.util.logging.Level;

public class SimpleDimmer {

    public static void main(String args[]) {
        Engine engine = new Engine("simple-dimmer");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setName("Ambient");
        inputVariable1.setRange(0.000, 1.000);

        inputVariable1.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        inputVariable1.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        inputVariable1.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        engine.addInputVariable(inputVariable1);

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
        engine.addOutputVariable(outputVariable1);

        RuleBlock ruleblock1 = new RuleBlock();
        ruleblock1.setName("");
        ruleblock1.setConjunction(new Minimum());
        ruleblock1.setDisjunction(new Maximum());
        ruleblock1.setActivation(new Minimum());

        ruleblock1.addRule(Rule.parse("if Ambient is DARK then Power is HIGH", engine));
        ruleblock1.addRule(Rule.parse("if Ambient is MEDIUM then Power is MEDIUM", engine));
        ruleblock1.addRule(Rule.parse("if Ambient is BRIGHT then Power is LOW", engine));
        engine.addRuleBlock(ruleblock1);

        StringBuilder message = new StringBuilder();
        if (!engine.isReady(message)) {
            throw new RuntimeException(String.format(
                    "[engine error] engine is not ready "
                    + "due to the following errors %s", message.toString()));
        }

        FuzzyLite.logger().setLevel(Level.FINEST);
        
        FuzzyLite.logger().info(String.format("%s %s; %s %s",
                str(outputVariable1.getMinimum()),
                str(outputVariable1.getMaximum()),
                str(outputVariable1.getOutput().getMinimum()),
                str(outputVariable1.getOutput().getMaximum())));
        
        double range = inputVariable1.getMaximum() - inputVariable1.getMinimum();
        for (int i = 0; i < 50; ++i) {
            double light = inputVariable1.getMinimum() + i * (range / 50);
            inputVariable1.setInputValue(light);
            engine.process();
            FuzzyLite.logger().info(String.format(
                    "Ambient.input = %s -> Power.output = %s",
                    Op.str(light), Op.str(outputVariable1.defuzzify())));
        }
    }
}
