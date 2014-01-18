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
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.imex.FllImporter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jcrada
 */
public class Console {

    public static final String KW_INPUT_FILE = "-i";
    public static final String KW_INPUT_FORMAT = "-if";
    public static final String KW_OUTPUT_FILE = "-o";
    public static final String KW_OUTPUT_FORMAT = "-of";
    public static final String KW_EXAMPLE = "-example";
    public static final String KW_DATA_INPUT = "-d";
    public static final String KW_DATA_MAXIMUM = "-maximum";
    public static final String KW_DECIMALS = "-decimals";

    static class Option {

        public String key, value, description;

        public Option(String key, String value, String description) {
            this.key = key;
            this.value = value;
            this.description = description;
        }
    }

    public static String usage() {
        List<Option> options = new ArrayList<>();
        options.add(new Option(KW_INPUT_FILE, "inputfile", "file to import your engine from"));
        options.add(new Option(KW_INPUT_FORMAT, "format", "format of the file to import (fll | fis | fcl)"));
        options.add(new Option(KW_OUTPUT_FILE, "outputfile", "file to export your engine to"));
        options.add(new Option(KW_OUTPUT_FORMAT, "format", "format of the file to export (fll | fld | cpp | java | fis | fcl)"));
        options.add(new Option(KW_EXAMPLE, "example", "if not inputfile, built-in example to use as engine: (m)amdani or (t)akagi-sugeno"));
        options.add(new Option(KW_DATA_INPUT, "datafile", "if exporting to fld, file of input values to evaluate your engine on"));
        options.add(new Option(KW_DATA_MAXIMUM, "number", "if exporting to fld without datafile, maximum number of results to export"));
        options.add(new Option(KW_DECIMALS, "number", "number of decimals to utilize"));

        StringBuilder result = new StringBuilder();
        result.append("========================================\n");
        result.append("fuzzylite: a fuzzy logic control library\n");
        result.append(String.format("version: %s\n", FuzzyLite.LONG_VERSION));
        result.append(String.format("author: %s\n", FuzzyLite.AUTHOR));
        result.append("========================================\n");
        result.append("usage: java -jar jfuzzylite.jar inputfile outputfile\n");
        result.append("   or: java -jar jfuzzylite.jar ");
        for (Option option : options) {
            result.append(String.format("[%s %s] ", option.key, option.value));
        }
        result.append("\n\nwhere:\n");

        for (Option option : options) {
            result.append(String.format("%s %s \t%s.\n",
                    option.key, option.value, option.description));
        }
        result.append("\n");
        result.append("Visit http://www.fuzzylite.com for more information.");
        return result.toString();
    }

