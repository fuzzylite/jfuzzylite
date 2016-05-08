/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.variable;

import com.fuzzylite.term.Triangle;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

public class VariableTest {

    public VariableTest() {
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

    /**
     * Test of sort method, of class Variable.
     */
    @org.junit.Test
    public void testSort() {
        System.out.println("sort");
        Variable instance = new Variable("Test");
        instance.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        instance.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        instance.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        instance.sort();

        assertThat("DARK is first", instance.getTerm(0).getName(), is("DARK"));
        assertThat("MEDIUM is middle", instance.getTerm(1).getName(), is("MEDIUM"));
        assertThat("BRIGHT is last", instance.getTerm(2).getName(), is("BRIGHT"));
    }

}
