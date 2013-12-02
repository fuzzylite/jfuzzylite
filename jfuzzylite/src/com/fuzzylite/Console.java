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
package com.fuzzylite;

import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.imex.CppExporter;
import com.fuzzylite.imex.Exporter;
import com.fuzzylite.imex.FclExporter;
import com.fuzzylite.imex.FclImporter;
import com.fuzzylite.imex.FisExporter;
import com.fuzzylite.imex.FisImporter;
import com.fuzzylite.imex.Importer;
import com.fuzzylite.imex.JavaExporter;
import com.fuzzylite.imex.DataExporter;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author jcrada
 */
public class Console {

    public static final String KW_INPUT_FILE = "-i";
    public static final String KW_INPUT_FORMAT = "-if";
    public static final String KW_OUTPUT_FILE = "-o";
    public static final String KW_OUTPUT_FORMAT = "-of";
    public static final String KW_EXAMPLE = "-ex";
    public static final String KW_DATA_RESOLUTION = "-res";
    public static final String KW_DATA_SEPARATOR = "-sep";

    public static String usage() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put(KW_INPUT_FILE, "inputfile");
        options.put(KW_INPUT_FORMAT, "fis,fcl");
        options.put(KW_OUTPUT_FILE, "outputfile");
        options.put(KW_OUTPUT_FORMAT, "fis,fcl,cpp,java,dat");
        options.put(KW_EXAMPLE, "(m)amdani,(t)akagi-sugeno");
        options.put(KW_DATA_RESOLUTION, "resolution");
        options.put(KW_DATA_SEPARATOR, "separator");

        StringBuilder result = new StringBuilder();
        result.append("========================================\n");
        result.append("fuzzylite: a fuzzy logic control library\n");
        result.append(String.format("version: %s\n", FuzzyLite.LONG_VERSION));
        result.append(String.format("author: %s\n", FuzzyLite.AUTHOR));
        result.append("========================================\n");
        result.append("usage: java -jar jfuzzylite.jar inputfile outputfile\n");
        result.append("   or: java -jar jfuzzylite.jar ");
        for (String option : options.keySet()) {
            result.append(String.format("[%s] ", option));
        }
        result.append("\n");
        result.append("where: ");

