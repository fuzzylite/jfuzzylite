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

import com.fuzzylite.Op.Pair;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.imex.CppExporter;
import com.fuzzylite.imex.Exporter;
import com.fuzzylite.imex.FclExporter;
import com.fuzzylite.imex.FclImporter;
import com.fuzzylite.imex.FisExporter;
import com.fuzzylite.imex.FisImporter;
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.imex.FllImporter;
import com.fuzzylite.imex.Importer;
import com.fuzzylite.imex.JavaExporter;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Console {

    public static final String KW_INPUT_FILE = "-i";
    public static final String KW_INPUT_FORMAT = "-if";
    public static final String KW_OUTPUT_FILE = "-o";
    public static final String KW_OUTPUT_FORMAT = "-of";
    public static final String KW_EXAMPLE = "-example";
    public static final String KW_DECIMALS = "-decimals";
    public static final String KW_DATA_INPUT_FILE = "-d";
    public static final String KW_DATA_VALUES = "-values";
    public static final String KW_DATA_VALUES_SCOPE = "-scope";
    public static final String KW_DATA_EXPORT_HEADER = "-dheader";
    public static final String KW_DATA_EXPORT_INPUTS = "-dinputs";

    static class Option {

        public String key, value, description;

        public Option(String key, String value, String description) {
            this.key = key;
            this.value = value;
            this.description = description;
        }
    }

    static class NullWriter extends OutputStreamWriter {

        static class NullStream extends OutputStream {

            @Override
            public void write(int b) throws IOException {
                //do nothing
            }
        }

        public NullWriter() {
            super(new NullStream(), FuzzyLite.UTF_8);
        }

        @Override
        public void close() {

        }

    }

    public String usage() {
        List<Option> options = new ArrayList<Option>();
        options.add(new Option(KW_INPUT_FILE, "inputfile", "file to import your engine from"));
        options.add(new Option(KW_INPUT_FORMAT, "format", "format of the file to import (fll | fis | fcl)"));
        options.add(new Option(KW_OUTPUT_FILE, "outputfile", "file to export your engine to"));
        options.add(new Option(KW_OUTPUT_FORMAT, "format", "format of the file to export (fll | fld | cpp | java | fis | fcl)"));
        options.add(new Option(KW_EXAMPLE, "example", "if not inputfile, built-in example to use as engine: (m)amdani or (t)akagi-sugeno"));
        options.add(new Option(KW_DECIMALS, "number", "number of decimals to utilize"));
        options.add(new Option(KW_DATA_INPUT_FILE, "datafile", "if exporting to fld, file of input values to evaluate your engine on"));
        options.add(new Option(KW_DATA_VALUES, "number",  "if exporting to fld without datafile, number of results to export within scope (default: EachVariable)"));
        options.add(new Option(KW_DATA_VALUES_SCOPE, "scope", "if exporting to fld without datafile, scope of " + KW_DATA_VALUES + ": [EachVariable|AllVariables]"));
        options.add(new Option(KW_DATA_EXPORT_HEADER, "boolean", "if true and exporting to fld, include headers"));
        options.add(new Option(KW_DATA_EXPORT_INPUTS, "boolean", "if true and exporting to fld, include input values"));

        StringBuilder result = new StringBuilder();
        result.append("=========================================\n");
        result.append("jfuzzylite: a fuzzy logic control library\n");
        result.append(String.format("version: %s\n", FuzzyLite.VERSION));
        result.append(String.format("author: %s\n", FuzzyLite.AUTHOR));
        result.append(String.format("license: %s\n", FuzzyLite.LICENSE));
        result.append("=========================================\n");
        result.append("usage: java -jar jfuzzylite.jar inputfile outputfile\n");
        result.append("   or: java -jar jfuzzylite.jar ");
        for (Option option : options) {
            result.append(String.format("[%s %s] ", option.key, option.value));
        }
        result.append("\n\nwhere:\n");

        for (Option option : options) {
            result.append(option.key);
            int keyPad = 12 - option.key.length();
            if (keyPad > 0) {
                result.append(String.format("%" + keyPad + "s", " "));
            }

            result.append(option.value);
            int valuePad = 13 - option.value.length();
            if (valuePad > 0) {
                result.append(String.format("%" + valuePad + "s", " "));
            }
            result.append(option.description).append("\n");
        }

        result.append("\n");
        result.append("Visit " + FuzzyLite.WEBSITE + " for more information.\n\n");
        result.append("Copyright (C) 2010-2016 FuzzyLite Limited.\n");
        result.append("All rights reserved.");

        return result.toString();
    }

    protected Map<String, String> parse(String[] args) {
        if (args.length % 2 != 0) {
            throw new RuntimeException("[option error] incomplete number of parameters [key value]");
        }
        Map<String, String> options = new HashMap<String, String>();
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
            Set<String> validOptions = new HashSet<String>();
            validOptions.add(KW_INPUT_FILE);
            validOptions.add(KW_INPUT_FORMAT);
            validOptions.add(KW_OUTPUT_FILE);
            validOptions.add(KW_OUTPUT_FORMAT);
            validOptions.add(KW_EXAMPLE);
            validOptions.add(KW_DATA_INPUT_FILE);
            validOptions.add(KW_DATA_VALUES);
            validOptions.add(KW_DATA_VALUES_SCOPE);
            validOptions.add(KW_DECIMALS);
            for (String option : options.keySet()) {
                if (!validOptions.contains(option)) {
                    throw new RuntimeException(String.format(
                            "[option error] option <%s> not recognized", option));
                }
            }
        }
        return options;
    }

    protected void process(Map<String, String> options) throws Exception {
        String decimals = options.get(KW_DECIMALS);
        if (decimals != null) {
            FuzzyLite.setDecimals(Integer.parseInt(decimals));
        }

        String inputFormat = "";
        StringBuilder textEngine = new StringBuilder();

        String example = options.get(KW_EXAMPLE);

        boolean isExample = !(example == null || example.isEmpty());

        if (isExample) {
            Engine engine;
            if ("m".equals(example) || "mamdani".equals(example)) {
                engine = mamdani();
            } else if ("t".equals(example) || "ts".equals(example)
                    || "takagi-sugeno".equals(example)) {
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inputFile), FuzzyLite.UTF_8));
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
                int extensionIndex = inputFilename.lastIndexOf('.');
                if (extensionIndex >= 0) {
                    inputFormat = inputFilename.substring(extensionIndex + 1);
                } else {
                    throw new RuntimeException("[format error] unspecified format of input file");
                }
            }
        }

        String outputFilename = options.get(KW_OUTPUT_FILE);
        if (outputFilename == null) {
            outputFilename = "";
        }
        String outputFormat = options.get(KW_OUTPUT_FORMAT);
        if (outputFormat == null || outputFormat.isEmpty()) {
            int extensionIndex = outputFilename.lastIndexOf('.');
            if (extensionIndex >= 0) {
                outputFormat = outputFilename.substring(extensionIndex + 1);
            } else {
                throw new RuntimeException("[format error] unspecified format of output file");
            }
        }

        Writer writer;
        if (outputFilename.isEmpty()) {
            writer = System.console().writer();
        } else {
            File outputFile = new File(outputFilename);
            if (!outputFile.createNewFile()) {
                FuzzyLite.logger().log(Level.FINE, "Replacing file {0}", outputFilename);
            }
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), FuzzyLite.UTF_8));
        }
        try {
            process(textEngine.toString(), writer, inputFormat, outputFormat, options);
        } catch (Exception ex) {
            throw ex;
        } finally {
            writer.close();
        }
    }

    protected void process(String input, Writer writer,
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
            fldExporter.setSeparator(" ");
            boolean exportHeaders = true;
            boolean exportInputValues = true;
            if (options.containsKey(KW_DATA_EXPORT_HEADER)) {
                exportHeaders = "true".equals(options.get(KW_DATA_EXPORT_HEADER));
            }
            if (options.containsKey(KW_DATA_EXPORT_INPUTS)) {
                exportInputValues = "true".equals(options.get(KW_DATA_EXPORT_INPUTS));
            }
            fldExporter.setExportHeaders(exportHeaders);
            fldExporter.setExportInputValues(exportInputValues);

            String filename = options.get(KW_DATA_INPUT_FILE);
            if (filename != null) {
                File dataFile = new File(filename);
                if (!dataFile.exists()) {
                    throw new RuntimeException("[export error] file <" + filename + "> "
                            + "does not exist");
                }
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(dataFile), FuzzyLite.UTF_8);
                try {
                    fldExporter.write(engine, writer, reader);
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    reader.close();
                }

            } else if (options.containsKey(KW_DATA_VALUES)) {
                int values = Integer.parseInt(options.get(KW_DATA_VALUES));
                FldExporter.ScopeOfValues scope = FldExporter.ScopeOfValues.EachVariable;
                if (options.containsKey(KW_DATA_VALUES_SCOPE)){
                    scope = FldExporter.ScopeOfValues.valueOf(options.get(KW_DATA_VALUES_SCOPE));
                }
                fldExporter.write(engine, writer, values, scope);
            } else {

                StringBuilder buffer = new StringBuilder();
                buffer.append("#FuzzyLite Interactive Console (press H for help)\n");
                buffer.append(fldExporter.header(engine)).append("\n");
                if (engine.getInputVariables().isEmpty()) {
                    buffer.append("[error] the engine does not have input variables.\n");
                }
                if (engine.getOutputVariables().isEmpty()) {
                    buffer.append("[error] the engine does not have output variables.\n");
                }
                writer.append(buffer.toString()).flush();

                boolean printToConsole = writer != System.console().writer();
                if (printToConsole) {
                    System.console().writer().append(buffer.toString()).flush();
                }

                if (engine.getInputVariables().isEmpty() || engine.getOutputVariables().isEmpty()) {
                    return;
                }
                interactive(writer, engine);
            }

        } else {
            Exporter exporter = null;
            if ("fll".equals(outputFormat)) {
                exporter = new FllExporter();
            } else if ("fcl".equals(outputFormat)) {
                exporter = new FclExporter();
            } else if ("fis".equals(outputFormat)) {
                exporter = new FisExporter();
            } else if ("cpp".equals(outputFormat)) {
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

    public void interactive(Writer writer, Engine engine) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, FuzzyLite.UTF_8));
        boolean printToConsole = writer != System.console().writer();
        StringBuilder buffer = new StringBuilder();
        final String space = "\t";
        do {
            buffer.append("\n> ");
            writer.append(buffer.toString()).flush();
            if (printToConsole) {
                System.console().writer().append(buffer.toString()).flush();
            }
            buffer.setLength(0);

            String line = reader.readLine();
            if (line == null) {
                line = "Q";
            }

            String[] tokens = line.split("\\s+");
            if (tokens.length == 1) {
                String token = tokens[0];
                if ("R".equalsIgnoreCase(token)) {
                    buffer.append("#[Restart]");
                    engine.restart();
                    continue;
                } else if ("H".equalsIgnoreCase(token)) {
                    buffer.append(interactiveHelp());
                    continue;
                } else if ("Q".equalsIgnoreCase(token)) {
                    buffer.append("#[Quit]\n");
                    writer.append(buffer.toString()).flush();
                    if (printToConsole) {
                        System.console().writer().append(buffer.toString()).flush();
                    }
                    break;
                }
            }

            int numberOfTokens = Math.max(tokens.length, engine.numberOfInputVariables());
            numberOfTokens += numberOfTokens % engine.numberOfInputVariables();
            double[] inputValues = new double[numberOfTokens];
            for (int i = 0; i < tokens.length; ++i) {
                double defaultValue = engine.getInputVariable(i % engine.numberOfInputVariables()).getValue();
                inputValues[i] = Op.toDouble(tokens[i], defaultValue);
            }
            for (int i = tokens.length; i < numberOfTokens; ++i) {
                double defaultValue = engine.getInputVariable(i % engine.numberOfInputVariables()).getValue();
                inputValues[i] = defaultValue;
            }

            for (int i = 0; i < inputValues.length; ++i) {
                int index = i % engine.numberOfInputVariables();

                engine.getInputVariable(index).setValue(inputValues[i]);
                buffer.append(space).append(Op.str(inputValues[i]));

                if ((i + 1) % engine.numberOfInputVariables() == 0) {
                    engine.process();
                    buffer.append(space).append("=").append(space);
                    Iterator<OutputVariable> it = engine.getOutputVariables().iterator();
                    while (it.hasNext()) {
                        buffer.append(Op.str(it.next().getValue()));
                        if (it.hasNext()) {
                            buffer.append(space);
                        }
                    }
                    if (i + 1 < inputValues.length) {
                        buffer.append("\n");
                    }
                }
            }
        } while (true);
        reader.close();
    }

    public String interactiveHelp() {
        StringBuilder result = new StringBuilder();
        result.append("#Special Keys:\n");
        result.append("#=============\n");
        result.append("#   [Enter]\tProcess engine\n");
        result.append("#      R\tRestart engine\n");
        result.append("#      Q\tQuit interactive console\n");
        result.append("#      H\tShow this help\n");
        result.append("#=============\n");
        return result.toString();
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
        outputVariable1.setLockValueInRange(false);
        outputVariable1.setLockPreviousValue(false);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setDefuzzifier(new Centroid(200));
        outputVariable1.fuzzyOutput().setAggregation(new Maximum());
        outputVariable1.addTerm(new Triangle("LOW", 0.000, 0.500, 1.000));
        outputVariable1.addTerm(new Triangle("MEDIUM", 0.500, 1.000, 1.500));
        outputVariable1.addTerm(new Triangle("HIGH", 1.000, 1.500, 2.000));
        engine.addOutputVariable(outputVariable1);

        RuleBlock ruleBlock1 = new RuleBlock();
        ruleBlock1.setName("");
        ruleBlock1.setConjunction(null);
        ruleBlock1.setDisjunction(null);
        ruleBlock1.setImplication(new Minimum());
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
        outputVariable1.setLockValueInRange(false);
        outputVariable1.setLockPreviousValue(true);
        outputVariable1.setDefaultValue(Double.NaN);
        outputVariable1.setDefuzzifier(new WeightedAverage());
        outputVariable1.fuzzyOutput().setAggregation(null);
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
        outputVariable2.setLockValueInRange(false);
        outputVariable2.setLockPreviousValue(true);
        outputVariable2.setDefaultValue(Double.NaN);
        outputVariable2.setDefuzzifier(new WeightedAverage());
        outputVariable2.fuzzyOutput().setAggregation(null);
        outputVariable2.addTerm(Function.create("fx", "sin(inputX)/inputX", engine));
        engine.addOutputVariable(outputVariable2);

        OutputVariable outputVariable3 = new OutputVariable();
        outputVariable3.setName("diffFx");
        outputVariable3.setRange(-1, 1);
        outputVariable3.setLockValueInRange(false);
        outputVariable3.setLockPreviousValue(false);
        outputVariable3.setDefaultValue(Double.NaN);
        outputVariable3.setDefuzzifier(new WeightedAverage());
        outputVariable3.fuzzyOutput().setAggregation(null);
        outputVariable3.addTerm(Function.create("diff", "fabs(outputFx-trueFx)", engine));
        engine.addOutputVariable(outputVariable3);

        RuleBlock ruleBlock1 = new RuleBlock();
        ruleBlock1.setName("");
        ruleBlock1.setConjunction(null);
        ruleBlock1.setDisjunction(null);
        ruleBlock1.setImplication(new AlgebraicProduct());
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

    public void exportAllExamples(String from, String to, String sourceBase, String targetBase) throws Exception {
        List<String> examples = new ArrayList<String>();
        examples.add("/mamdani/AllTerms");
        examples.add("/mamdani/SimpleDimmer");
//        examples.add("/mamdani/Laundry");
//        examples.add("/mamdani/SimpleDimmerInverse");
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

        List<Pair<Exporter, Importer>> tests = new ArrayList<Pair<Exporter, Importer>>();
        tests.add(new Pair<Exporter, Importer>(new FllExporter(), new FllImporter()));
        tests.add(new Pair<Exporter, Importer>(new FclExporter(), new FclImporter()));
        tests.add(new Pair<Exporter, Importer>(new FisExporter(), new FisImporter()));

        List<String> errors = new LinkedList<String>();
        for (int i = 0; i < examples.size(); ++i) {
            FuzzyLite.logger().log(Level.INFO, "Processing {0}/{1}: {2}",
                    new Object[]{(i + 1), examples.size(), examples.get(i)});

            Engine engine;

            //READING
            final String inputFile = sourceBase + examples.get(i) + "." + from;
            BufferedReader source = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inputFile), FuzzyLite.UTF_8));
            try {
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = source.readLine()) != null) {
                    text.append(line).append("\n");
                }
                engine = importer.fromString(text.toString());
            } catch (Exception ex) {
                errors.add(ex.toString() + ": " + inputFile);
                FuzzyLite.logger().log(Level.SEVERE, "{0}: {1}",
                        new String[]{ex.toString(), inputFile});
                continue;
            } finally {
                source.close();
            }

            //WRITING
            final String output = targetBase + examples.get(i) + "." + to;
            File outputFile = new File(output);
            try {
                if (!outputFile.createNewFile()) {
                    FuzzyLite.logger().log(Level.FINE, "Replacing file {0}", output);
                }
            } catch (Exception ex) {
                errors.add(ex.toString() + ": " + output);
                FuzzyLite.logger().log(Level.SEVERE, "{0}: {1}",
                        new String[]{ex.toString(), output});
                return;
            }

            BufferedWriter target = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), FuzzyLite.UTF_8));
            try {
                for (Pair<Exporter, Importer> imex : tests) {
                    FuzzyLite.logger().info(String.format("Converting from %s to %s",
                            imex.getSecond().getClass().getSimpleName(),
                            imex.getFirst().getClass().getSimpleName()));
                    String out = imex.getFirst().toString(engine);
                    Engine copy = imex.getSecond().fromString(out);
                    String out_copy = imex.getFirst().toString(copy);

                    if (!out.equals(out_copy)) {
                        errors.add(String.format("[imex error] different results <%s,%s> at %s.%s",
                                imex.getFirst().getClass().getSimpleName(),
                                imex.getFirst().getClass().getSimpleName(),
                                examples.get(i), from));
                    }
                }

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
                            + "public class " + Op.validName(className) + "{\n"
                            + "public static void main(String[] args){\n"
                            + exporter.toString(engine)
                            + "\n}\n}\n");
                } else {
                    target.write(exporter.toString(engine));
                }
            } catch (Exception ex) {
                errors.add(ex.toString() + ": " + output);
                FuzzyLite.logger().log(Level.SEVERE, "{0}: {1}",
                        new String[]{ex.toString(), output});
            } finally {
                target.close();
            }
        }

        if (errors.isEmpty()) {
            FuzzyLite.logger().info("No errors were found exporting files");
        } else {
            FuzzyLite.logger().log(Level.SEVERE,
                    "Errors were encountered while exporting:\n{0}",
                    Op.join(errors, "\n"));
            throw new RuntimeException(Op.join(errors, "\n"));
        }
    }

    public void benchmarkExamples(String path, int runs) {
        List<Op.Pair<String, Integer>> examples = new LinkedList<Op.Pair<String, Integer>>();
        examples.add(new Op.Pair<String, Integer>("/mamdani/AllTerms", (int) 1e4));
        examples.add(new Op.Pair<String, Integer>("/mamdani/SimpleDimmer", (int) 1e5));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/mam21", 128));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/mam22", 128));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/shower", 256));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/tank", 256));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/tank2", 512));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/tipper", 256));
        examples.add(new Op.Pair<String, Integer>("/mamdani/matlab/tipper1", (int) 1e5));
        examples.add(new Op.Pair<String, Integer>("/mamdani/octave/investment_portfolio", 256));
        examples.add(new Op.Pair<String, Integer>("/mamdani/octave/mamdani_tip_calculator", 256));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/approximation", (int) 1e6));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/SimpleDimmer", (int) 2e6));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/fpeaks", 512));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/invkine1", 256));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/invkine2", 256));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/juggler", 512));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/membrn1", 1024));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/membrn2", 512));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/slbb", 20));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/slcp", 20));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/slcp1", 15));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/slcpp1", 9));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/sltbu_fl", 128));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/sugeno1", (int) 2e6));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/tanksg", 1024));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/matlab/tippersg", 1024));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/octave/cubic_approximator", (int) 2e6));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/octave/heart_disease_risk", 1024));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/octave/linear_tip_calculator", 1024));
        examples.add(new Op.Pair<String, Integer>("/takagi-sugeno/octave/sugeno_tip_calculator", 512));
        examples.add(new Op.Pair<String, Integer>("/tsukamoto/tsukamoto", (int) 1e6));

        for (Op.Pair<String, Integer> example : examples) {
            StringBuilder message = new StringBuilder();
            message.append(example.getFirst()).append("\t").append(example.getSecond());
            FuzzyLite.logger().info(message.toString());
        }

        FllImporter importer = new FllImporter();
        FldExporter exporter = new FldExporter();
        exporter.setExportHeaders(false);
        exporter.setExportInputValues(false);
        exporter.setExportOutputValues(false);
        NullWriter nullWriter = new NullWriter();
        try {
            for (Op.Pair<String, Integer> example : examples) {
                Engine engine = importer.fromFile(new File(path + example.getFirst() + ".fll"));
                int results = (int) Math.pow(example.getSecond(), engine.numberOfInputVariables());
                double[] time = new double[runs];

                for (int run = 0; run < runs; ++run) {
                    long start = System.currentTimeMillis();
                    exporter.write(engine, nullWriter, results, FldExporter.ScopeOfValues.AllVariables);
                    long end = System.currentTimeMillis();
                    time[run] = (end - start) / 1e3;
                }

                double mean = Op.mean(time);
                double stdev = Op.standardDeviation(time);
                StringBuilder message = new StringBuilder();
                message.append(Op.str(mean)).append("\t").append(Op.str(stdev))
                        .append("\t").append(Op.join(time, " "))
                        .append(" ").append(example.getFirst());
//                FuzzyLite.logger().info(message.toString());
                System.out.println(message.toString());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        nullWriter.close();
    }

    public static void main(String[] args) {
        Console console = new Console();
        if (args.length == 0) {
            System.out.println(console.usage());
            return;
        }
        if ("benchmarks".equals(args[0])) {
            String path = ".";
            if (args.length >= 2) {
                path = args[1];
            }
            int runs = 10;
            if (args.length >= 3) {
                runs = Integer.parseInt(args[2]);
            }
            console.benchmarkExamples(path, runs);
            return;
        }
        if ("export-examples".equals(args[0])) {
            String path = ".";
            if (args.length >= 2) {
                path = args[1];
            }
            String outputPath = "/tmp/";
            if (args.length >= 3) {
                outputPath = args[2];
            }
            FuzzyLite.logger().log(Level.FINE, "Origin: {0}\nTarget: {1}",
                    new String[]{path, outputPath});
            FuzzyLite.setDecimals(3);
            try {
                /*
                mkdir -p fl/mamdani/matlab; mkdir -p fl/mamdani/octave; 
                mkdir -p fl/takagi-sugeno/matlab; mkdir -p fl/takagi-sugeno/octave
                mkdir -p fl/tsukamoto
                 */
                console.exportAllExamples("fll", "fll", path, outputPath);
//                exportAllExamples("fll", "fcl", path, outputPath);
//                exportAllExamples("fll", "fis", path, outputPath);
//                exportAllExamples("fll", "cpp", path, outputPath);
//                exportAllExamples("fll", "java", path, outputPath);
                FuzzyLite.setDecimals(8);
//                exportAllExamples("fll", "fld", path, outputPath);
            } catch (Exception ex) {
                FuzzyLite.logger().log(Level.SEVERE, ex.toString(), ex);
                throw new RuntimeException(ex);
            }
            return;
        }
        try {
            Map<String, String> options = console.parse(args);
            console.process(options);
        } catch (Exception ex) {
            FuzzyLite.logger().log(Level.SEVERE, ex.toString(), ex);
        }
    }
}
