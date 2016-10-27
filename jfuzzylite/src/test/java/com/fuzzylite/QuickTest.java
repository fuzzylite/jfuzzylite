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
package com.fuzzylite;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import static org.hamcrest.CoreMatchers.is;

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

    @Test
    public void testOpStrInMultipleThreads() {
        FuzzyLite.setLogging(true);
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            final int decimals = i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    FuzzyLite.setDecimals(decimals);
                    DecimalFormat formatter = FuzzyLite.getFormatter();
                    Random random = new Random(decimals);

                    for (int times = 0; times < 10; times++) {
                        try {
                            Thread.sleep(200);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        double example = (random.nextInt() % 100) / 100.0;
                        FuzzyLite.logger().info(String.format(
                                "I am printing %s decimals like this: %s",
                                decimals, Op.str(example)));
                        String obtained = Op.str(example);
                        int obtainedDecimals
                                = obtained.substring(
                                        1 + obtained.lastIndexOf(
                                                formatter.getDecimalFormatSymbols().getDecimalSeparator()))
                                .length();

                        Assert.assertThat(obtainedDecimals, is(decimals));
                    }
                }
            };
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }

}
