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

import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllImporter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author juan
 */
public class BenchmarkTest {

    public BenchmarkTest() {
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

    @Ignore
    @Test
    public void testBenchmarksFromConsole() {
        Console.main(new String[]{"benchmarks", "../examples/", "1"});
    }

    @Test
    public void testBenchmarksFromFiles() throws IOException {
        String path = "../examples/";
        List<String> examples = new ArrayList<String>();
        examples.add("mamdani/AllTerms");
        examples.add("mamdani/SimpleDimmer");
        examples.add("mamdani/matlab/mam21");
        examples.add("mamdani/matlab/mam22");
        examples.add("mamdani/matlab/shower");
        examples.add("mamdani/matlab/tank");
        examples.add("mamdani/matlab/tank2");
        examples.add("mamdani/matlab/tipper");
        examples.add("mamdani/matlab/tipper1");
        examples.add("mamdani/octave/investment_portfolio");
        examples.add("mamdani/octave/mamdani_tip_calculator");
        examples.add("takagi-sugeno/approximation");
        examples.add("takagi-sugeno/SimpleDimmer");
        examples.add("takagi-sugeno/matlab/fpeaks");
        examples.add("takagi-sugeno/matlab/invkine1");
        examples.add("takagi-sugeno/matlab/invkine2");
        examples.add("takagi-sugeno/matlab/juggler");
        examples.add("takagi-sugeno/matlab/membrn1");
        examples.add("takagi-sugeno/matlab/membrn2");
        examples.add("takagi-sugeno/matlab/slbb");
        examples.add("takagi-sugeno/matlab/slcp");
        examples.add("takagi-sugeno/matlab/slcp1");
        examples.add("takagi-sugeno/matlab/slcpp1");
        examples.add("takagi-sugeno/matlab/sltbu_fl");
        examples.add("takagi-sugeno/matlab/sugeno1");
        examples.add("takagi-sugeno/matlab/tanksg");
        examples.add("takagi-sugeno/matlab/tippersg");
        examples.add("takagi-sugeno/octave/cubic_approximator");
        examples.add("takagi-sugeno/octave/heart_disease_risk");
        examples.add("takagi-sugeno/octave/linear_tip_calculator");
        examples.add("takagi-sugeno/octave/sugeno_tip_calculator");
        examples.add("tsukamoto/tsukamoto");

        StringBuilder writer = new StringBuilder();
        int errors[] = new int[examples.size()];
        for (int i = 0; i < examples.size(); ++i) {
            String example = examples.get(i);

            FuzzyLite.logger().log(Level.INFO, "Benchmark {0}/{1}: {2}",
                    new Object[]{i + 1, examples.size(), example});
            Engine engine = new FllImporter().fromFile(new File(path, example + ".fll"));

            Benchmark benchmark = new Benchmark(example, engine, FuzzyLite.getMachEps());
            benchmark.prepare(new FileReader(new File(path, example + ".fld")), 1024);

            benchmark.run(1);
            if (i == 0) {
                writer.append("\n")
                        .append(benchmark.format(benchmark.results(),
                                Benchmark.TableShape.Horizontal, Benchmark.TableContents.HeaderAndBody))
                        .append("\n");
            } else {
                writer.append(benchmark.format(benchmark.results(),
                        Benchmark.TableShape.Horizontal, Benchmark.TableContents.Body))
                        .append("\n");
            }
            errors[i] = benchmark.accuracyErrors();
        }
        FuzzyLite.logger().info(writer.toString());

        for (int i = 0; i < examples.size(); ++i) {
            Assert.assertThat("benchmark " + examples.get(i) + " has no errors",
                    errors[i], is(0));
        }

    }

    @Test
    public void testSimpleDimmer() {
        Engine engine = Console.mamdani();
        Benchmark benchmark = new Benchmark(engine.getName(), engine);
        benchmark.prepare(1024, FldExporter.ScopeOfValues.AllVariables);
        benchmark.run(100);

        FuzzyLite.logger().info(benchmark.format(benchmark.results(),
                Benchmark.TableShape.Vertical, Benchmark.TableContents.HeaderAndBody,
                ": "));
    }

    @Test
    public void testHeaders() {
        Benchmark benchmark = new Benchmark();
        Assert.assertThat(benchmark.header(10, true).size(), is(30));
        Assert.assertThat(benchmark.header(10, false).size(), is(30 - 8));
    }
}
