/*
 Copyright (C) 2010-2017 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.examples;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.activation.General;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllImporter;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Ramp;
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
        //Code automatically generated with fuzzylite 6.0.

        Engine engine = new Engine();
        engine.setName("ObstacleAvoidance");
        engine.setDescription("");

        InputVariable obstacle = new InputVariable();
        obstacle.setName("obstacle");
        obstacle.setDescription("");
        obstacle.setEnabled(true);
        obstacle.setRange(0.000, 1.000);
        obstacle.setLockValueInRange(false);
        obstacle.addTerm(new Ramp("left", 1.000, 0.000));
        obstacle.addTerm(new Ramp("right", 0.000, 1.000));
        engine.addInputVariable(obstacle);

        OutputVariable mSteer = new OutputVariable();
        mSteer.setName("mSteer");
        mSteer.setDescription("");
        mSteer.setEnabled(true);
        mSteer.setRange(0.000, 1.000);
        mSteer.setLockValueInRange(false);
        mSteer.setAggregation(new Maximum());
        mSteer.setDefuzzifier(new Centroid(100));
        mSteer.setDefaultValue(Double.NaN);
        mSteer.setLockPreviousValue(false);
        mSteer.addTerm(new Ramp("left", 1.000, 0.000));
        mSteer.addTerm(new Ramp("right", 0.000, 1.000));
        engine.addOutputVariable(mSteer);

        RuleBlock mamdani = new RuleBlock();
        mamdani.setName("mamdani");
        mamdani.setDescription("");
        mamdani.setEnabled(true);
        mamdani.setConjunction(null);
        mamdani.setDisjunction(null);
        mamdani.setImplication(new AlgebraicProduct());
        mamdani.setActivation(new General());
        mamdani.addRule(Rule.parse("if obstacle is left then mSteer is right", engine));
        mamdani.addRule(Rule.parse("if obstacle is right then mSteer is left", engine));
        engine.addRuleBlock(mamdani);


        FuzzyLite.logger().info(new FldExporter().toString(engine));
    }

    //Method 2: Load from a file
    public void method2() {
        Engine engine = null;
        String configurationFile = "/ObstacleAvoidance.fll";
        URL url = SimpleDimmer.class.getResource(configurationFile);
        try {
            engine = new FllImporter().fromFile(new File(url.toURI()));
        } catch (Exception ex) {
            FuzzyLite.logger().log(Level.SEVERE, ex.toString(), ex);
        }

        FuzzyLite.logger().info(new FldExporter().toString(engine));
    }

    public void run() {
        method1();
        method2();
    }

    public static void main(String[] args) {
        new SimpleDimmer().run();
    }

}
