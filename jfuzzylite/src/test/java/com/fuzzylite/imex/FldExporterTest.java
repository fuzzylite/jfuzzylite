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

import com.fuzzylite.Console;
import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.variable.InputVariable;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author juan
 */
public class FldExporterTest {

    public FldExporterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        FuzzyLite.setDecimals(3);
        FuzzyLite.setLogging(true);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSimpleDimmer() {
        Engine engine = Console.mamdani();
        engine.addInputVariable(new InputVariable("Dummy2", 0, 1));
        engine.addInputVariable(new InputVariable("Dummy3", 0, 1));
        engine.addInputVariable(new InputVariable("Dummy4", 0, 1));

        FldExporter exporter = new FldExporter("\t");
        exporter.setExportHeaders(false);

        int valuesEachVariable = 3;
        int expectedValues = (int) Math.pow(valuesEachVariable, engine.numberOfInputVariables());

        String eachVariable = exporter.toString(engine, valuesEachVariable, FldExporter.ScopeOfValues.EachVariable);
        Assert.assertThat("expected values are obtained by each variable",
                eachVariable.split("\n").length, is(expectedValues));

        String allVariables = exporter.toString(engine, expectedValues, FldExporter.ScopeOfValues.AllVariables);
        Assert.assertThat("expected values are obtained by all variables",
                allVariables.split("\n").length, is(expectedValues));
    }

}
