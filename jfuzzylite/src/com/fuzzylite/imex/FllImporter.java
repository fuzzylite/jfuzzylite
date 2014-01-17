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
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author jcrada
 */
public class FllImporter extends Importer {

    protected String separator;

    public FllImporter() {
        this("\n");
    }

    public FllImporter(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public Engine fromString(String fll) {
        Engine engine = new Engine();

        String tag = "";
        StringBuilder block = new StringBuilder();
        boolean processPending;
        BufferedReader reader = new BufferedReader(new StringReader(fll));
        String line = "";
        Deque<String> queue = new ArrayDeque<>();
        int lineNumber = 0;
        try {
            while (!queue.isEmpty() || (line = reader.readLine()) != null) {
                if (!queue.isEmpty()) {
                    line = queue.poll();
                } else {
                    line = clean(line);
                    if (line.isEmpty()) {
                        continue;
                    }
                    List<String> split = Op.split(line, separator);
                    line = clean(split.get(0));
                    for (int i = 1; i < split.size(); ++i) {
                        queue.offer(clean(split.get(i)));
                    }
                    ++lineNumber;
                }

                if (line.isEmpty()) {
                    continue;
                }
                int colon = line.indexOf(':');
                if (colon < 0) {
                    throw new RuntimeException("[import error] "
                            + "expected a colon at line " + lineNumber + ": " + line);
                }
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                if ("Engine".equals(key)) {
                    engine.setName(value);
                    continue;
                } else {
                    processPending = ("InputVariable".equals(key)
                            || "OutputVariable".equals(key)
                            || "RuleBlock".equals(key));
                }
                if (processPending) {
                    process(tag, block.toString(), engine);
                    block.setLength(0);
                    processPending = false;
                    tag = key;
                }
                block.append(String.format("%s:%s\n", key, value));
            }
            process(tag, block.toString(), engine);
            reader.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return engine;
    }

    protected void process(String tag, String block, Engine engine) throws Exception {
        if (tag.isEmpty()) {
            return;
        }
        if ("InputVariable".equals(tag)) {
            processInputVariable(block, engine);
        } else if ("OutputVariable".equals(tag)) {
            processOutputVariable(block, engine);
        } else if ("RuleBlock".equals(tag)) {
            processRuleBlock(block, engine);
        } else {
            throw new RuntimeException("[import error] block tag <" + tag + "> "
                    + "not recognized");
        }
    }

    protected void processInputVariable(String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        String line;
        InputVariable inputVariable = new InputVariable();
        engine.addInputVariable(inputVariable);
        while ((line = reader.readLine()) != null) {
            Op.Pair<String, String> keyValue = parseKeyValue(line, ':');
            if ("InputVariable".equals(keyValue.first)) {
                inputVariable.setName(keyValue.second);
            } else if ("enabled".equals(keyValue.first)) {
                inputVariable.setEnabled(parseBoolean(keyValue.second));
            } else if ("range".equals(keyValue.first)) {
                Op.Pair<Double, Double> range = parseRange(keyValue.second);
                inputVariable.setRange(range.first, range.second);
            } else if ("term".equals(keyValue.first)) {
                inputVariable.addTerm(parseTerm(keyValue.second, engine));
            } else {
                throw new RuntimeException("[import error] "
                        + "key <" + keyValue.first + "> " + "not recognized in pair <"
                        + Op.join(":", keyValue.first, keyValue.second) + ">");
            }
        }
        reader.close();
    }

    protected void processOutputVariable(String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        String line;
        OutputVariable outputVariable = new OutputVariable();
        engine.addOutputVariable(outputVariable);
        while ((line = reader.readLine()) != null) {
            Op.Pair<String, String> keyValue = parseKeyValue(line, ':');
            if ("OutputVariable".equals(keyValue.first)) {
                outputVariable.setName(keyValue.second);
            } else if ("enabled".equals(keyValue.first)) {
                outputVariable.setEnabled(parseBoolean(keyValue.second));
            } else if ("range".equals(keyValue.first)) {
                Op.Pair<Double, Double> range = parseRange(keyValue.second);
                outputVariable.setRange(range.first, range.second);
            } else if ("default".equals(keyValue.first)) {
                outputVariable.setDefaultValue(Op.toDouble(keyValue.second));
            } else if ("lock-valid".equals(keyValue.first)) {
                outputVariable.setLockValidOutput(parseBoolean(keyValue.second));
            } else if ("lock-range".equals(keyValue.first)) {
                outputVariable.setLockOutputRange(parseBoolean(keyValue.second));
            } else if ("defuzzifier".equals(keyValue.first)) {
                outputVariable.setDefuzzifier(parseDefuzzifier(keyValue.second));
            } else if ("accumulation".equals(keyValue.first)) {
                outputVariable.fuzzyOutput().setAccumulation(parseSNorm(keyValue.second));
            } else if ("term".equals(keyValue.first)) {
                outputVariable.addTerm(parseTerm(keyValue.second, engine));
            } else {
                throw new RuntimeException("[import error] "
                        + "key <" + keyValue.first + "> " + "not recognized in pair <"
                        + Op.join(":", keyValue.first, keyValue.second) + ">");
            }
        }
        reader.close();
    }

    protected void processRuleBlock(String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        String line;
        RuleBlock ruleBlock = new RuleBlock();
        engine.addRuleBlock(ruleBlock);
        while ((line = reader.readLine()) != null) {
            Op.Pair<String, String> keyValue = parseKeyValue(line, ':');
            if ("RuleBlock".equals(keyValue.first)) {
                ruleBlock.setName(keyValue.second);
            } else if ("enabled".equals(keyValue.first)) {
                ruleBlock.setEnabled(parseBoolean(keyValue.second));
            } else if ("conjunction".equals(keyValue.first)) {
                ruleBlock.setConjunction(parseTNorm(keyValue.second));
            } else if ("disjunction".equals(keyValue.first)) {
                ruleBlock.setDisjunction(parseSNorm(keyValue.second));
            } else if ("activation".equals(keyValue.first)) {
                ruleBlock.setActivation(parseTNorm(keyValue.second));
            } else if ("rule".equals(keyValue.first)) {
                ruleBlock.addRule(Rule.parse(keyValue.second, engine));
            } else {
                throw new RuntimeException("[import error] "
                        + "key <" + keyValue.first + "> " + "not recognized in pair <"
                        + Op.join(":", keyValue.first, keyValue.second) + ">");
            }
        }
        reader.close();
    }

    protected Term parseTerm(String text, Engine engine) {
        List<String> tokens = Op.split(text, " ");
        if (tokens.size() < 2) {
            throw new RuntimeException("[syntax error] "
                    + "expected a term in format <name class parameters>, "
                    + "but found <" + text + ">");
        }
        Term term = FactoryManager.instance().term().createInstance(tokens.get(1));
        term.setName(tokens.get(0));
        StringBuilder parameters = new StringBuilder();
        for (int i = 2; i < tokens.size(); ++i) {
            parameters.append(tokens.get(i));
            if (i + 1 < tokens.size()) {
                parameters.append(" ");
            }
        }
        term.configure(parameters.toString());
        //special cases
        if (term instanceof Linear) {
            ((Linear) term).inputVariables = new ArrayList<>(engine.getInputVariables());
        } else if (term instanceof Function) {
            Function function = (Function) term;
            function.setEngine(engine);
            function.load();
        }
        return term;
    }

    protected TNorm parseTNorm(String name) {
        if (name.isEmpty() || "none".equals(name)) {
            return null;
        }
        return FactoryManager.instance().tnorm().createInstance(name);
    }

    protected SNorm parseSNorm(String name) {
        if (name.isEmpty() || "none".equals(name)) {
            return null;
        }
        return FactoryManager.instance().snorm().createInstance(name);
    }

    protected Defuzzifier parseDefuzzifier(String text) {
        if (text.isEmpty() || "none".equals(text)) {
            return null;
        }
        List<String> parameters = Op.split(text, " ");
        Defuzzifier defuzzifier = FactoryManager.instance().
                defuzzifier().createInstance(parameters.get(0));
        if (defuzzifier instanceof IntegralDefuzzifier && parameters.size() > 1) {
            ((IntegralDefuzzifier) defuzzifier).setResolution(
                    Integer.parseInt(parameters.get(1)));
        }
        return defuzzifier;
    }

    protected Op.Pair<Double, Double> parseRange(String text) {
        Op.Pair<String, String> range = parseKeyValue(text, ' ');
        return new Op.Pair<>(Op.toDouble(range.first), Op.toDouble(range.second));
    }

    protected boolean parseBoolean(String bool) {
        if ("true".equals(bool)) {
            return true;
        }
        if ("false".equals(bool)) {
            return false;
        }
        throw new RuntimeException("[syntax error] expected boolean <true|false>, "
                + "but found <" + bool + ">");
    }

    protected Op.Pair<String, String> parseKeyValue(String text, char separator) {
        int half = text.indexOf(separator);
        if (half < 0) {
            throw new RuntimeException("[syntax error] expected pair in the form "
                    + "<key" + separator + "value>, but found <" + text + ">");
        }
        Op.Pair<String, String> result = new Op.Pair<>();
        result.first = text.substring(0, half);
        result.second = text.substring(half + 1);
        return result;
    }

    protected String clean(String line) {
        if (line.isEmpty()) {
            return line;
        }
        if (line.length() == 1) {
            return Character.isWhitespace(line.charAt(0)) ? "" : line;
        }
        int start = 0, end = line.length() - 1;
        while (start <= end && Character.isWhitespace(line.charAt(start))) {
            ++start;
        }
        int sharp = start;
        while (sharp <= end) {
            if (line.charAt(sharp) == '#') {
                end = sharp - 1;
                break;
            }
            ++sharp;
        }
        while (end >= start && (line.charAt(end) == '#'
                || Character.isWhitespace(line.charAt(end)))) {
            --end;
        }

        return line.substring(start, end + 1);
    }

}
