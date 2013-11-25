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
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.example.ts.SimpleDimmer;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class ResultExporter extends Exporter {

    @Override
    public String toString(Engine engine) {
        return toString(engine, " ");
    }

    public String toString(Engine engine, String separator) {
        return toString(engine, separator, 100);
    }

    public String toString(Engine engine, String separator, int resolution) {
        StringWriter writer = new StringWriter();
        try {
            toWriter(engine, writer, separator, resolution);
        } catch (Exception ex) {
            throw new RuntimeException("[exporter error] an exception occurred while exporting the results", ex);
        }
        return writer.toString();
    }

    public void toWriter(Engine engine, Writer writer, String separator, int resolution)
            throws Exception {

        List<String> variables = new ArrayList<>();
        for (InputVariable inputVariable : engine.getInputVariables()) {
            variables.add(inputVariable.getName());
        }
        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            variables.add(outputVariable.getName());
        }

        writer.write(Op.join(variables, separator) + "\n");
        writer.flush();

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
            List<String> values = new ArrayList<>();

            for (int i = 0; i < engine.numberOfInputVariables(); ++i) {
                InputVariable inputVariable = engine.getInputVariable(i);
                double range = inputVariable.getMaximum() - inputVariable.getMinimum();
                double inputValue = inputVariable.getMinimum()
                        + sampleValues[i] * range / resolution;
                inputVariable.setInputValue(inputValue);
                values.add(Op.str(inputValue));
            }

            engine.process();

            for (OutputVariable outputVariable : engine.getOutputVariables()) {
                values.add(Op.str(outputVariable.defuzzify()));
            }

            writer.write(Op.join(values, separator) + "\n");
            writer.flush();

            overflow = Op.increment(sampleValues, minSampleValues, maxSampleValues);
        }
    }

    public static void main(String[] args) {
        SimpleDimmer dimmer = new SimpleDimmer();
        ResultExporter exporter = new ResultExporter();
        FuzzyLite.setDecimals(6);
        System.out.println(exporter.toString(dimmer));
    }
}
