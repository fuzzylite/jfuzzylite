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
package com.fuzzylite;

import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 The Benchmark class is designed to evaluate the performance of an Engine.

 @author Juan Rada-Vilela, Ph.D.
 @see Engine
 @since 6.0
 */
public class Benchmark {

    /**
     Unit of time to utilize in the results
     */
    public enum TimeUnit {
        NanoSeconds, MicroSeconds, MilliSeconds, Seconds, Minutes, Hours
    }

    /**
     Shape of the table of results
     */
    public enum TableShape {
        Horizontal, Vertical
    }

    /**
     Contents of the table of results
     */
    public enum TableContents {
        Header, Body, HeaderAndBody
    }

    /**
     Type of error between expected and obtained values
     */
    public enum ErrorType {
        NonFinite, Accuracy, All
    }

    private String name;
    private Engine engine;
    private List<double[]> expected;
    private List<double[]> obtained;
    private List<Double> times;
    private double tolerance;

    public Benchmark() {
        this("");
    }

    public Benchmark(String name) {
        this(name, null);
    }

    public Benchmark(String name, Engine engine) {
        this(name, engine, FuzzyLite.getMachEps());
    }

    public Benchmark(String name, Engine engine, double tolerance) {
        this.name = name;
        this.engine = engine;
        this.expected = new ArrayList<double[]>();
        this.obtained = new ArrayList<double[]>();
        this.times = new ArrayList<Double>();
        this.tolerance = tolerance;
    }

    /**
     Gets the name of the benchmark

     @return name is the name of the benchmark
     */
    public String getName() {
        return name;
    }

    /**
     Sets the name of the benchmark

     @param name is the name of the benchmark
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Gets the engine to benchmark

     @return the engine to benchmark
     */
    public Engine getEngine() {
        return engine;
    }

    /**
     Sets the engine to benchmark

     @param engine is the engine to benchmark
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     Gets the set of expected values from the engine, where the inner vector
     contains the input values and output values

     @return the set of expected values from the engine
     */
    public List<double[]> getExpected() {
        return expected;
    }

    /**
     Sets the set of expected values from the engine, where the inner vector
     contains the input values and output values

     @param expected is the set of expected values from the engine
     */
    public void setExpected(List<double[]> expected) {
        this.expected = expected;
    }

    /**
     Gets the set of obtained values from the engine, where the inner vector
     contains the input values and output values

     @return the set of obtained values from the engine
     */
    public List<double[]> getObtained() {
        return obtained;
    }

    /**
     Sets the set of obtained values from the engine, where the inner vector
     contains the input values and output values

     @param obtained is the set of obtained values from the engine
     */
    public void setObtained(List<double[]> obtained) {
        this.obtained = obtained;
    }

    /**
     Gets the vector of nanoseconds taken to produce the set of obtained values
     from the set of expected input values

     @return the vector of nanoseconds taken to produce the set of obtained
     values from the set of expected input values
     */
    public List<Double> getTimes() {
        return times;
    }

    /**
     Sets the vector of nanoseconds taken to produce the set of obtained values
     from the set of expected input values

     @param times is the vector of nanoseconds taken to produce the set of
     obtained values from the set of expected input values
     */
    public void setTimes(List<Double> times) {
        this.times = times;
    }

    /**
     Gets the tolerance above which the difference between an expected and
     obtained value from the engine is considered an error

     @return the tolerance above which the difference between an expected and
     obtained value from the engine is considered an error
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     Sets the tolerance above which the difference between an expected and
     obtained value from the engine is considered an error

     @param tolerance is the tolerance above which the difference between an
     expected and obtained value from the engine is considered an error
     */
    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     Produces and loads into memory the set of expected values from the engine

