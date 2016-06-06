/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        String path = "../examples/original/";
        List<Op.Pair<String, Integer>> examples = new ArrayList<Op.Pair<String, Integer>>();
        examples.add(new Op.Pair<String, Integer>("mamdani/AllTerms", (int) 1e4));
        examples.add(new Op.Pair<String, Integer>("mamdani/SimpleDimmer", (int) 1e5));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/mam21", 128));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/mam22", 128));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/shower", 256));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/tank", 256));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/tank2", 512));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/tipper", 256));
        examples.add(new Op.Pair<String, Integer>("mamdani/matlab/tipper1", (int) 1e5));
        examples.add(new Op.Pair<String, Integer>("mamdani/octave/investment_portfolio", 256));
        examples.add(new Op.Pair<String, Integer>("mamdani/octave/mamdani_tip_calculator", 256));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/approximation", (int) 1e6));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/SimpleDimmer", (int) 2e6));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/fpeaks", 512));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/invkine1", 256));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/invkine2", 256));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/juggler", 512));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/membrn1", 1024));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/membrn2", 512));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/slbb", 20));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/slcp", 20));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/slcp1", 15));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/slcpp1", 9));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/sltbu_fl", 128));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/sugeno1", (int) 2e6));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/tanksg", 1024));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/matlab/tippersg", 1024));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/octave/cubic_approximator", (int) 2e6));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/octave/heart_disease_risk", 1024));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/octave/linear_tip_calculator", 1024));
        examples.add(new Op.Pair<String, Integer>("takagi-sugeno/octave/sugeno_tip_calculator", 512));
        examples.add(new Op.Pair<String, Integer>("tsukamoto/tsukamoto", (int) 1e6));
        
        StringBuilder writer = new StringBuilder();
        for (int i = 0; i < examples.size(); ++i) {
            Op.Pair<String, Integer> example = examples.get(i);
            
            FuzzyLite.logger().log(Level.INFO, "Benchmark {0}/{1}: {2} ({3} values)",
                    new Object[]{i + 1, examples.size(), example.getFirst(), example.getSecond()});
            Engine engine = new FllImporter().fromFile(new File(path + example.getFirst() + ".fll"));
            
            Benchmark benchmark = new Benchmark(example.getFirst(), engine);
//            benchmark.prepare(example.getSecond(), FldExporter.ScopeOfValues.AllVariables);
            benchmark.prepare(new FileReader(new File(path + "../" + example.getFirst() + ".fld")));
            
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
            
            Assert.assertThat("benchmark has no errors", benchmark.numberOfErrors(), is(0));
        }
        FuzzyLite.logger().info(writer.toString());
    }
    
    @Test
    public void testSimpleDimmer(){
        Engine engine = Console.mamdani();
        Benchmark benchmark = new Benchmark(engine.getName(), engine);
        benchmark.prepare(1024, FldExporter.ScopeOfValues.AllVariables);
        benchmark.run(100);
        
        FuzzyLite.logger().info(benchmark.format(benchmark.results(),
                Benchmark.TableShape.Vertical, Benchmark.TableContents.HeaderAndBody,
                ": "));
    }
}
