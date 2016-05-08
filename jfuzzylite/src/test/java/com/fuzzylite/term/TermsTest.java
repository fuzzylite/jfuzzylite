/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.term;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;

public class TermsTest {

    public TermsTest() {
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
    public void testTerms() {
        String terms
                = "  term: A Sigmoid 0.500 -20.000\n"
                + "  term: B ZShape 0.000 1.000\n"
                + "  term: C Ramp 1.000 0.000\n"
                + "  term: D Triangle 0.500 1.000 1.500\n"
                + "  term: E Trapezoid 1.000 1.250 1.750 2.000\n"
                + "  term: F Concave 0.850 0.250\n"
                + "  term: G Rectangle 1.750 2.250\n"
                + "  term: H Discrete 2.000 0.000 2.250 1.000 2.500 0.500 2.750 1.000 3.000 0.000\n"
                + "  term: I Gaussian 3.000 0.200\n"
                + "  term: J Cosine 3.250 0.650\n"
                + "  term: K GaussianProduct 3.500 0.100 3.300 0.300\n"
                + "  term: L Spike 3.640 1.040\n"
                + "  term: M Bell 4.000 0.250 3.000\n"
                + "  term: N PiShape 4.000 4.500 4.500 5.000\n"
                + "  term: O Concave 5.650 6.250\n"
                + "  term: P SigmoidDifference 4.750 10.000 30.000 5.250\n"
                + "  term: Q SigmoidProduct 5.250 20.000 -10.000 5.750\n"
                + "  term: R Ramp 5.500 6.500\n"
                + "  term: S SShape 5.500 6.500\n"
                + "  term: T Sigmoid 6.000 20.000";

    }
}
