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
import com.fuzzylite.imex.FldExporter;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.imex.FllImporter;
import com.fuzzylite.imex.Importer;
import com.fuzzylite.imex.JavaExporter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author jcrada
 */
public class Main {

    private static void exportAllExamples(String from, String to) throws Exception {
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

        String sourceBase = "/home/jcrada/Development/fl/fuzzylite/examples";
        String targetBase = "/tmp/fl/";

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
            exporter = new FldExporter(" ", 1024);
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
            System.out.println("Processing " + (i + 1) + "/" + examples.size());
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
                return ;
            }
        }
        if (errors.toString().isEmpty()) {
            FuzzyLite.logger().info("No errors were found exporting files");
        } else {
            FuzzyLite.logger().info("The following errors were encountered while exporting:\n"
                    + errors.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        FuzzyLite.logger().setLevel(Level.WARNING);
        FuzzyLite.setDecimals(8);
        exportAllExamples("fis", "fll");
        exportAllExamples("fis", "fcl");
        exportAllExamples("fis", "fis");
        exportAllExamples("fis", "cpp");
        exportAllExamples("fis", "java");
        exportAllExamples("fis", "fld");
//TODO: Check logs
//        Console.main(args);
    }
}
