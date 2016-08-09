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
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

public class FldExporter extends Exporter {

    public enum ScopeOfValues {
        EachVariable, AllVariables
    }

    private String separator;
    private boolean exportHeaders;
    private boolean exportInputValues;
    private boolean exportOutputValues;

    public FldExporter() {
        this(" ");
    }

    public FldExporter(String separator) {
        this.separator = separator;
        this.exportHeaders = true;
        this.exportInputValues = true;
        this.exportOutputValues = true;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public boolean exportsHeaders() {
        return exportHeaders;
    }

    public void setExportHeaders(boolean exportHeaders) {
        this.exportHeaders = exportHeaders;
    }

    public boolean exportsInputValues() {
        return exportInputValues;
    }

    public void setExportInputValues(boolean exportInputValues) {
        this.exportInputValues = exportInputValues;
    }

    public boolean exportsOutputValues() {
        return exportOutputValues;
    }

    public void setExportOutputValues(boolean exportOutputValues) {
        this.exportOutputValues = exportOutputValues;
    }

    public String header(Engine engine) {
        List<String> result = new LinkedList<String>();
        if (exportInputValues) {
            for (InputVariable inputVariable : engine.getInputVariables()) {
                result.add(inputVariable.getName());
            }
        }
        if (exportOutputValues) {
            for (OutputVariable outputVariable : engine.getOutputVariables()) {
                result.add(outputVariable.getName());
            }
        }
        return Op.join(result, separator);
    }

    @Override
    public String toString(Engine engine) {
        return toString(engine, 1024);
    }

    public String toString(Engine engine, int values) {
        return toString(engine, values, ScopeOfValues.AllVariables);
    }

    public String toString(Engine engine, int values, ScopeOfValues scope) {
        return toString(engine, values, scope, engine.getInputVariables());
    }

    public String toString(Engine engine, int values, ScopeOfValues scope,
            List<InputVariable> activeVariables) {
        StringWriter writer = new StringWriter();
        try {
            write(engine, writer, values, scope, activeVariables);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("[exporter error] an exception occurred while exporting the results: " + ex);
        }
        return writer.toString();
    }

    public String toString(Engine engine, Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                ++lineNumber;
                line = line.trim();
                if (!line.isEmpty() && line.charAt(0) == '#') {
                    continue;
                }
                List<Double> inputValues;
                if (lineNumber == 1) { //automatic detection of header.
                    try {
                        inputValues = parse(line);
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    inputValues = parse(line);
                }
                write(engine, writer, inputValues, engine.getInputVariables());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            bufferedReader.close();
        }
        return writer.toString();
    }

    public void toFile(File file, Engine engine, int values, ScopeOfValues scope) throws IOException {
        toFile(file, engine, values, scope, engine.getInputVariables());
    }

    public void toFile(File file, Engine engine, int values, ScopeOfValues scope,
            List<InputVariable> activeVariables) throws IOException {
        if (!file.createNewFile()) {
            FuzzyLite.logger().log(Level.FINE, "Replacing file {0}", file.getAbsolutePath());
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), FuzzyLite.UTF_8));
        try {
            write(engine, writer, values, scope, activeVariables);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            writer.close();
        }
    }

    public void toFile(File file, Engine engine, Reader reader) throws IOException {
        if (!file.createNewFile()) {
            FuzzyLite.logger().log(Level.FINE, "Replacing file {0}", file.getAbsolutePath());
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), FuzzyLite.UTF_8));
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null) {
                ++lineNumber;
                line = line.trim();
                if (!line.isEmpty() && line.charAt(0) == '#') {
                    continue; //comments are ignored, blank lines are retained
                }
                List<Double> inputValues;
                if (lineNumber == 1) { //automatic detection of header.
                    try {
                        inputValues = parse(line);
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    inputValues = parse(line);
                }
                write(engine, writer, inputValues, engine.getInputVariables());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            bufferedReader.close();
            writer.close();
        }
    }

    public List<Double> parse(String values) {
        List<Double> inputValues = new ArrayList<Double>();
        if (!(values.isEmpty() || values.charAt(0) == '#')) {
            StringTokenizer tokenizer = new StringTokenizer(values);
            while (tokenizer.hasMoreTokens()) {
                inputValues.add(Op.toDouble(tokenizer.nextToken()));
            }
        }
        return inputValues;
    }

    public void write(Engine engine, Writer writer, int values, ScopeOfValues scope)
            throws IOException {
        write(engine, writer, values, scope, engine.getInputVariables());
    }

    public void write(Engine engine, Writer writer, int values, ScopeOfValues scope,
            List<InputVariable> activeVariables)
            throws IOException {
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }
        if (activeVariables.size() != engine.getInputVariables().size()) {
            throw new RuntimeException("[exporter error] number of active variables "
                    + "<" + activeVariables.size() + "> "
                    + "must match the number of input variables in the engine "
                    + "<" + engine.getInputVariables().size() + ">");
        }

        int resolution;
        if (scope == ScopeOfValues.AllVariables) {
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
            if (engine.getInputVariable(i) == activeVariables.get(i)) {
                maxSampleValues[i] = resolution;
            } else {
                maxSampleValues[i] = 0;
            }
        }

        do {
            List<Double> inputValues = new ArrayList<Double>(engine.numberOfInputVariables());
            for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
                InputVariable inputVariable = engine.getInputVariable(i);
                if (inputVariable == activeVariables.get(i)) {
                    inputValues.add(inputVariable.getMinimum()
                            + sampleValues[i] * inputVariable.range() / Math.max(1, resolution));
                } else {
                    inputValues.add(inputVariable.getValue());
                }
            }
            write(engine, writer, inputValues, activeVariables);
        } while (Op.increment(sampleValues, minSampleValues, maxSampleValues));
    }

    public void write(Engine engine, Writer writer, Reader reader) throws IOException {
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }

        String line;
        int lineNumber = 0;
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            while ((line = bufferedReader.readLine()) != null) {
                ++lineNumber;
                List<Double> inputValues;
                if (lineNumber == 1) { //automatic detection of header.
                    try {
                        inputValues = parse(line);
                    } catch (Exception ex) {
                        continue;
                    }
                } else {
                    inputValues = parse(line);
                }
                write(engine, writer, inputValues, engine.getInputVariables());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            bufferedReader.close();
        }
    }

    public void write(Engine engine, Writer writer, List<Double> inputValues,
            List<InputVariable> activeVariables) throws IOException {
        if (inputValues.isEmpty()) {
            writer.append("\n");
            return;
        }

        if (inputValues.size() < engine.numberOfInputVariables()) {
            throw new RuntimeException(String.format(
                    "[export error] engine has <%d> input variables, "
                    + "but input data provides <%d> values",
                    engine.numberOfInputVariables(), inputValues.size()));
        }

        if (activeVariables.size() != engine.getInputVariables().size()) {
            throw new RuntimeException(String.format(
                    "[exporter error] number of active variables <%d> "
                    + "must match the number of input variables in the engine <%d>",
                    activeVariables.size(), engine.getInputVariables().size()));
        }

        List<Double> values = new ArrayList<Double>();
        for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
            InputVariable inputVariable = engine.getInputVariable(i);
            double inputValue;
            if (inputVariable == activeVariables.get(i)) {
                inputValue = inputValues.get(i);
            } else {
                inputValue = inputVariable.getValue();
            }
            inputVariable.setValue(inputValue);
            if (exportInputValues) {
                values.add(inputValue);
            }
        }

        engine.process();

        for (int i = 0; i < engine.numberOfOutputVariables(); ++i) {
            OutputVariable outputVariable = engine.getOutputVariable(i);
            if (exportOutputValues) {
                values.add(outputVariable.getValue());
            }
        }
        writer.append(Op.join(values, separator)).append("\n");
    }

    @Override
    public FldExporter clone() throws CloneNotSupportedException {
        return (FldExporter) super.clone();
    }

}
