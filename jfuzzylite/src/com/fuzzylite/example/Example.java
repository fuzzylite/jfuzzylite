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
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Bell;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Gaussian;
import com.fuzzylite.term.GaussianProduct;
import com.fuzzylite.term.PiShape;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.Rectangle;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.SigmoidDifference;
import com.fuzzylite.term.SigmoidProduct;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
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
        engine.setName("qtfuzzylite");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setName("AllInputTerms");
        inputVariable1.setRange(0.000, 6.500);
        inputVariable1.addTerm(new Sigmoid("A", 0.500, -20.000));
        inputVariable1.addTerm(new ZShape("B", 0.000, 1.000));
        inputVariable1.addTerm(new Ramp("C", 1.000, 0.000));
        inputVariable1.addTerm(new Triangle("D", 0.500, 1.000, 1.500));
        inputVariable1.addTerm(new Trapezoid("E", 1.000, 1.250, 1.750, 2.000));
        inputVariable1.addTerm(new Rectangle("F", 1.750, 2.250));
        inputVariable1.addTerm(Discrete.create("G", 2.000, 0.000, 2.250, 1.000, 2.500, 0.500, 2.750, 1.000, 3.000, 0.000));
        inputVariable1.addTerm(new Gaussian("H", 3.000, 0.200));
        inputVariable1.addTerm(new GaussianProduct("I", 3.500, 0.100, 3.300, 0.300));
        inputVariable1.addTerm(new Bell("J", 4.000, 0.250, 3.000));
        inputVariable1.addTerm(new PiShape("K", 4.000, 4.500, 4.500, 5.000));
        inputVariable1.addTerm(new SigmoidDifference("L", 4.750, 10.000, 30.000, 5.250));
        inputVariable1.addTerm(new SigmoidProduct("M", 5.250, 20.000, -10.000, 5.750));
        inputVariable1.addTerm(new Ramp("N", 5.500, 6.500));
        inputVariable1.addTerm(new SShape("O", 5.500, 6.500));
        inputVariable1.addTerm(new Sigmoid("P", 6.000, 20.000));
        engine.addInputVariable(inputVariable1);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setName("AllOutputTerms");
        outputVariable1.setRange(0.000, 6.500);
        outputVariable1.setLockOutputRange(false);
        outputVariable1.setLockValidOutput(false);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.getOutput().setAccumulation(new Maximum());
        outputVariable1.addTerm(new Sigmoid("A", 0.500, -20.000));
        outputVariable1.addTerm(new ZShape("B", 0.000, 1.000));
        outputVariable1.addTerm(new Ramp("C", 1.000, 0.000));
        outputVariable1.addTerm(new Triangle("D", 0.500, 1.000, 1.500));
        outputVariable1.addTerm(new Trapezoid("E", 1.000, 1.250, 1.750, 2.000));
        outputVariable1.addTerm(new Rectangle("F", 1.750, 2.250));
        outputVariable1.addTerm(Discrete.create("G", 2.000, 0.000, 2.250, 1.000, 2.500, 0.500, 2.750, 1.000, 3.000, 0.000));
        outputVariable1.addTerm(new Gaussian("H", 3.000, 0.200));
        outputVariable1.addTerm(new GaussianProduct("I", 3.500, 0.100, 3.300, 0.300));
        outputVariable1.addTerm(new Bell("J", 4.000, 0.250, 3.000));
        outputVariable1.addTerm(new PiShape("K", 4.000, 4.500, 4.500, 5.000));
        outputVariable1.addTerm(new SigmoidDifference("L", 4.750, 10.000, 30.000, 5.250));
        outputVariable1.addTerm(new SigmoidProduct("M", 5.250, 20.000, -10.000, 5.750));
        outputVariable1.addTerm(new Ramp("N", 5.500, 6.500));
        outputVariable1.addTerm(new SShape("O", 5.500, 6.500));
        outputVariable1.addTerm(new Sigmoid("P", 6.000, 20.000));
        engine.addOutputVariable(outputVariable1);

        RuleBlock ruleBlock1 = new RuleBlock();
        ruleBlock1.setName("");
        ruleBlock1.setConjunction(new Minimum());
        ruleBlock1.setDisjunction(new Maximum());
        ruleBlock1.setActivation(new Minimum());
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is A then AllOutputTerms is P", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is B then AllOutputTerms is O", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is C then AllOutputTerms is N", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is D then AllOutputTerms is M", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is E then AllOutputTerms is L", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is F then AllOutputTerms is K", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is G then AllOutputTerms is J", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is H then AllOutputTerms is I", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is I then AllOutputTerms is H", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is J then AllOutputTerms is G", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is K then AllOutputTerms is F", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is L then AllOutputTerms is E", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is M then AllOutputTerms is D", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is N then AllOutputTerms is C", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is O then AllOutputTerms is B", engine));
        ruleBlock1.addRule(Rule.parse("if AllInputTerms is P then AllOutputTerms is A", engine));
        engine.addRuleBlock(ruleBlock1);

    }
}
