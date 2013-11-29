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

import com.fuzzylite.imex.CppExporter;
import com.fuzzylite.imex.Exporter;
import com.fuzzylite.imex.FclExporter;
import com.fuzzylite.imex.FclImporter;
import com.fuzzylite.imex.FisExporter;
import com.fuzzylite.imex.FisImporter;
import com.fuzzylite.imex.Importer;
import com.fuzzylite.imex.JavaExporter;
import com.fuzzylite.imex.ResultExporter;
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
    public static final String KW_RESULT_RESOLUTION = "-res";
    public static final String KW_RESULT_SEPARATOR = "-sep";

    public static String usage() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put(KW_INPUT_FILE, "inputfile");
        options.put(KW_INPUT_FORMAT, "fis,fcl");
        options.put(KW_OUTPUT_FILE, "outputfile");
        options.put(KW_OUTPUT_FORMAT, "fis,fcl,cpp,java,out");
        options.put(KW_RESULT_RESOLUTION, "resolution");
        options.put(KW_RESULT_SEPARATOR, "separator");

        StringBuilder result = new StringBuilder();
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
        result.append("Visit http://www.fuzzylite.com for more information.\n");
        return result.toString();
    }

    public static Map<String, String> parse(String[] args) {
        Map<String, String> options = new HashMap<>();
        String key, value;
        for (int i = 0; i < args.length - 1; i += 2) {
            key = args[i];
            value = args[i + 1];
            options.put(key, value);
        }
        if (options.size() == 1) {
            Map.Entry<String, String> in_out = options.entrySet().iterator().next();
            if (!in_out.getKey().startsWith("-")) {
                options.put(KW_INPUT_FILE, in_out.getKey());
                options.put(KW_OUTPUT_FILE, in_out.getValue());
            }
        }
        return options;
    }

    public static void process(Map<String, String> options) throws Exception {
        String inputFilename = options.get(KW_INPUT_FILE);
        if (inputFilename == null) {
            throw new RuntimeException("[option error] no input file specified");
        }
        File inputFile = new File(inputFilename);
        if (!inputFile.exists()) {
            inputFile.createNewFile();
        }

        String inputFormat = options.get(KW_INPUT_FORMAT);
        if (inputFormat == null || inputFormat.isEmpty()) {
            int extensionIndex = inputFilename.lastIndexOf(".");
            if (extensionIndex >= 0) {
                inputFormat = inputFilename.substring(extensionIndex + 1);
            } else {
                throw new RuntimeException("[format error] unspecified format of input file");
            }
        }

        String outputFilename = options.get(KW_OUTPUT_FILE);
        Writer writer = null;
        if (outputFilename == null || outputFilename.isEmpty()) {
            writer = new StringWriter();
        } else {
            File outputFile = new File(outputFilename);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            writer = new FileWriter(outputFile);
        }

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

        StringBuilder textEngine = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String line = reader.readLine();
        while (line != null) {
            textEngine.append(line);
            line = reader.readLine();
        }
        reader.close();

        process(textEngine.toString(), writer, inputFormat, outputFormat, options);
        writer.flush();
        if (outputFilename == null || outputFilename.isEmpty()) {
            StringWriter stringWriter = (StringWriter) writer;
            System.out.println(stringWriter.getBuffer().toString());
        }
        writer.close();
    }

    public static void process(String input, Writer output,
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
        } else if ("out".equalsIgnoreCase(outputFormat)) {
            exporter = new ResultExporter();
        } else {
            throw new RuntimeException(String.format(
                    "[export error] format <%s> not supported", outputFormat));
        }

        Engine engine = importer.fromString(input);
        if (exporter instanceof ResultExporter) {
            ResultExporter resultExporter = (ResultExporter) exporter;
            String separator = " ";
            int resolution = ResultExporter.DEFAULT_RESOLUTION;
            if (options.containsKey(KW_RESULT_SEPARATOR)) {
                separator = options.get(KW_RESULT_SEPARATOR);
            }
            if (options.containsKey(KW_RESULT_RESOLUTION)) {
                resolution = Integer.parseInt(options.get(KW_RESULT_RESOLUTION));
            }
            resultExporter.toWriter(engine, output, separator, resolution);
        } else {
            output.write(exporter.toString(engine));
        }
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