        for (Map.Entry<String, String> option : options.entrySet()) {
            result.append(String.format("[%s %s] \n       ", option.getKey(), option.getValue()));
        }
        result.append("\n");
        result.append("Visit http://www.fuzzylite.com for more information.");
        return result.toString();
    }

    protected static Map<String, String> parse(String[] args) {
        Map<String, String> options = new HashMap<>();
        String key, value;
        for (int i = 0; i < args.length - 1; i += 2) {
            key = args[i];
            value = args[i + 1];
            options.put(key, value);
        }
        if (options.size() == 1) {
            Map.Entry<String, String> in_out = options.entrySet().iterator().next();
            if (in_out.getKey().charAt(0) != '-') {
                options.put(KW_INPUT_FILE, in_out.getKey());
                options.put(KW_OUTPUT_FILE, in_out.getValue());
            }
        }
        return options;
    }

    protected static void process(Map<String, String> options) throws Exception {
        String inputFormat = "";
        StringBuilder textEngine = new StringBuilder();

        String example = options.get(KW_EXAMPLE);

        boolean isExample = !(example == null || example.isEmpty());

        if (isExample) {
            Engine engine;
            if (example.equalsIgnoreCase("m") || example.equalsIgnoreCase("mamdani")){
                engine = mamdani();
            }else if (example.equalsIgnoreCase("t") || example.equalsIgnoreCase("ts") ||
                    example.equalsIgnoreCase("takagi-sugeno")){
                engine = takagiSugeno();
            }else{
                throw new RuntimeException(String.format(
                        "[option error] example <%s> not available", example));
            }
            inputFormat = "fcl";
            textEngine.append(new FclExporter().toString(engine));
        } else {

            String inputFilename = options.get(KW_INPUT_FILE);
            if (inputFilename == null) {
                throw new RuntimeException("[option error] no input file specified");
            }
            File inputFile = new File(inputFilename);
            if (!inputFile.exists()) {
                inputFile.createNewFile();
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            try {
                String line = reader.readLine();
                while (line != null) {
                    textEngine.append(line).append("\n");
                    line = reader.readLine();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                reader.close();
            }

            inputFormat = options.get(KW_INPUT_FORMAT);
            if (inputFormat == null || inputFormat.isEmpty()) {
                int extensionIndex = inputFilename.lastIndexOf(".");
                if (extensionIndex >= 0) {
                    inputFormat = inputFilename.substring(extensionIndex + 1);
                } else {
                    throw new RuntimeException("[format error] unspecified format of input file");
                }
            }
        }

        String outputFilename = options.get(KW_OUTPUT_FILE);
        String outputFormat = options.get(KW_OUTPUT_FORMAT);
        if (outputFormat == null || outputFormat.isEmpty()) {
            if (outputFilename == null || outputFilename.isEmpty()) {
                throw new RuntimeException("[format error] unspecified format of output");
            } else {
                int extensionIndex = outputFilename.lastIndexOf(".");
                if (extensionIndex >= 0) {
                    outputFormat = outputFilename.substring(extensionIndex + 1);
                } else {
                    throw new RuntimeException("[format error] unspecified format of output file");
                }
            }
        }

        Writer writer;
        if (outputFilename == null || outputFilename.isEmpty()) {
            writer = new StringWriter();
        } else {
            File outputFile = new File(outputFilename);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            writer = new FileWriter(outputFile);
        }
        try {
            process(textEngine.toString(), writer, inputFormat, outputFormat, options);
        } catch (Exception ex) {
            writer.close();
            throw ex;
        }
        writer.flush();
        if (outputFilename == null || outputFilename.isEmpty()) {
            StringWriter stringWriter = (StringWriter) writer;
            System.out.println(stringWriter.getBuffer().toString());
        }
        writer.close();
    }

    protected static void process(String input, Writer output,
            String inputFormat, String outputFormat, Map<String, String> options)
            throws Exception {
        Importer importer = null;
        if ("fcl".equalsIgnoreCase(inputFormat)) {
            importer = new FclImporter();
        } else if ("fis".equalsIgnoreCase(inputFormat)) {
            importer = new FisImporter();
        } else {
            throw new RuntimeException(String.format(
                    "[import error] format <%s> not supported", inputFormat));
        }

        Exporter exporter = null;
        if ("fcl".equalsIgnoreCase(outputFormat)) {
            exporter = new FclExporter();
        } else if ("fis".equalsIgnoreCase(outputFormat)) {
            exporter = new FisExporter();
        } else if ("c++".equalsIgnoreCase(outputFormat)
                || "cpp".equalsIgnoreCase(outputFormat)) {
            exporter = new CppExporter();
        } else if ("java".equalsIgnoreCase(outputFormat)) {
            exporter = new JavaExporter();
        } else if ("dat".equalsIgnoreCase(outputFormat)) {
            String separator = DataExporter.DEFAULT_SEPARATOR;
            int resolution = DataExporter.DEFAULT_RESOLUTION;
            if (options.containsKey(KW_DATA_SEPARATOR)) {
                separator = options.get(KW_DATA_SEPARATOR);
            }
            if (options.containsKey(KW_DATA_RESOLUTION)) {
                resolution = Integer.parseInt(options.get(KW_DATA_RESOLUTION));
            }
            exporter = new DataExporter(separator, resolution);
        } else {
            throw new RuntimeException(String.format(
                    "[export error] format <%s> not supported", outputFormat));
        }

        Engine engine = importer.fromString(input);
        output.write(exporter.toString(engine));
    }

    public static Engine mamdani() {
        Engine engine = new Engine();
        engine.setName("simple-dimmer");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setName("Ambient");
        inputVariable1.setRange(0.000, 1.000);
        inputVariable1.addTerm(new Triangle("DARK", 0.000, 0.250, 0.500));
        inputVariable1.addTerm(new Triangle("MEDIUM", 0.250, 0.500, 0.750));
        inputVariable1.addTerm(new Triangle("BRIGHT", 0.500, 0.750, 1.000));
        engine.addInputVariable(inputVariable1);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setName("Power");
        outputVariable1.setRange(0.000, 2.000);
        outputVariable1.setLockOutputRange(false);
        outputVariable1.setLockValidOutput(false);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.getOutput().setAccumulation(new Maximum());
        outputVariable1.addTerm(new Triangle("LOW", 0.000, 0.500, 1.000));
        outputVariable1.addTerm(new Triangle("MEDIUM", 0.500, 1.000, 1.500));
        outputVariable1.addTerm(new Triangle("HIGH", 1.000, 1.500, 2.000));
        engine.addOutputVariable(outputVariable1);

        RuleBlock ruleBlock1 = new RuleBlock();
        ruleBlock1.setName("");
        ruleBlock1.setConjunction(null);
        ruleBlock1.setDisjunction(null);
        ruleBlock1.setActivation(new Minimum());
        ruleBlock1.addRule(Rule.parse("if Ambient is DARK then Power is HIGH", engine));
        ruleBlock1.addRule(Rule.parse("if Ambient is MEDIUM then Power is MEDIUM", engine));
        ruleBlock1.addRule(Rule.parse("if Ambient is BRIGHT then Power is LOW", engine));
        engine.addRuleBlock(ruleBlock1);

        return engine;
    }

    public static Engine takagiSugeno() {
        Engine engine = new Engine();
        engine.setName("approximation of sin(x)/x");

        InputVariable inputVariable1 = new InputVariable();
        inputVariable1.setName("inputX");
        inputVariable1.setRange(0.000, 10.000);
        inputVariable1.addTerm(new Triangle("NEAR_1", 0.000, 1.000, 2.000));
        inputVariable1.addTerm(new Triangle("NEAR_2", 1.000, 2.000, 3.000));
        inputVariable1.addTerm(new Triangle("NEAR_3", 2.000, 3.000, 4.000));
        inputVariable1.addTerm(new Triangle("NEAR_4", 3.000, 4.000, 5.000));
        inputVariable1.addTerm(new Triangle("NEAR_5", 4.000, 5.000, 6.000));
        inputVariable1.addTerm(new Triangle("NEAR_6", 5.000, 6.000, 7.000));
        inputVariable1.addTerm(new Triangle("NEAR_7", 6.000, 7.000, 8.000));
        inputVariable1.addTerm(new Triangle("NEAR_8", 7.000, 8.000, 9.000));
        inputVariable1.addTerm(new Triangle("NEAR_9", 8.000, 9.000, 10.000));
        engine.addInputVariable(inputVariable1);

        OutputVariable outputVariable1 = new OutputVariable();
        outputVariable1.setName("outputFx");
        outputVariable1.setRange(-1.000, 1.000);
        outputVariable1.setLockOutputRange(false);
        outputVariable1.setLockValidOutput(true);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setDefuzzifier(new WeightedAverage());
        outputVariable1.getOutput().setAccumulation(null);
        outputVariable1.addTerm(new Constant("f1", 0.840));
        outputVariable1.addTerm(new Constant("f2", 0.450));
        outputVariable1.addTerm(new Constant("f3", 0.040));
        outputVariable1.addTerm(new Constant("f4", -0.180));
        outputVariable1.addTerm(new Constant("f5", -0.190));
        outputVariable1.addTerm(new Constant("f6", -0.040));
        outputVariable1.addTerm(new Constant("f7", 0.090));
        outputVariable1.addTerm(new Constant("f8", 0.120));
        outputVariable1.addTerm(new Constant("f9", 0.040));
        engine.addOutputVariable(outputVariable1);

        OutputVariable outputVariable2 = new OutputVariable();
        outputVariable2.setName("trueFx");
        outputVariable2.setRange(Double.NaN, Double.NaN);
        outputVariable2.setLockOutputRange(false);
        outputVariable2.setLockValidOutput(true);
        outputVariable2.setDefaultValue(Double.NaN);
        outputVariable2.setDefuzzifier(new WeightedAverage());
        outputVariable2.getOutput().setAccumulation(null);
        outputVariable2.addTerm(Function.create("fx", "sin(inputX)/inputX", engine, true));
        engine.addOutputVariable(outputVariable2);

        OutputVariable outputVariable3 = new OutputVariable();
        outputVariable3.setName("diffFx");
        outputVariable3.setRange(Double.NaN, Double.NaN);
        outputVariable3.setLockOutputRange(false);
        outputVariable3.setLockValidOutput(false);
        outputVariable3.setDefaultValue(Double.NaN);
        outputVariable3.setDefuzzifier(new WeightedAverage());
        outputVariable3.getOutput().setAccumulation(null);
        outputVariable3.addTerm(Function.create("diff", "fabs(outputFx-trueFx)", engine, true));
        engine.addOutputVariable(outputVariable3);

        RuleBlock ruleBlock1 = new RuleBlock();
        ruleBlock1.setName("");
        ruleBlock1.setConjunction(null);
        ruleBlock1.setDisjunction(null);
        ruleBlock1.setActivation(new AlgebraicProduct());
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_1 then outputFx = f1", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_2 then outputFx = f2", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_3 then outputFx = f3", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_4 then outputFx = f4", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_5 then outputFx = f5", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_6 then outputFx = f6", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_7 then outputFx = f7", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_8 then outputFx = f8", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is NEAR_9 then outputFx = f9", engine));
        ruleBlock1.addRule(Rule.parse("if inputX is any then trueFx = fx and diffFx = diff", engine));
        engine.addRuleBlock(ruleBlock1);

        return engine;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(usage());
            return;
        }

        Map<String, String> options = parse(args);
        try {
            process(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
