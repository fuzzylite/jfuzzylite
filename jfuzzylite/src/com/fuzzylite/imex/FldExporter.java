/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author jcrada
 */
public class FldExporter extends Exporter {

    public static final int DEFAULT_MAXIMUM = 1024;
    public static final String DEFAULT_SEPARATOR = " ";

    protected String separator;

    public FldExporter() {
        this(DEFAULT_SEPARATOR);
    }

    public FldExporter(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String header(Engine engine) {
        String inputsHeader = headerInputVariables(engine.getInputVariables());
        String outputsHeader = headerOutputVariables(engine.getOutputVariables());
        StringBuilder result = new StringBuilder();
        result.append(String.format("@Engine: %s;", engine.getName()));
        if (!inputsHeader.isEmpty()) {
            result.append(separator).append(inputsHeader);
        }
        if (!outputsHeader.isEmpty()) {
            result.append(separator).append(outputsHeader);
        }
        return result.toString();
    }

    public String headerInputVariables(List<InputVariable> inputVariables) {
        List<String> result = new ArrayList<>();
        for (InputVariable inputVariable : inputVariables) {
            if (inputVariable.isEnabled()) {
                result.add("@InputVariable: " + inputVariable.getName() + ";");
            }
        }
        return Op.join(result, separator);
    }

    public String headerOutputVariables(List<OutputVariable> outputVariables) {
        List<String> result = new ArrayList<>();
        for (OutputVariable outputVariable : outputVariables) {
            if (outputVariable.isEnabled()) {
                result.add("@OutputVariable: " + outputVariable.getName() + ";");
            }
        }
        return Op.join(result, separator);
    }

    @Override
    public String toString(Engine engine) {
        return toString(engine, DEFAULT_MAXIMUM);
    }

    public String toString(Engine engine, int maximumNumberOfResults) {
        StringWriter writer = new StringWriter();
        try {
            toWriter(engine, writer, maximumNumberOfResults, separator);
        } catch (Exception ex) {
            throw new RuntimeException("[exporter error] an exception occurred while exporting the results", ex);
        }
        return writer.toString();
    }

    public void toWriter(Engine engine, Writer writer, int maximumNumberOfResults, String separator)
            throws Exception {
        writer.write("#" + header(engine) + "\n");

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

        boolean overflow = false;
        while (!overflow) {
            List<String> values = new ArrayList<>();

            for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
                InputVariable inputVariable = engine.getInputVariable(i);
                if (inputVariable.isEnabled()) {
                    double inputValue = inputVariable.getMinimum()
                            + sampleValues[i] * inputVariable.range() / resolution;
                    inputVariable.setInputValue(inputValue);
                    values.add(Op.str(inputValue));
                }
            }

            engine.process();

            for (OutputVariable outputVariable : engine.getOutputVariables()) {
                if (outputVariable.isEnabled()) {
                    values.add(Op.str(outputVariable.defuzzify()));
                }
            }

            writer.write(Op.join(values, separator) + "\n");
            writer.flush();

            overflow = Op.increment(sampleValues, minSampleValues, maxSampleValues);
        }
    }

    public String toString(Engine engine, String inputData) {
        StringWriter writer = new StringWriter();
        writer.write("#" + header(engine) + "\n");
        BufferedReader reader = new BufferedReader(new StringReader(inputData));
        String line;
        int lineNumber = 0;
        try {
            while ((line = reader.readLine()) != null) {
                ++lineNumber;
                List<Double> inputValues = parse(line.trim());
                if (inputValues.isEmpty()) {
                    continue;
                }
                if (inputValues.size() != engine.numberOfInputVariables()) {
                    throw new RuntimeException("[export error] engine has "
                            + "<" + engine.numberOfInputVariables() + "> input variables, "
                            + "but input data provides <" + inputValues.size() + "> "
                            + "at line number <" + lineNumber + ">");
                }
                toWriter(engine, writer, inputValues, separator);
                writer.write("\n");
                writer.flush();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return writer.toString();
    }

    public List<Double> parse(String values) {
        List<Double> result = new ArrayList<>();
        if (values.isEmpty() || values.charAt(0) == '#') {
            return result;
        }
        StringTokenizer tokenizer = new StringTokenizer(values);
        while (tokenizer.hasMoreTokens()) {
            result.add(Op.toDouble(tokenizer.nextToken()));
        }
        return result;
    }

    public void toWriter(Engine engine, Writer writer, List<Double> inputValues,
            String separator) throws Exception {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
            InputVariable inputVariable = engine.getInputVariable(i);
            if (inputVariable.isEnabled()) {
                double inputValue = inputValues.get(i);
                inputVariable.setInputValue(inputValue);
                values.add(inputValue);
            }
        }

        engine.process();

        for (int i = 0; i < engine.numberOfOutputVariables(); ++i) {
            OutputVariable outputVariable = engine.getOutputVariable(i);
            if (outputVariable.isEnabled()) {
                values.add(outputVariable.defuzzify());
            }
        }
        writer.write(Op.join(values, separator));
    }

}
