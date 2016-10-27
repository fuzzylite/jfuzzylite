/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author juan
 */
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
