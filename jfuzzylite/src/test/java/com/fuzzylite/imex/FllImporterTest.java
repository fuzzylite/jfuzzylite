/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.logging.Level;

import static org.hamcrest.CoreMatchers.is;

/**
 *
 * @author juan
 */
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
        Assert.assertThat("File exists", laundry.exists(), is(true));
        FllImporter importer = new FllImporter();
        Engine engine = importer.fromFile(laundry);
        FllExporter exporter = new FllExporter();
        FuzzyLite.logger().log(Level.INFO, exporter.toString(engine));
    }
}
