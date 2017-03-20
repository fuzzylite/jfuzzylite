/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2017 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.factory;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.term.Function;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class FunctionFactoryTest {

    public FunctionFactoryTest() {
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
    public void testFunctionsCanBeCalled() throws Exception {
        FuzzyLite.setLogging(true);
        FunctionFactory ff = FactoryManager.instance().function();
        double x = 0.5, y = 1.0;
        List<String> functions = new ArrayList<String>(ff.availableFunctions());
        Collections.sort(functions);
        for (String function : functions) {
            Function.Element element = ff.getObject(function);
            Object result;
            if (element.getArity() == 1) {
                result = element.getMethod().invoke(Op.class, x);
                FuzzyLite.logger().log(Level.INFO, "{0}(x={1})={2}",
                        new Object[]{function, x, result});
            } else if (element.getArity() == 2) {
                result = element.getMethod().invoke(Op.class, x, y);
                FuzzyLite.logger().log(Level.INFO, "{0}(x={1}, y={2})={3}",
                        new Object[]{function, x, y, result});
            } else {
                throw new RuntimeException("Only unary and binary methods are allowed");
            }
        }
    }

    @Test
    public void testOperatorsCanBeCalled() throws Exception {
        FuzzyLite.setLogging(true);
        FunctionFactory ff = FactoryManager.instance().function();
        double x = 0.5, y = 1.0;
        List<String> operators = new ArrayList<String>(ff.availableOperators());
        Collections.sort(operators);
        for (String function : operators) {
            Function.Element element = ff.getObject(function);
            Object result;
            if (element.getArity() == 1) {
                result = element.getMethod().invoke(Op.class, x);
                FuzzyLite.logger().log(Level.INFO, "{0}(x={1})={2}",
                        new Object[]{function, x, result});
            } else if (element.getArity() == 2) {
                result = element.getMethod().invoke(Op.class, x, y);
                FuzzyLite.logger().log(Level.INFO, "{0}(x={1}, y={2})={3}",
                        new Object[]{function, x, y, result});
            } else {
                throw new RuntimeException("Only unary and binary methods are allowed");
            }

        }
    }
}