    protected static Map<String, String> parse(String[] args) {
        if (args.length % 2 != 0) {
            throw new RuntimeException("[option error] incomplete number of parameters [key value]");
        }
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
        } else {
            Set<String> validOptions = new HashSet<>();
            validOptions.add(KW_INPUT_FILE);
            validOptions.add(KW_INPUT_FORMAT);
            validOptions.add(KW_OUTPUT_FILE);
            validOptions.add(KW_OUTPUT_FORMAT);
            validOptions.add(KW_EXAMPLE);
            validOptions.add(KW_DATA_INPUT);
            validOptions.add(KW_DATA_MAXIMUM);
            validOptions.add(KW_DECIMALS);
            for (String option : options.keySet()) {
                if (!validOptions.contains(option)) {
                    throw new RuntimeException(String.format(
                            "[option error] option <%s> not supported", option));
                }
            }
        }
        return options;
    }

    protected static void process(Map<String, String> options) throws Exception {
        String decimals = options.get(KW_DECIMALS);
        if (decimals != null){
            FuzzyLite.setDecimals(Integer.parseInt(decimals));
        }
        
        String inputFormat = "";
        StringBuilder textEngine = new StringBuilder();

        String example = options.get(KW_EXAMPLE);

        boolean isExample = !(example == null || example.isEmpty());

        if (isExample) {
            Engine engine;
            if (example.equals("m") || example.equals("mamdani")) {
                engine = mamdani();
            } else if (example.equals("t") || example.equals("ts")
                    || example.equals("takagi-sugeno")) {
                engine = takagiSugeno();
            } else {
                throw new RuntimeException(String.format(
                        "[option error] example <%s> not available", example));
            }
            inputFormat = "fll";
            textEngine.append(new FllExporter().toString(engine));
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

    protected static void process(String input, Writer writer,
            String inputFormat, String outputFormat, Map<String, String> options)
            throws Exception {
        Importer importer = null;
        if ("fll".equals(inputFormat)) {
            importer = new FllImporter();
        } else if ("fcl".equals(inputFormat)) {
            importer = new FclImporter();
        } else if ("fis".equals(inputFormat)) {
            importer = new FisImporter();
        } else {
            throw new RuntimeException(String.format(
                    "[import error] format <%s> not supported", inputFormat));
        }
        Engine engine = importer.fromString(input);

        if ("fld".equals(outputFormat)) {
            FldExporter fldExporter = new FldExporter();
            String filename = options.get(KW_DATA_INPUT);
            if (filename != null) {
                File dataFile = new File(filename);
                if (!dataFile.exists()) {
                    throw new RuntimeException("[export error] file <" + filename + "> "
                            + "does not exist");
                }
                writer.write("#" + fldExporter.header(engine) + "\n");
                BufferedReader reader = new BufferedReader(new FileReader(dataFile));
                int lineNumber = 0;
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        ++lineNumber;
                        List<Double> inputValues = fldExporter.parse(line.trim());
                        if (inputValues.isEmpty()) {
                            continue;
                        }
                        if (inputValues.size() != engine.numberOfInputVariables()) {
                            throw new RuntimeException(String.format(
                                    "[export error] engine has <%i> input variables, "
                                    + "but input data provides <%i> at line <%i>",
                                    engine.numberOfInputVariables(),
                                    inputValues.size(), lineNumber));
                        }
                        fldExporter.toWriter(engine, writer, inputValues, fldExporter.getSeparator());
                        writer.write("\n");
                        writer.flush();
                    }
                } catch (Exception ex) {
                    reader.close();
                    throw new RuntimeException("at line <" + lineNumber + ">", ex);
                }

            } else {
                if (options.containsKey(KW_DATA_MAXIMUM)) {
                    int maximum = Integer.parseInt(options.get(KW_DATA_MAXIMUM));
                    writer.write(fldExporter.toString(engine, maximum));
                } else {
                    writer.write(fldExporter.toString(engine));
                }
            }

        } else {
            Exporter exporter = null;
            if ("fll".equals(outputFormat)) {
                exporter = new FllExporter();
            } else if ("fcl".equals(outputFormat)) {
                exporter = new FclExporter();
            } else if ("fis".equals(outputFormat)) {
                exporter = new FisExporter();
            } else if ("c++".equals(outputFormat)
                    || "cpp".equals(outputFormat)) {
                exporter = new CppExporter();
            } else if ("java".equals(outputFormat)) {
                exporter = new JavaExporter();
            } else {
                throw new RuntimeException(String.format(
                        "[export error] format <%s> not supported", outputFormat));
            }
            writer.write(exporter.toString(engine));
        }
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
        outputVariable1.fuzzyOutput().setAccumulation(new Maximum());
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
        outputVariable1.fuzzyOutput().setAccumulation(null);
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
        outputVariable2.setRange(-1, 1);
        outputVariable2.setLockOutputRange(false);
        outputVariable2.setLockValidOutput(true);
        outputVariable2.setDefaultValue(Double.NaN);
        outputVariable2.setDefuzzifier(new WeightedAverage());
        outputVariable2.fuzzyOutput().setAccumulation(null);
        outputVariable2.addTerm(Function.create("fx", "sin(inputX)/inputX", engine, true));
        engine.addOutputVariable(outputVariable2);

        OutputVariable outputVariable3 = new OutputVariable();
        outputVariable3.setName("diffFx");
        outputVariable3.setRange(-1, 1);
        outputVariable3.setLockOutputRange(false);
        outputVariable3.setLockValidOutput(false);
        outputVariable3.setDefaultValue(Double.NaN);
        outputVariable3.setDefuzzifier(new WeightedAverage());
        outputVariable3.fuzzyOutput().setAccumulation(null);
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

    public static void exportAllExamples(String from, String to, String sourceBase, String targetBase) throws Exception {
        List<String> examples = new ArrayList<>();
        examples.add("/mamdani/AllTerms");
        examples.add("/mamdani/SimpleDimmer");
        examples.add("/mamdani/matlab/mam21");
        examples.add("/mamdani/matlab/mam22");
        examples.add("/mamdani/matlab/shower");
        examples.add("/mamdani/matlab/tank");
        examples.add("/mamdani/matlab/tank2");
        examples.add("/mamdani/matlab/tipper");
        examples.add("/mamdani/matlab/tipper1");
        examples.add("/mamdani/octave/investment_portfolio");
        examples.add("/mamdani/octave/mamdani_tip_calculator");
        examples.add("/takagi-sugeno/approximation");
        examples.add("/takagi-sugeno/SimpleDimmer");
        examples.add("/takagi-sugeno/matlab/fpeaks");
        examples.add("/takagi-sugeno/matlab/invkine1");
        examples.add("/takagi-sugeno/matlab/invkine2");
        examples.add("/takagi-sugeno/matlab/juggler");
        examples.add("/takagi-sugeno/matlab/membrn1");
        examples.add("/takagi-sugeno/matlab/membrn2");
        examples.add("/takagi-sugeno/matlab/slbb");
        examples.add("/takagi-sugeno/matlab/slcp");
        examples.add("/takagi-sugeno/matlab/slcp1");
        examples.add("/takagi-sugeno/matlab/slcpp1");
        examples.add("/takagi-sugeno/matlab/sltbu_fl");
        examples.add("/takagi-sugeno/matlab/sugeno1");
        examples.add("/takagi-sugeno/matlab/tanksg");
        examples.add("/takagi-sugeno/matlab/tippersg");
        examples.add("/takagi-sugeno/octave/cubic_approximator");
        examples.add("/takagi-sugeno/octave/heart_disease_risk");
        examples.add("/takagi-sugeno/octave/linear_tip_calculator");
        examples.add("/takagi-sugeno/octave/sugeno_tip_calculator");
        examples.add("/tsukamoto/tsukamoto");

        Importer importer;
        if ("fll".equals(from)) {
            importer = new FllImporter();
        } else if ("fis".equals(from)) {
            importer = new FisImporter();
        } else if ("fcl".equals(from)) {
            importer = new FclImporter();
        } else {
            throw new RuntimeException("[examples error] unrecognized format "
                    + "<" + from + "> to import");
        }

        Exporter exporter;
        if ("fll".equals(to)) {
            exporter = new FllExporter();
        } else if ("fld".equals(to)) {
            exporter = new FldExporter(" ");
        } else if ("fcl".equals(to)) {
            exporter = new FclExporter();
        } else if ("fis".equals(to)) {
            exporter = new FisExporter();
        } else if ("cpp".equals(to)) {
            exporter = new CppExporter();
        } else if ("java".equals(to)) {
            exporter = new JavaExporter();
        } else {
            throw new RuntimeException("[examples error] unrecognized format "
                    + "<" + from + "> to export");
        }

        List<Op.Pair<Exporter, Importer>> tests = new ArrayList<>();
        tests.add(new Op.Pair<Exporter, Importer>(new FllExporter(), new FllImporter()));
        tests.add(new Op.Pair<Exporter, Importer>(new FclExporter(), new FclImporter()));
        tests.add(new Op.Pair<Exporter, Importer>(new FisExporter(), new FisImporter()));

        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < examples.size(); ++i) {
            System.out.println("Processing " + (i + 1) + "/" + examples.size() + ": " + examples.get(i));
            try {
                StringBuilder text = new StringBuilder();
                String input = sourceBase + examples.get(i) + "." + from;
                BufferedReader source = new BufferedReader(new FileReader(input));
                String line;
                while ((line = source.readLine()) != null) {
                    text.append(line).append("\n");
                }
                source.close();

                Engine engine = importer.fromString(text.toString());

                for (Op.Pair<Exporter, Importer> imex : tests) {
                    String out = imex.first.toString(engine);
                    Engine copy = imex.second.fromString(out);
                    String out_copy = imex.first.toString(copy);

                    if (!out.equals(out_copy)) {
                        errors.append(String.format("[imex error] different results <%s,%s> at %s.%s",
                                imex.first.getClass().getSimpleName(),
                                imex.first.getClass().getSimpleName(),
                                examples.get(i), from));
                    }
                }

                String output = targetBase + examples.get(i) + "." + to;
                File outputFile = new File(output);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileWriter target = new FileWriter(outputFile);
                if ("cpp".equals(to)) {
                    target.write("#include <fl/Headers.h>\n\n"
                            + "int main(int argc, char** argv){\n"
                            + exporter.toString(engine)
                            + "\n}\n");
                } else if ("java".equals(to)) {
                    String className = examples.get(i).substring(examples.get(i).lastIndexOf('/') + 1);
                    target.write(
                            "import com.fuzzylite.*;\n"
                            + "import com.fuzzylite.defuzzifier.*;\n"
                            + "import com.fuzzylite.factory.*;\n"
                            + "import com.fuzzylite.hedge.*;\n"
                            + "import com.fuzzylite.imex.*;\n"
                            + "import com.fuzzylite.norm.*;\n"
                            + "import com.fuzzylite.norm.s.*;\n"
                            + "import com.fuzzylite.norm.t.*;\n"
                            + "import com.fuzzylite.rule.*;\n"
                            + "import com.fuzzylite.term.*;\n"
                            + "import com.fuzzylite.variable.*;\n\n"
                            + "public class " + Op.makeValidId(className) + "{\n"
                            + "public static void main(String[] args){\n"
                            + exporter.toString(engine)
                            + "\n}\n}\n");
                } else {
                    target.write(exporter.toString(engine));
                }
                target.close();
            } catch (Exception ex) {
                errors.append("error at " + examples.get(i) + ":\n" + ex.toString() + "\n");
                ex.printStackTrace();
                return;
            }
        }
        if (errors.toString().isEmpty()) {
            FuzzyLite.logger().info("No errors were found exporting files");
        } else {
            FuzzyLite.logger().info("The following errors were encountered while exporting:\n"
                    + errors.toString());
        }
    }

    public static void main(String[] args) {
//        FuzzyLite.logger().setLevel(Level.INFO);
        if (args.length == 0) {
            System.out.println(usage());
            return;
        }
        if (args.length == 1 && "export-examples".equals(args[0])) {
            String sourceBase = "/home/jcrada/Development/fl/jfuzzylite/examples/original";
            String targetBase = "/tmp/fl";
            FuzzyLite.setDecimals(3);
            try {
                exportAllExamples("fis", "fll", sourceBase, targetBase);
                exportAllExamples("fis", "fcl", sourceBase, targetBase);
                exportAllExamples("fis", "fis", sourceBase, targetBase);
                exportAllExamples("fis", "cpp", sourceBase, targetBase);
                exportAllExamples("fis", "java", sourceBase, targetBase);
                FuzzyLite.setDecimals(8);
                exportAllExamples("fis", "fld", sourceBase, targetBase);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
        try {
            Map<String, String> options = parse(args);
            process(options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
