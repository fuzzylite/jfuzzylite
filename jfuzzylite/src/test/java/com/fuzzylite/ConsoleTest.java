/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite;

import java.io.File;
import java.util.logging.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author juan
 */
public class ConsoleTest {

    @ClassRule
    public static TemporaryFolder flFolder = new TemporaryFolder();

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
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of exportAllExamples method, of class Console.
     */
    @Test
    public void testExportAllExamples() throws Exception {
        File examples = new File("../examples/original/");
        Assert.assertThat("examples is reachable", examples.exists(), is(true));
        FuzzyLite.logger().log(Level.INFO, "Examples: {0}\nOutput: {1}",
                new String[]{examples.getAbsolutePath(), flFolder.getRoot().getAbsolutePath()});
        Assert.assertThat("output folder exists", flFolder.getRoot().exists(), is(true));
        Console.main(new String[]{
            "export-examples", examples.getAbsolutePath(), flFolder.getRoot().getAbsolutePath()});
    }

    /**
     * Test of benchmarkExamples method, of class Console.
     */
    @Test
    public void testBenchmarkExamples() {
    }

    /**
     * Test of main method, of class Console.
     */
    @Test
    public void testMain() {
    }

}