     @param values is the number of values to evaluate the engine upon
     @param scope is the scope of the values to generate
     @throws RuntimeException if the engine is not set
     */
    public void prepare(int values, FldExporter.ScopeOfValues scope) {
        if (engine == null) {
            throw new RuntimeException("[benchmark error] engine not set before "
                    + "preparing for values and scope");
        }
        int resolution;
        if (scope == FldExporter.ScopeOfValues.AllVariables) {
            resolution = -1 + (int) Math.max(1.0, Math.pow(
                    values, 1.0 / engine.numberOfInputVariables()));
        } else {//if (type == EachVariable
            resolution = values - 1;
        }

        int[] sampleValues = new int[engine.numberOfInputVariables()];
        int[] minSampleValues = new int[engine.numberOfInputVariables()];
        int[] maxSampleValues = new int[engine.numberOfInputVariables()];
        for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
            sampleValues[i] = 0;
            minSampleValues[i] = 0;
            maxSampleValues[i] = resolution;
        }

        this.expected = new ArrayList<double[]>();
        do {
            double[] expectedValues = new double[engine.numberOfInputVariables()];
            for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
                InputVariable inputVariable = engine.getInputVariable(i);
                expectedValues[i] = inputVariable.getMinimum()
                        + sampleValues[i] * inputVariable.range() / Math.max(1, resolution);
            }
            this.expected.add(expectedValues);
        } while (Op.increment(sampleValues, minSampleValues, maxSampleValues));
    }

    /**
     Reads and loads into memory the set of expected values from the engine

     @param reader is the reader of a set of lines containing space-separated
     values
     @throws IOException if the reader cannot be read
     */
    public void prepare(Reader reader) throws IOException {
        prepare(reader, -1);
    }

    /**
     Reads and loads into memory the set of expected values from the engine

     @param reader is the reader of a set of lines containing space-separated
     values
     @param numberOfLines is the maximum number of lines to read from the
     reader, and a value $f@n=(\infty, -1]$f@ reads the entire file.
     @throws IOException if the reader cannot be read
     */
    public void prepare(Reader reader, long numberOfLines) throws IOException {
        this.expected = new ArrayList<double[]>();
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            int lineNumber = 0;
            while (lineNumber != numberOfLines
                    && (line = bufferedReader.readLine()) != null) {
                ++lineNumber;
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }

                double[] expectedValues;
                if (lineNumber == 1) { //automatic detection of header.
                    try {
                        expectedValues = Op.toDoubles(line);
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    expectedValues = Op.toDoubles(line);
                }
                this.expected.add(expectedValues);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            bufferedReader.close();
        }
    }

    /**
     Runs the benchmark on the engine only once

     @return the time in nanoseconds required by the run, which is also appended
     to the times stored in Benchmark::getTimes()
     */
    public double runOnce() {
        return run(1)[0];
    }

    /**
     Runs the benchmark on the engine multiple times

     @param times is the number of times to run the benchmark on the engine
     @return vector of the time in nanoseconds required by each run, which is
     also appended to the times stored in Benchmark::getTimes()
     */
    public double[] run(int times) {
        if (engine == null) {
            throw new RuntimeException("[benchmark error] engine not set for benchmark");
        }

        double[] runtimes = new double[times];
        final int offset = engine.getInputVariables().size();
        for (int t = 0; t < times; ++t) {
            obtained = new ArrayList<double[]>(expected.size());
            for (int i = 0; i < expected.size(); ++i) {
                obtained.add(new double[engine.numberOfInputVariables()
                        + engine.numberOfOutputVariables()]);
            }
            engine.restart();
            long start = System.nanoTime();

            for (int evaluation = 0; evaluation < expected.size(); ++evaluation) {
                double[] expectedValues = expected.get(evaluation);
                double[] obtainedValues = obtained.get(evaluation);

                if (expectedValues.length < engine.getInputVariables().size()) {
                    throw new RuntimeException(MessageFormat.format(
                            "[benchmark error] the number of input values given <{0}> "
                            + "at line <{1}> must be at least the same number of input variables "
                            + "<{2}> in the engine",
                            expectedValues.length, evaluation + 1, engine.numberOfInputVariables()));
                }

                for (int i = 0; i < engine.getInputVariables().size(); ++i) {
                    engine.getInputVariables().get(i).setValue(expectedValues[i]);
                    obtainedValues[i] = expectedValues[i];
                }

                engine.process();

                for (int i = 0; i < engine.getOutputVariables().size(); ++i) {
                    obtainedValues[i + offset] = engine.getOutputVariables().get(i).getValue();
                }
            }

            long end = System.nanoTime();
            runtimes[t] = end - start;
        }

        for (double x : runtimes) {
            this.times.add(x);
        }
        return runtimes;
    }

    /**
     Resets the benchmark to be ready to run again
     */
    public void reset() {
        this.obtained.clear();
        this.times.clear();
    }

    /**
     Indicates whether errors can be computed based on the expected and obtained
     values from the benchmark. If the benchmark was prepared from a file reader
     and the file included columns of expected output values and the benchmark
     has been run at least once, then the benchmark can automatically compute
     the errors and will automatically include them in the results.

     @return whether errors can be computed based on the expected and obtained
     values from the benchmark
     */
    public boolean canComputeErrors() {
        return !(engine == null || expected.isEmpty() || obtained.isEmpty()
                || expected.size() != obtained.size()
                || expected.get(0).length != obtained.get(0).length
                || expected.get(0).length != engine.variables().size());
    }

    /**
     Computes the mean squared error over all output variables considering only
     those cases where there is an accuracy error as defined in
     Benchmark::accuracyErrors().

     @return the mean squared error over all the output variables.
     */
    public double meanSquaredError() {
        return meanSquaredError(null);
    }

    /**
     Computes the mean squared error of the given output variable considering
     only those cases where there is an accuracy error as defined in
     Benchmark::accuracyErrors().

     @param outputVariable is the output variable to compute the errors for
     @return the mean squared error over the given output variable.
     */
    public double meanSquaredError(OutputVariable outputVariable) {
        if (!canComputeErrors()) {
            return Double.NaN;
        }

        double mse = 0.0;
        int errors = 0;
        final int offset = engine.numberOfInputVariables();
        for (int i = 0; i < expected.size(); ++i) {
            double[] e = expected.get(i);
            double[] o = obtained.get(i);
            for (int y = 0; y < engine.numberOfOutputVariables(); ++y) {
                if (outputVariable == null
                        || outputVariable == engine.getOutputVariable(y)) {
                    double difference = e[offset + y] - o[offset + y];
                    if (Op.isFinite(difference)
                            && !Op.isEq(difference, 0.0, tolerance)) {
                        mse += difference * difference;
                        ++errors;
                    }
                }
            }
        }

        if (errors > 0) {
            mse /= errors;
        }
        return mse;
    }

    /**
     Computes the number of errors over all the output variables caused by
     non-finite differences or accuracy differences. An error is counted when
     the difference between the expected and obtained values is not finite, or
     the absolute difference between the expected and obtained values is not
     smaller than the tolerance.

     @return the number of errors over all the output variables caused by
     non-finite differences or accuracy differences
     */
    public int allErrors() {
        return allErrors(null);
    }

    /**
     Computes the number of errors of the given output variable caused by
     non-finite differences or accuracy differences. An error is counted when
     the difference between the expected and obtained values is not finite, or
     the absolute difference between the expected and obtained values is not
     smaller than the tolerance.

     @param outputVariable is the output variable to account the errors for
     @return the number of errors of the given output variable caused by
     non-finite differences or accuracy differences
     */
    public int allErrors(OutputVariable outputVariable) {
        return numberOfErrors(ErrorType.All, outputVariable);
    }

    /**
     Computes the number of errors over all the output variables caused by
     non-finite differences (ie, infinity and NaN). An error is counted when the
     difference between the expected and obtained values is not finite.

     @return the number of errors over all the output variables caused by
     non-finite differences
     */
    public int nonFiniteErrors() {
        return nonFiniteErrors(null);
    }

    /**
     Computes the number of errors of the given output variable caused by
     non-finite differences (ie, infinity and NaN). An error is counted when the
     difference between the expected and obtained values is not finite.

     @param outputVariable is the output variable to account the errors for
     @return the number of errors of the given output variable caused by
     non-finite differences
     */
    public int nonFiniteErrors(OutputVariable outputVariable) {
        return numberOfErrors(ErrorType.NonFinite, outputVariable);
    }

    /**
     Computes the number of errors over all the output variables caused by a
     significant difference in accuracy. An error is counted when the absolute
     difference between the expected and obtained values is not smaller than the
     tolerance.

     @f$\text{E} = \sum_y \sum_i \epsilon_i^y, \text{where } \epsilon_i^y =
     \begin{cases} 0 & \text{if} |e_i^y - o^y_i| < \theta\\ 1 & \text{otherwise}
     \end{cases} @f$, @f$y@f$ is the set of output variable s , @f$e@f$ is the
     set of expected output values, @f$o@f$ is the set of obtained output
     values, and @f$\theta@f$ is the tolerance

     @return the number of errors over all the output variables caused by a
     significant difference in accuracy
     */
    public int accuracyErrors() {
        return accuracyErrors(null);
    }

    /**
     Computes the number of errors over the given output variable caused by a
     significant difference in accuracy. An error is counted when the absolute
     difference between the expected and obtained values is not smaller than the
     tolerance.

     @f$\text{E} = \sum_i \epsilon_i, \text{where } \epsilon_i = \begin{cases} 0
     & \text{if} |e_i - o_i| < \theta\\ 1 & \text{otherwise} \end{cases} @f$,
     @f$e@f$ is the set of expected output values,
     @f$o@f$ is the set of obtained output values, and @f$\theta@f$ is the
     tolerance

     @param outputVariable is the output variable to account the errors for
     @return the number of errors of the given output variable caused by a
     significant difference in accuracy
     */
    public int accuracyErrors(OutputVariable outputVariable) {
        return numberOfErrors(ErrorType.Accuracy, outputVariable);
    }

    /**
     Computes the number of errors of the given type over all the output
     variables.

     @param errorType is type of error to account for
     @return the number of errors over all the output variables
     */
    public int numberOfErrors(ErrorType errorType) {
        return numberOfErrors(errorType, null);
    }

    /**
     Computes the number of errors of the given type over the given output
     variable.

     @param errorType is type of error to account for
     @param outputVariable is output variable to account the errors for
     @return the number of errors over the given output variable
     */
    public int numberOfErrors(ErrorType errorType, OutputVariable outputVariable) {
        if (!canComputeErrors()) {
            return -1;
        }
        int errors = 0;
        final int offset = engine.numberOfInputVariables();
        for (int i = 0; i < expected.size(); ++i) {
            double[] e = expected.get(i);
            double[] o = obtained.get(i);
            for (int y = 0; y < engine.numberOfOutputVariables(); ++y) {
                if (outputVariable == null
                        || outputVariable == engine.getOutputVariable(y)) {

                    if (!Op.isEq(e[offset + y], o[offset + y], tolerance)) {
                        double difference = e[offset + y] - o[offset + y];
                        if (errorType == ErrorType.Accuracy && Op.isFinite(difference)) {
                            ++errors;
                        } else if (errorType == ErrorType.NonFinite && !Op.isFinite(difference)) {
                            ++errors;
                        } else if (errorType == ErrorType.All) {
                            ++errors;
                        }
                    }
                }
            }
        }
        return errors;
    }

    /**
     Returns the factor of the given unit from NanoSeconds

     @param unit is the unit of time
     @return the factor of the given unit from NanoSeconds
     */
    public double factorOf(TimeUnit unit) {
        if (unit == TimeUnit.NanoSeconds) {
            return 1.0;
        } else if (unit == TimeUnit.MicroSeconds) {
            return 1.0e-3;
        } else if (unit == TimeUnit.MilliSeconds) {
            return 1.0e-6;
        } else if (unit == TimeUnit.Seconds) {
            return 1.0e-9;
        } else if (unit == TimeUnit.Minutes) {
            return 1.0e-9 / 60;
        } else if (unit == TimeUnit.Hours) {
            return 1.0e-9 / 3600;
        }
        return Double.NaN;
    }

    /**
     Converts the time to different scales

     @param time is the time to convert
     @param from is the units of the time to convert from
     @param to is the units of the time to convert to
     @return the time in the units specified
     */
    public double convert(double time, TimeUnit from, TimeUnit to) {
        return time * factorOf(to) / factorOf(from);
    }

    /**
     Returns the header of a horizontal table of results

     @param runs is the number of times the benchmark will be run, hence
     producing the relevant number of columns for each run
     @param includeErrors indicates whether to include columns for computing the
     errors
     @return the header of a horizontal table of results
     */
    public Set<String> header(int runs, boolean includeErrors) {
        Benchmark result = new Benchmark();

        Engine dummy = new Engine();
        dummy.addOutputVariable(new OutputVariable());
        result.setEngine(dummy);

        Double[] dummyTimes = new Double[runs];
        Arrays.fill(dummyTimes, Double.NaN);
        result.setTimes(Arrays.asList(dummyTimes));

        if (includeErrors) {
            double[] dummyArray = new double[1];
            List<double[]> dummyList = new ArrayList<double[]>();
            dummyList.add(dummyArray);
            result.setExpected(dummyList);
            result.setObtained(dummyList);
        }

        return result.results().keySet();
    }

    /**
     Computes and returns the results from the benchmark aggregating the
     statistics of all the output variables

     @return the results from the benchmark
     */
    public Map<String, String> results() {
        return results(TimeUnit.NanoSeconds);
    }

    /**
     Computes and returns the results from the benchmark aggregating the
     statistics of all the output variables

     @param timeUnit is the unit of time of the results
     @return the results from the benchmark
     */
    public Map<String, String> results(TimeUnit timeUnit) {
        return results(timeUnit, true);
    }

    /**
     Computes and returns the results from the benchmark aggregating the
     statistics of all the output variables

     @param timeUnit is the unit of time of the results
     @param includeTimes indicates whether to include the times of each run
     @return the results from the benchmark
     */
    public Map<String, String> results(TimeUnit timeUnit, boolean includeTimes) {
        return results(null, timeUnit, includeTimes);
    }

    /**
     Computes and returns the results from the benchmark for the given output
     variable

     @param outputVariable is the output variable to compute the statistics for
     @return the results from the benchmark
     */
    public Map<String, String> results(OutputVariable outputVariable) {
        return results(outputVariable, TimeUnit.NanoSeconds);
    }

    /**
     Computes and returns the results from the benchmark for the given output
     variable

     @param outputVariable is the output variable to compute the statistics for
     @param timeUnit is the unit of time of the results
     @return the results from the benchmark
     */
    public Map<String, String> results(OutputVariable outputVariable, TimeUnit timeUnit) {
        return results(outputVariable, timeUnit, true);
    }

    /**
     Computes and returns the results from the benchmark for the given output
     variable

     @param outputVariable is the output variable to compute the statistics for
     @param timeUnit is the unit of time of the results
     @param includeTimes indicates whether to include the times of each run
     @return the results from the benchmark
     */
    public Map<String, String> results(OutputVariable outputVariable, TimeUnit timeUnit, boolean includeTimes) {
        if (engine == null) {
            throw new RuntimeException("[benchmark error] engine not set for benchmark");
        }

        double[] runtimes = new double[times.size()];
        for (int i = 0; i < times.size(); ++i) {
            runtimes[i] = times.get(i);
        }

        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("library", FuzzyLite.LIBRARY);
        result.put("name", name);
        result.put("inputs", String.valueOf(engine.numberOfInputVariables()));
        result.put("outputs", String.valueOf(engine.numberOfOutputVariables()));
        result.put("ruleBlocks", String.valueOf(engine.numberOfRuleBlocks()));
        int rules = 0;
        for (RuleBlock ruleBlock : engine.getRuleBlocks()) {
            rules += ruleBlock.numberOfRules();
        }
        result.put("rules", String.valueOf(rules));
        result.put("runs", String.valueOf(times.size()));
        result.put("evaluations", String.valueOf(expected.size()));
        if (canComputeErrors()) {
            List<String> names = new LinkedList<String>();
            double meanRange = 0.0;
            double rmse = Math.sqrt(meanSquaredError());
            double nrmse = 0.0;
            double weights = 0.0;
            for (OutputVariable y : engine.getOutputVariables()) {
                if (outputVariable == null || outputVariable == y) {
                    names.add(y.getName());
                    meanRange += y.range();
                    nrmse += Math.sqrt(meanSquaredError(y)) * 1.0 / y.range();
                    weights += 1.0 / y.range();
                }
            }
            meanRange /= names.size();
            nrmse /= weights;

            result.put("outputVariable", Op.join(names, ","));
            result.put("range", Op.str(meanRange));

            result.put("tolerance", String.format("%6.3e", getTolerance()));
            result.put("errors", String.valueOf(allErrors(outputVariable)));

            result.put("nfErrors", String.valueOf(nonFiniteErrors(outputVariable)));
            result.put("accErrors", String.valueOf(accuracyErrors(outputVariable)));

            result.put("rmse", String.format("%6.6e", rmse));
            result.put("nrmse", String.format("%6.6e", nrmse));
        }
        result.put("units", timeUnit.name().toLowerCase());

        result.put("sum(t)", String.valueOf(
                convert(Op.sum(runtimes), TimeUnit.NanoSeconds, timeUnit)));
        result.put("mean(t)", String.valueOf(
                convert(Op.mean(runtimes), TimeUnit.NanoSeconds, timeUnit)));
        result.put("sd(t)", String.valueOf(
                convert(Op.standardDeviation(runtimes), TimeUnit.NanoSeconds, timeUnit)));

        if (includeTimes) {
            for (int i = 0; i < runtimes.length; ++i) {
                result.put("t" + (i + 1), String.valueOf(
                        convert(runtimes[i], TimeUnit.NanoSeconds, timeUnit)));
            }
        }
        return result;
    }

    /**
     Formats the results

     @param results is the vector of results
     @return the formatted results from the benchmark
     */
    public String format(Map<String, String> results) {
        return format(results, TableShape.Horizontal);
    }

    /**
     Formats the results

     @param results is the vector of results
     @param shape is the shape to present the table of results
     @return the formatted results from the benchmark
     */
    public String format(Map<String, String> results, TableShape shape) {
        return format(results, shape, TableContents.HeaderAndBody);
    }

    /**
     Formats the results

     @param results is the vector of results
     @param shape is the shape to present the table of results
     @param contents indicates the information to include in the table of
     results
     @return the formatted results from the benchmark
     */
    public String format(Map<String, String> results, TableShape shape,
            TableContents contents) {
        return format(results, shape, contents, "\t");
    }

    /**
     Formats the results

     @param results is the vector of results
     @param shape is the shape to present the table of results
     @param contents indicates the information to include in the table of
     results
     @param delimiter is the delimiter of the table of results
     @return the formatted results from the benchmark
     */
    public String format(Map<String, String> results, TableShape shape,
            TableContents contents, String delimiter) {
        StringWriter writer = new StringWriter();

        if (shape == TableShape.Vertical) {
            Iterator<Map.Entry<String, String>> it = results.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                if (contents == TableContents.Header || contents == TableContents.HeaderAndBody) {
                    writer.append(pair.getKey());
                }
                if (contents == TableContents.HeaderAndBody) {
                    writer.append(delimiter);
                }
                if (contents == TableContents.Body || contents == TableContents.HeaderAndBody) {
                    writer.append(pair.getValue());
                }
                if (it.hasNext()) {
                    writer.append("\n");
                }
            }

        } else if (shape == TableShape.Horizontal) {
            StringWriter header = new StringWriter();
            StringWriter body = new StringWriter();
            Iterator<Map.Entry<String, String>> it = results.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                if (contents == TableContents.Header || contents == TableContents.HeaderAndBody) {
                    header.append(pair.getKey());
                    if (it.hasNext()) {
                        header.append(delimiter);
                    }
                }
                if (contents == TableContents.Body || contents == TableContents.HeaderAndBody) {
                    body.append(pair.getValue());
                    if (it.hasNext()) {
                        body.append(delimiter);
                    }
                }
            }
            if (contents == TableContents.Header || contents == TableContents.HeaderAndBody) {
                writer.append(header.toString());
            }
            if (contents == TableContents.HeaderAndBody) {
                writer.append("\n");
            }
            if (contents == TableContents.Body || contents == TableContents.HeaderAndBody) {
                writer.append(body.toString());
            }
        }

        return writer.toString();
    }
}
