/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.examples;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllImporter;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;

public class SimpleDimmer {

    public SimpleDimmer() {

    }

    //Method 1: Set up the engine manually.
    public void method1() {
        Engine engine = new Engine();
        engine.setName("simple-dimmer");

        InputVariable inputVariable = new InputVariable();
        inputVariable.setEnabled(true);
        inputVariable.setName("Ambient");
        inputVariable.setRange(0.000, 1.000);
        inputVariable.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        inputVariable.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        inputVariable.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        engine.addInputVariable(inputVariable);

        OutputVariable outputVariable = new OutputVariable();
        outputVariable.setEnabled(true);
        outputVariable.setName("Power");
        outputVariable.setRange(0.000, 1.000);
        outputVariable.fuzzyOutput().setAccumulation(new Maximum());
        outputVariable.setDefuzzifier(new Centroid(200));
        outputVariable.setDefaultValue(Double.NaN);
        outputVariable.setLockPreviousOutputValue(false);
        outputVariable.setLockOutputValueInRange(false);
        outputVariable.addTerm(new Triangle("LOW", 0.000, 0.250, 0.500));
        outputVariable.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        outputVariable.addTerm(new Triangle("HIGH", 0.500, 0.750, 1.000));
        engine.addOutputVariable(outputVariable);

        RuleBlock ruleBlock = new RuleBlock();
        ruleBlock.setEnabled(true);
        ruleBlock.setName("");
        ruleBlock.setConjunction(null);
        ruleBlock.setDisjunction(null);
        ruleBlock.setActivation(new Minimum());
        ruleBlock.addRule(Rule.parse("if Ambient is DARK then Power is HIGH", engine));
        ruleBlock.addRule(Rule.parse("if Ambient is MEDIUM then Power is MEDIUM", engine));
        ruleBlock.addRule(Rule.parse("if Ambient is BRIGHT then Power is LOW", engine));
        engine.addRuleBlock(ruleBlock);

        FuzzyLite.logger().info(new FldExporter().toString(engine));
    }
    
    //Method 2: Load from a file
    public void method2(){
        Engine engine = null;
        String configurationFile = "/SimpleDimmer.fll";
        URL url = SimpleDimmer.class.getResource(configurationFile);
        try {
            engine = new FllImporter().fromFile(new File(url.toURI()));
        } catch (Exception ex) {
            FuzzyLite.logger().log(Level.SEVERE, ex.toString(), ex);
        }
        
        FuzzyLite.logger().info(new FldExporter().toString(engine));
    }
    
    public void run(){
        method1();
        method2();
    }

    public static void main(String[] args) {
        new SimpleDimmer().run();
    }

}
