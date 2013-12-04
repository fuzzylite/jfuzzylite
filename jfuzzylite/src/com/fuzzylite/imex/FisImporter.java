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
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.Variable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author jcrada
 */
public class FisImporter extends Importer {

    protected static int and = 0, or = 1, imp = 2, agg = 3, defuzz = 4;

    @Override
    public Engine fromString(String fis) {
        Engine engine = new Engine();

        BufferedReader fisReader = new BufferedReader(new StringReader(fis));
        String line;
        List<String> sections = new ArrayList<>();
        try {
            while ((line = fisReader.readLine()) != null) {
                if (line.contains("//")) {
                    line = line.substring(0, line.indexOf("//"));
                }
                //TODO: ignore multiline comments
                if (line.contains("/*")) {
                    line = line.substring(0, line.indexOf("/*"));
                }
                if (line.contains("#")) {
                    line = line.substring(0, line.indexOf("/*"));
                }
                line = line.trim().replaceAll("'", "");
                // (%) indicates a comment only when used at the start of line
                if (line.isEmpty() || line.charAt(0) == '%') {
                    continue;
                }

                if (line.startsWith("[System]")
                        || line.startsWith("[Input")
                        || line.startsWith("[Output")
                        || line.startsWith("[Rules]")) {
                    sections.add(line);
                } else {
                    if (!sections.isEmpty()) {
                        int lastIndex = sections.size() - 1;
                        String section = sections.get(lastIndex);
                        section += "\n" + line;
                        sections.set(lastIndex, section);
                    } else {
                        throw new RuntimeException(String.format(
                                "[import error] line <%s> "
                                + "does not belong to any section", line));
                    }
                }
            }

            String[] methods = new String[5];
            for (String section : sections) {
                if (section.startsWith("[System]")) {
                    importSystem(section, engine, methods);
                } else if (section.startsWith("[Input")) {
                    importInput(section, engine);
                } else if (section.startsWith("Output")) {
                    importOutput(section, engine);
                } else if (section.startsWith("[Rules]")) {
                    importRules(section, engine);
                } else {
                    throw new RuntimeException("[import error] section <%s> not recognized");
                }
                engine.configure(tnorm(methods[and]), snorm(methods[or]),
                        tnorm(methods[imp]), snorm(methods[agg]),
                        defuzzifier(methods[defuzz]));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return engine;
    }

    protected void importSystem(String section, Engine engine, String[] methods) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(section));
        reader.readLine(); //ignore first line [System]
        String line;
        while ((line = reader.readLine()) != null) {
            String[] keyValue = line.split(Pattern.quote("="));
            String key = keyValue[0].trim();
            String value = "";
            for (int i = 1; i < keyValue.length; ++i) {
                value += keyValue[i];
            }
            value = value.trim();
            if ("Name".equals(key)) {
                engine.setName(value);
            } else if ("AndMethod".equals(key)) {
                methods[and] = value;
            } else if ("OrMethod".equals(key)) {
                methods[or] = value;
            } else if ("ImpMethod".equals(key)) {
                methods[imp] = value;
            } else if ("AggMethod".equals(key)) {
                methods[agg] = value;
            } else if ("DefuzzMethod".equals(key)) {
                methods[defuzz] = value;
            } else if ("Type".equals(key) || "Version".equals(key)
                    || "NumInputs".equals(key) || "NumOutputs".equals(key)
                    || "NumRules".equals(key) || "NumMFs".equals(key)) {
                //ignore because are redundant
            } else {
                throw new RuntimeException(String.format(
                        "[import error] token <%s> not recognized", key));
            }
        }
    }

    protected void importInput(String section, Engine engine) {

    }

    protected void importOutput(String section, Engine engine) {

    }

    protected void importRules(String section, Engine engine) {

    }

    protected String translateProposition(float code, Variable variable) {
        return "";
    }

    protected String tnorm(String name) {
        return "";
    }

    protected String snorm(String name) {
        return "";
    }

    protected String defuzzifier(String name) {
        return "";
    }

    protected Term extractTerm(String fis) {
        return null;
    }

    protected Term prepareTerm(Term term, Engine engine) {
        return term;
    }

    protected Term createInstance(String mClass, String name, List<String> parameters) {
        return null;
    }

    protected Op.Pair<Double, Double> extractRange(String range) {
        return null;
    }

}
