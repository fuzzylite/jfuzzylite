/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
