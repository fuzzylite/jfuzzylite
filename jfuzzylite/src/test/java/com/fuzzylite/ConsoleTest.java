/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.logging.Level;

import static org.hamcrest.CoreMatchers.is;

public class ConsoleTest {

    @ClassRule
    public static TemporaryFolder flFolder = new TemporaryFolder();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    public ConsoleTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        try {
            flFolder.newFolder("mamdani", "matlab");
            flFolder.newFolder("mamdani", "octave");
            flFolder.newFolder("takagi-sugeno", "matlab");
            flFolder.newFolder("takagi-sugeno", "octave");
            flFolder.newFolder("tsukamoto");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
     Test of exportAllExamples method, of class Console.
     */
    @Test
    public void testExportAllExamples() throws Exception {
        exit.expectSystemExitWithStatus(0);

        File examples = new File("../examples/");
        Assert.assertThat("examples is reachable", examples.exists(), is(true));
        FuzzyLite.logger().log(Level.INFO, "Examples: {0}\nOutput: {1}",
                new String[]{examples.getAbsolutePath(), flFolder.getRoot().getAbsolutePath()});
        Assert.assertThat("output folder exists", flFolder.getRoot().exists(), is(true));
        Console.main(new String[]{
                "export-examples", examples.getAbsolutePath(), flFolder.getRoot().getAbsolutePath()});
//        System.in.read();
    }

    @Ignore
    @Test
    public void testBenchmarks() throws Exception {
        File examples = new File("../examples/");
        Assert.assertThat("examples is reachable", examples.exists(), is(true));
        Console.main(new String[]{
                "benchmarks", examples.getAbsolutePath(), "1"});
    }

    /**
     Test of benchmarkExamples method, of class Console.
     */
    @Test
    public void testBenchmarkExamples() {
    }

    /**
     Test of main method, of class Console.
     */
    @Test
    public void testMain() {
    }

}
