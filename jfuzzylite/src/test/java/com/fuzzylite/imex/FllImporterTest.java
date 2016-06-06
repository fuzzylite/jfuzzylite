/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import java.io.File;
import java.util.logging.Level;
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
