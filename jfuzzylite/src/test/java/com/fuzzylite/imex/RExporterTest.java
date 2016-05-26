/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.imex;

import com.fuzzylite.Console;
import com.fuzzylite.Engine;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author juan
 */
public class RExporterTest {

    public RExporterTest() {
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
    public void quickTest() {
        Engine mamdani = Console.mamdani();
        RScriptExporter exporter = new RScriptExporter();
//        exporter.toString(mamdani, 
//                mamdani.getInputVariable(0),
//                mamdani.getInputVariable(1));
    }
}
