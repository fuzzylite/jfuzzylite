/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite;

import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.variable.InputVariable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Benchmark {

    public enum TimeUnit {
        NanoSeconds, MicroSeconds, MilliSeconds, Seconds, Minutes, Hours
    }

    public enum TableShape {
        Horizontal, Vertical
    }

    public enum TableContents {
        Header, Body, HeaderAndBody
    }

    private String name;
    private Engine engine;
    private List<double[]> expected;
    private List<double[]> obtained;
    private List<Long> times;
    private double errorThreshold;

    public Benchmark() {
        this("");
    }

    public Benchmark(String name) {
        this(name, null);
    }

    public Benchmark(String name, Engine engine) {
        this(name, engine, 10 * FuzzyLite.getMachEps());
    }

    public Benchmark(String name, Engine engine, double errorThreshold) {
        this.name = name;
        this.engine = engine;
        this.expected = new ArrayList<double[]>();
        this.obtained = new ArrayList<double[]>();
        this.times = new ArrayList<Long>();
        this.errorThreshold = errorThreshold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public List<double[]> getExpected() {
        return expected;
    }

    public void setExpected(List<double[]> expected) {
        this.expected = expected;
    }

    public List<double[]> getObtained() {
        return obtained;
    }

    public void setObtained(List<double[]> obtained) {
        this.obtained = obtained;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public double getErrorThreshold() {
        return errorThreshold;
    }

    public void setErrorThreshold(double errorThreshold) {
        this.errorThreshold = errorThreshold;
    }

    public void prepare(int values, FldExporter.ScopeOfValues scope) {
        int resolution;
        if (scope == FldExporter.ScopeOfValues.AllVariables) {
            resolution = -1 + (int) Math.max(1.0, Math.pow(
                    values, 1.0 / engine.numberOfInputVariables()));
        } else {//if (type == EachVariable
            resolution = values - 1;
        }

        int sampleValues[] = new int[engine.numberOfInputVariables()];
        int minSampleValues[] = new int[engine.numberOfInputVariables()];
        int maxSampleValues[] = new int[engine.numberOfInputVariables()];
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

    public void prepare(Reader reader) throws IOException {
        this.expected = new ArrayList<double[]>();
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
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

    public long runOnce() {
        return run(1)[0];
    }

    public long[] run(int times) {
        long[] runtimes = new long[times];

        final int offset = engine.getInputVariables().size();
        for (int t = 0; t < times; ++t) {
            obtained = new ArrayList<double[]>(expected.size());
            engine.restart();
            long start = System.nanoTime();

            for (int evaluation = 0; evaluation < expected.size(); ++evaluation) {
                double[] expectedValues = expected.get(evaluation);
                double[] obtainedValues = new double[engine.numberOfInputVariables() + engine.numberOfOutputVariables()];

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

                obtained.add(obtainedValues);
            }

            long end = System.nanoTime();
            runtimes[t] = end - start;
        }

        for (long x : runtimes) {
            this.times.add(x);
        }
        return runtimes;
    }

    public void reset() {
        this.obtained.clear();
        this.times.clear();
    }

    public double meanSquareError() {
        if (expected.isEmpty() || obtained.isEmpty()
                || expected.get(0).length != engine.variables().size()) {
            return Double.NaN;
        }
        double[] sumOfSquares = new double[engine.numberOfOutputVariables()];
        for (int i = 0; i < sumOfSquares.length; ++i) {
            sumOfSquares[i] = 0.0;
        }
        final int offset = engine.numberOfInputVariables();
        for (int i = 0; i < expected.size(); ++i) {
            double[] e = expected.get(i);
            double[] o = obtained.get(i);
            for (int y = 0; y < engine.numberOfOutputVariables(); ++y) {
                double squareDifference = (e[offset + y] - o[offset + y])
                        * (e[offset + y] - o[offset + y]);
                if (Op.isFinite(squareDifference)) {
                    sumOfSquares[y] += squareDifference;
                }
            }
        }

        double mse = 0.0;
        for (double x : sumOfSquares) {
            mse += x;
        }
        mse /= (engine.numberOfOutputVariables() * expected.size());
        return mse;
    }

    public int numberOfErrors() {
        if (expected.isEmpty() || obtained.isEmpty()
                || expected.get(0).length != engine.variables().size()) {
            return -1;
        }
        int[] errors = new int[engine.numberOfOutputVariables()];
        for (int i = 0; i < errors.length; ++i) {
            errors[i] = 0;
        }
        final int offset = engine.numberOfInputVariables();
        for (int i = 0; i < expected.size(); ++i) {
            double[] e = expected.get(i);
            double[] o = obtained.get(i);
            for (int y = 0; y < engine.numberOfOutputVariables(); ++y) {
                if (!Op.isEq(e[y + offset], o[y + offset], errorThreshold)) {
                    ++errors[y];
                }
            }
        }

        int result = 0;
        for (double x : errors) {
            result += x;
        }
        return result;
    }

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

    public double convert(double x, TimeUnit from, TimeUnit to) {
        return x * factorOf(to) / factorOf(from);
    }

    public List<Op.Pair<String, String>> results() {
        return results(TimeUnit.NanoSeconds);
    }

    public List<Op.Pair<String, String>> results(TimeUnit timeUnit) {
        return results(timeUnit, true);
    }

    public List<Op.Pair<String, String>> results(TimeUnit timeUnit, boolean includeTimes) {
        double[] runtimes = new double[times.size()];
        for (int i = 0; i < times.size(); ++i) {
            runtimes[i] = times.get(i);
        }
        List<Op.Pair<String, String>> result = new LinkedList<Op.Pair<String, String>>();
        result.add(new Op.Pair<String, String>("library", FuzzyLite.LIBRARY));
        result.add(new Op.Pair<String, String>("name", name));
        result.add(new Op.Pair<String, String>("inputs", String.valueOf(engine.numberOfInputVariables())));
        result.add(new Op.Pair<String, String>("outputs", String.valueOf(engine.numberOfOutputVariables())));
        result.add(new Op.Pair<String, String>("runs", String.valueOf(times.size())));
        result.add(new Op.Pair<String, String>("evaluations", String.valueOf(expected.size())));
        result.add(new Op.Pair<String, String>("errors", String.valueOf(numberOfErrors())));
        result.add(new Op.Pair<String, String>("mse", Op.str(meanSquareError())));
        result.add(new Op.Pair<String, String>("units", timeUnit.name()));

        if (timeUnit == TimeUnit.NanoSeconds) {
            result.add(new Op.Pair<String, String>("sum(t)",
                    String.valueOf((long) Op.sum(runtimes))));
        } else {
            result.add(new Op.Pair<String, String>("sum(t)",
                    String.valueOf(convert(Op.sum(runtimes), TimeUnit.NanoSeconds, timeUnit))));
        }
        result.add(new Op.Pair<String, String>("mean(t)",
                Op.str(convert(Op.mean(runtimes), TimeUnit.NanoSeconds, timeUnit))));
        result.add(new Op.Pair<String, String>("sd(t)",
                Op.str(convert(Op.standardDeviation(runtimes), TimeUnit.NanoSeconds, timeUnit))));

        if (includeTimes) {
            for (int i = 0; i < runtimes.length; ++i) {
                if (timeUnit == TimeUnit.NanoSeconds) {
                    result.add(new Op.Pair<String, String>("t" + (i + 1), String.valueOf((long) runtimes[i])));
                } else {
                    result.add(new Op.Pair<String, String>("t" + (i + 1),
                            Op.str(convert(runtimes[i], TimeUnit.NanoSeconds, timeUnit))));
                }
            }
        }
        return result;
    }

    public String format(List<Op.Pair<String, String>> results){
        return format(results, TableShape.Horizontal);
    }
    
    public String format(List<Op.Pair<String, String>> results, TableShape shape) {
        return format(results, shape, TableContents.HeaderAndBody);
    }
    
    public String format(List<Op.Pair<String, String>> results, TableShape shape,
            TableContents contents) {
        return format(results, shape, contents, "\t");
    }

    public String format(List<Op.Pair<String, String>> results, TableShape shape,
            TableContents contents, String delimiter) {
        StringWriter writer = new StringWriter();

        if (shape == TableShape.Vertical) {
            for (int i = 0; i < results.size(); ++i) {
                Op.Pair<String, String> pair = results.get(i);
                if (contents == TableContents.Header || contents == TableContents.HeaderAndBody) {
                    writer.append(pair.getFirst());
                }
                if (contents == TableContents.HeaderAndBody) {
                    writer.append(delimiter);
                }
                if (contents == TableContents.Body || contents == TableContents.HeaderAndBody) {
                    writer.append(pair.getSecond());
                }
                if (i + 1 < results.size()) {
                    writer.append("\n");
                }
            }

        } else if (shape == TableShape.Horizontal) {
            StringWriter header = new StringWriter();
            StringWriter body = new StringWriter();
            for (int i = 0; i < results.size(); ++i) {
                Op.Pair<String, String> pair = results.get(i);
                if (contents == TableContents.Header || contents == TableContents.HeaderAndBody) {
                    header.append(pair.getFirst());
                    if (i + 1 < results.size()) {
                        header.append(delimiter);
                    }
                }
                if (contents == TableContents.Body || contents == TableContents.HeaderAndBody) {
                    body.append(pair.getSecond());
                    if (i + 1 < results.size()) {
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
