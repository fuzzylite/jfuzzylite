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
package com.fuzzylite.imex;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.logging.Level;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;

public class FllImporterTest {

    public FllImporterTest() {
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

    @Test
    public void testLaundry() throws Exception {
        File laundry = new File("../examples/original/mamdani/Laundry.fll");
        assertThat("File exists", laundry.exists(), is(true));
        FllImporter importer = new FllImporter();
        Engine engine = importer.fromFile(laundry);
        FllExporter exporter = new FllExporter();
        FuzzyLite.logger().log(Level.INFO, exporter.toString(engine));
    }

    @Test
    public void testLaundryFromInputStream() throws Exception {
        FllImporter importer = new FllImporter();
        Engine engine = importer.fromStream(getClass().getResourceAsStream("/Laundry.fll"));

        assertThat(engine.getInputVariable("Dirt"), notNullValue());
    }
}
