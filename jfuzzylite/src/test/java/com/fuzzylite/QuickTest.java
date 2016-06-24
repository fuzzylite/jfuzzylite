/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QuickTest {

    public QuickTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        FuzzyLite.setLogging(false);
        FuzzyLite.setDecimals(3);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMessageFormat() {
        Assert.assertThat("can repeat parameter",
                MessageFormat.format("{0}{1}{0}",
                        "a", "b")
                .equals("aba"), is(true));
    }

    @Test
    public void testIncrement() {
        int[] minArray = new int[]{0, 0, 0, 0};
        int[] maxArray = new int[]{10, 0, 10, 0};
        int[] array = Arrays.copyOf(minArray, minArray.length);
        FuzzyLite.logger().log(Level.INFO, "@#@#@#@#@");
        do {
            FuzzyLite.logger().log(Level.INFO, "array {0}", Op.join(array, " "));
        } while (Op.increment(array, minArray, maxArray));
    }

    @Test
    public void testArrayList() {
        List<String> x = new ArrayList<String>(10);
        Assert.assertThat("size is zero", x.size(), is(0));
    }

    @Test
    public void testOpStr() {
        FuzzyLite.setLogging(true);
        FuzzyLite.setDecimals(9);
        FuzzyLite.logger().log(Level.INFO, Op.str(1e-5));
        FuzzyLite.logger().log(Level.INFO, Op.str(1e-5));
    }

    @Test
    public void testOpMinMax() {
        FuzzyLite.setLogging(true);
        Assert.assertThat(Op.min(Double.NaN, 0.0), is(0.0));
        Assert.assertThat(Op.min(0.0, Double.NaN), is(0.0));
        Assert.assertThat(Op.max(Double.NaN, 0.0), is(0.0));
        Assert.assertThat(Op.max(0.0, Double.NaN), is(0.0));

    }

}
