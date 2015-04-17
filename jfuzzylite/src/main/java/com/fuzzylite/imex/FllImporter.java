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
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.Op.Pair;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class FllImporter extends Importer {

    private String separator;

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
        Deque<String> queue = new ArrayDeque<String>();
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
            Pair<String, String> keyValue = parseKeyValue(line, ':');
            if ("InputVariable".equals(keyValue.getFirst())) {
                inputVariable.setName(keyValue.getSecond());
            } else if ("enabled".equals(keyValue.getFirst())) {
                inputVariable.setEnabled(parseBoolean(keyValue.getSecond()));
            } else if ("range".equals(keyValue.getFirst())) {
                Pair<Double, Double> range = parseRange(keyValue.getSecond());
                inputVariable.setRange(range.getFirst(), range.getSecond());
            } else if ("term".equals(keyValue.getFirst())) {
                inputVariable.addTerm(parseTerm(keyValue.getSecond(), engine));
            } else {
                throw new RuntimeException("[import error] "
                        + "key <" + keyValue.getFirst() + "> " + "not recognized in pair <"
                        + Op.join(":", keyValue.getFirst(), keyValue.getSecond()) + ">");
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
            Pair<String, String> keyValue = parseKeyValue(line, ':');
            if ("OutputVariable".equals(keyValue.getFirst())) {
                outputVariable.setName(keyValue.getSecond());
            } else if ("enabled".equals(keyValue.getFirst())) {
                outputVariable.setEnabled(parseBoolean(keyValue.getSecond()));
            } else if ("range".equals(keyValue.getFirst())) {
                Pair<Double, Double> range = parseRange(keyValue.getSecond());
                outputVariable.setRange(range.getFirst(), range.getSecond());
            } else if ("default".equals(keyValue.getFirst())) {
                outputVariable.setDefaultValue(Op.toDouble(keyValue.getSecond()));
            } else if ("lock-previous".equals(keyValue.getFirst())) {
                outputVariable.setLockPreviousOutputValue(parseBoolean(keyValue.getSecond()));
            } else if ("lock-range".equals(keyValue.getFirst())) {
                outputVariable.setLockOutputValueInRange(parseBoolean(keyValue.getSecond()));
            } else if ("defuzzifier".equals(keyValue.getFirst())) {
                outputVariable.setDefuzzifier(parseDefuzzifier(keyValue.getSecond()));
            } else if ("accumulation".equals(keyValue.getFirst())) {
                outputVariable.fuzzyOutput().setAccumulation(parseSNorm(keyValue.getSecond()));
            } else if ("term".equals(keyValue.getFirst())) {
                outputVariable.addTerm(parseTerm(keyValue.getSecond(), engine));
            } else {
                throw new RuntimeException("[import error] "
                        + "key <" + keyValue.getFirst() + "> " + "not recognized in pair <"
                        + Op.join(":", keyValue.getFirst(), keyValue.getSecond()) + ">");
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
            Pair<String, String> keyValue = parseKeyValue(line, ':');
            if ("RuleBlock".equals(keyValue.getFirst())) {
                ruleBlock.setName(keyValue.getSecond());
            } else if ("enabled".equals(keyValue.getFirst())) {
                ruleBlock.setEnabled(parseBoolean(keyValue.getSecond()));
            } else if ("conjunction".equals(keyValue.getFirst())) {
                ruleBlock.setConjunction(parseTNorm(keyValue.getSecond()));
            } else if ("disjunction".equals(keyValue.getFirst())) {
                ruleBlock.setDisjunction(parseSNorm(keyValue.getSecond()));
            } else if ("activation".equals(keyValue.getFirst())) {
                ruleBlock.setActivation(parseTNorm(keyValue.getSecond()));
            } else if ("rule".equals(keyValue.getFirst())) {
                Rule rule = new Rule();
                rule.setText(keyValue.getSecond());
                try {
                    rule.load(engine);
                } catch (Exception ex) {
                    FuzzyLite.logger().warning(ex.toString());
                }
                ruleBlock.addRule(rule);
            } else {
                throw new RuntimeException("[import error] "
                        + "key <" + keyValue.getFirst() + "> " + "not recognized in pair <"
                        + Op.join(":", keyValue.getFirst(), keyValue.getSecond()) + ">");
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
        Term term = FactoryManager.instance().term().constructObject(tokens.get(1));
        Term.updateReference(term, engine);
        term.setName(Op.validName(tokens.get(0)));
        StringBuilder parameters = new StringBuilder();
        for (int i = 2; i < tokens.size(); ++i) {
            parameters.append(tokens.get(i));
            if (i + 1 < tokens.size()) {
                parameters.append(" ");
            }
        }
        term.configure(parameters.toString());
        return term;
    }

    protected TNorm parseTNorm(String name) {
        if ("none".equals(name)) {
            return FactoryManager.instance().tnorm().constructObject("");
        }
        return FactoryManager.instance().tnorm().constructObject(name);
    }

    protected SNorm parseSNorm(String name) {
        if ("none".equals(name)) {
            return FactoryManager.instance().snorm().constructObject("");
        }
        return FactoryManager.instance().snorm().constructObject(name);
    }

    protected Defuzzifier parseDefuzzifier(String text) {
        List<String> parameters = Op.split(text, " ");
        String name = parameters.get(0);
        if ("none".equals(name)) {
            return FactoryManager.instance().defuzzifier().constructObject("");
        }
        Defuzzifier defuzzifier = FactoryManager.instance().defuzzifier().constructObject(name);
        if (parameters.size() > 1) {
            String parameter = parameters.get(1);
            if (defuzzifier instanceof IntegralDefuzzifier) {
                ((IntegralDefuzzifier) defuzzifier).setResolution(Integer.parseInt(parameter));
            } else if (defuzzifier instanceof WeightedDefuzzifier) {
                ((WeightedDefuzzifier) defuzzifier).setType(WeightedDefuzzifier.Type.valueOf(parameter));
            }
        }
        return defuzzifier;
    }

    protected Pair<Double, Double> parseRange(String text) {
        Pair<String, String> range = parseKeyValue(text, ' ');
        return new Pair<Double, Double>(Op.toDouble(range.getFirst()), Op.toDouble(range.getSecond()));
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

    protected Pair<String, String> parseKeyValue(String text, char separator) {
        int half = text.indexOf(separator);
        if (half < 0) {
            throw new RuntimeException("[syntax error] expected pair in the form "
                    + "<key" + separator + "value>, but found <" + text + ">");
        }
        Pair<String, String> result = new Pair<String, String>();
        result.setFirst(text.substring(0, half));
        result.setSecond(text.substring(half + 1));
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

    @Override
    public FllImporter clone() throws CloneNotSupportedException {
        return (FllImporter) super.clone();
    }

}
