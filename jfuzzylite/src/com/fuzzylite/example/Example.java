/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.example;

import com.fuzzylite.Engine;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.norm.s.EinsteinSum;
import com.fuzzylite.norm.t.EinsteinProduct;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Gaussian;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.ZShape;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

/**
 *
 * @author jcrada
 */
public class Example {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.setName("Investment-Portfolio");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setName("Age");
        inputVariable1.setRange(20.000, 100.000);
        inputVariable1.addTerm(new ZShape("Young", 30.000, 90.000));
        inputVariable1.addTerm(new SShape("Old", 30.000, 90.000));
        engine.addInputVariable(inputVariable1);

        InputVariable inputVariable2 = new InputVariable();
        inputVariable2.setName("RiskTolerance");
        inputVariable2.setRange(0.000, 10.000);
        inputVariable2.addTerm(new ZShape("Low", 2.000, 8.000));
        inputVariable2.addTerm(new SShape("High", 2.000, 8.000));
        engine.addInputVariable(inputVariable2);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setName("PercentageInStocks");
        outputVariable1.setRange(0.000, 100.000);
        outputVariable1.setLockOutputRange(false);
        outputVariable1.setLockValidOutput(false);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.getOutput().setAccumulation(new EinsteinSum());
        outputVariable1.addTerm(new Gaussian("AboutFifteen", 15.000, 10.000));
        outputVariable1.addTerm(new Gaussian("AboutFifty", 50.000, 10.000));
        outputVariable1.addTerm(new Gaussian("AboutEightyFive", 85.000, 10.000));
        engine.addOutputVariable(outputVariable1);

        RuleBlock ruleBlock1 = new RuleBlock();
        ruleBlock1.setName("");
        ruleBlock1.setConjunction(new EinsteinProduct());
        ruleBlock1.setDisjunction(new EinsteinSum());
        ruleBlock1.setActivation(new EinsteinProduct());
        ruleBlock1.addRule(Rule.parse("if Age is Young or RiskTolerance is High then PercentageInStocks is AboutEightyFive", engine));
        ruleBlock1.addRule(Rule.parse("if Age is Old or RiskTolerance is Low then PercentageInStocks is AboutFifteen", engine));
        ruleBlock1.addRule(Rule.parse("if Age is not extremely Old and RiskTolerance is not extremely Low then PercentageInStocks is AboutFifty with 0.5", engine));
        ruleBlock1.addRule(Rule.parse("if Age is not extremely Young and RiskTolerance is not extremely High then PercentageInStocks is AboutFifty with 0.5", engine));
        engine.addRuleBlock(ruleBlock1);

    }
}
