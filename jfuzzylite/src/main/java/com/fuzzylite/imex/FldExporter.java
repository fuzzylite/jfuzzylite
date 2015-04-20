/*
 Author: Juan Rada-Vilela, Ph.D.
 Copyright (C) 2010-2014 FuzzyLite Limited
 All rights reserved

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 jfuzzylite is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with jfuzzylite.  If not, see <http://www.gnu.org/licenses/>.

 fuzzylite™ is a trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class FldExporter extends Exporter {

    public static final int DEFAULT_MAXIMUM = 1024;
    public static final String DEFAULT_SEPARATOR = " ";

    private String separator;
    private boolean exportHeaders;
    private boolean exportInputValues;
    private boolean exportOutputValues;

    public FldExporter() {
        this(DEFAULT_SEPARATOR);
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
                if (inputVariable.isEnabled()) {
                    result.add("@InputVariable: " + inputVariable.getName() + ";");
                }
            }
        }
        if (exportOutputValues) {
            for (OutputVariable outputVariable : engine.getOutputVariables()) {
                if (outputVariable.isEnabled()) {
                    result.add("@OutputVariable: " + outputVariable.getName() + ";");
                }
            }
        }
        return String.format("#@Engine: %s;\n#", engine.getName()) + Op.join(result, separator);
    }

    @Override
    public String toString(Engine engine) {
        return toString(engine, DEFAULT_MAXIMUM);
    }

    public String toString(Engine engine, int maximumNumberOfResults) {
        StringWriter writer = new StringWriter();
        try {
            write(engine, writer, maximumNumberOfResults);
        } catch (Exception ex) {
            throw new RuntimeException("[exporter error] an exception occurred while exporting the results", ex);
        }
        return writer.toString();
    }

    public void toFile(File file, Engine engine, int maximumNumberOfResults) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        write(engine, writer, maximumNumberOfResults);
        writer.close();
    }

    public String toString(Engine engine, String inputData) {
        StringWriter writer = new StringWriter();
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }
        BufferedReader reader = new BufferedReader(new StringReader(inputData));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && line.charAt(0) == '#') {
                    continue;
                }
                List<Double> inputValues = parse(line);
                write(engine, writer, inputValues);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return writer.toString();
    }

    public void toFile(File file, Engine engine, String inputData) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && line.charAt(0) == '#') {
                continue;
            }
            List<Double> inputValues = parse(line);
            write(engine, writer, inputValues);
            writer.flush();
        }
        reader.close();
        writer.close();
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

    public void write(Engine engine, Writer writer, int maximumNumberOfResults)
            throws IOException {
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }
        int resolution = -1 + (int) Math.max(1.0, Math.pow(
                maximumNumberOfResults, 1.0 / engine.numberOfInputVariables()));

        int sampleValues[] = new int[engine.numberOfInputVariables()];
        int minSampleValues[] = new int[engine.numberOfInputVariables()];
        int maxSampleValues[] = new int[engine.numberOfInputVariables()];
        for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
            sampleValues[i] = 0;
            minSampleValues[i] = 0;
            maxSampleValues[i] = resolution;
        }

        engine.restart();

        boolean overflow = false;
        while (!overflow) {
            List<Double> inputValues = new ArrayList<Double>(engine.numberOfInputVariables());
            for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
                InputVariable inputVariable = engine.getInputVariable(i);
                inputValues.add(inputVariable.getMinimum()
                        + sampleValues[i] * inputVariable.range() / Math.max(1.0, resolution));
            }
            write(engine, writer, inputValues);
            overflow = Op.increment(sampleValues, minSampleValues, maxSampleValues);
        }
    }

    public void write(Engine engine, Writer writer, Reader reader) throws IOException {
        if (exportHeaders) {
            writer.append(header(engine)).append("\n");
        }

        engine.restart();

        String line;
        int lineNumber = 0;
        BufferedReader bufferedReader = new BufferedReader(reader);
        while ((line = bufferedReader.readLine()) != null) {
            ++lineNumber;
            List<Double> inputValues = parse(line.trim());
            try {
                write(engine, writer, inputValues);
            } catch (IOException ex) {
                throw new IOException(String.format("IOException writing line <%d>", lineNumber), ex);
            }
        }
    }

    public void write(Engine engine, Writer writer, List<Double> inputValues) throws IOException {
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

        List<Double> values = new ArrayList<Double>();
        for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
            InputVariable inputVariable = engine.getInputVariable(i);
            double inputValue = inputVariable.isEnabled() ? inputValues.get(i) : Double.NaN;
            inputVariable.setInputValue(inputValue);
            if (exportInputValues) {
                values.add(inputValue);
            }
        }

        engine.process();

        for (int i = 0; i < engine.numberOfOutputVariables(); ++i) {
            OutputVariable outputVariable = engine.getOutputVariable(i);
            outputVariable.defuzzify();
            if (exportOutputValues) {
                values.add(outputVariable.getOutputValue());
            }
        }
        writer.append(Op.join(values, separator)).append("\n");
    }

    @Override
    public FldExporter clone() throws CloneNotSupportedException {
        return (FldExporter) super.clone();
    }

}
