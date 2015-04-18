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
import com.fuzzylite.Op;
import com.fuzzylite.Op.Pair;
import com.fuzzylite.defuzzifier.Bisector;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.LargestOfMaximum;
import com.fuzzylite.defuzzifier.MeanOfMaximum;
import com.fuzzylite.defuzzifier.SmallestOfMaximum;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.defuzzifier.WeightedSum;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.norm.s.AlgebraicSum;
import com.fuzzylite.norm.s.BoundedSum;
import com.fuzzylite.norm.s.DrasticSum;
import com.fuzzylite.norm.s.EinsteinSum;
import com.fuzzylite.norm.s.HamacherSum;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.s.NilpotentMaximum;
import com.fuzzylite.norm.s.NormalizedSum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.norm.t.BoundedDifference;
import com.fuzzylite.norm.t.DrasticProduct;
import com.fuzzylite.norm.t.EinsteinProduct;
import com.fuzzylite.norm.t.HamacherProduct;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.norm.t.NilpotentMinimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class FclImporter extends Importer {

    @Override
    public Engine fromString(String fcl) {
        Engine engine = new Engine();

        Map<String, String> tags = new HashMap<String, String>();
        tags.put("VAR_INPUT", "END_VAR");
        tags.put("VAR_OUTPUT", "END_VAR");
        tags.put("FUZZIFY", "END_FUZZIFY");
        tags.put("DEFUZZIFY", "END_DEFUZZIFY");
        tags.put("RULEBLOCK", "END_RULEBLOCK");

        String currentTag = "", closingTag = "";
        StringBuilder block = new StringBuilder();
        BufferedReader fclReader = new BufferedReader(new StringReader(fcl));
        int lineNumber = 0;
        String line;
        try {
            while ((line = fclReader.readLine()) != null) {
                ++lineNumber;
                List<String> comments = Op.split(line, "//");
                if (comments.size() > 1) {
                    line = comments.get(0);
                }
                comments = Op.split(line, "#");
                if (comments.size() > 1) {
                    line = comments.get(0);
                }
                line = line.trim();
                // (%) indicates a comment only when used at the start of line
                if (line.isEmpty() || line.charAt(0) == '%' || line.charAt(0) == '#'
                        || "//".equals(line.substring(0, 2))) {
                    continue;
                }

                line = line.replaceAll(Pattern.quote(";"), "");
                StringTokenizer tokenizer = new StringTokenizer(line);
                String firstToken = tokenizer.nextToken();

                if ("FUNCTION_BLOCK".equals(firstToken)) {
                    if (tokenizer.hasMoreTokens()) {
                        StringBuilder name = new StringBuilder();
                        name.append(tokenizer.nextToken());
                        while (tokenizer.hasMoreTokens()) {
                            name.append(" ").append(tokenizer.nextToken());
                        }
                        engine.setName(name.toString());
                    }
                    continue;
                }

                if ("END_FUNCTION_BLOCK".equals(firstToken)) {
                    break;
                }

                if (currentTag.isEmpty()) {
                    if (!tags.containsKey(firstToken)) {
                        throw new RuntimeException(String.format(
                                "[syntax error] unknown block definition <%s> in line <%d>: %s",
                                firstToken, lineNumber, line));
                    }

                    currentTag = firstToken;
                    closingTag = tags.get(firstToken);
                    block.setLength(0);
                    block.append(line).append("\n");
                    continue;
                }

                if (!currentTag.isEmpty()) {
                    if (firstToken.equals(closingTag)) {
                        processBlock(currentTag, block.toString(), engine);
                        currentTag = "";
                        closingTag = "";
                    } else if (tags.containsKey(firstToken)) {
                        //if opening new block without closing the previous one
                        throw new RuntimeException(String.format(
                                "[syntax error] expected <%s> before <%s> in line: %s",
                                closingTag, firstToken, line));
                    } else {
                        block.append(line).append("\n");
                    }
                    continue;
                }

                if (!currentTag.isEmpty()) {
                    String error = "[syntax error] ";
                    if (block.length() > 0) {
                        error += String.format("expected <%s> for block:\n%s", closingTag, block.toString());
                    } else {
                        error += String.format("expected <%s>, but not found", closingTag);
                    }
                    throw new RuntimeException(error);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return engine;
    }

    protected void processBlock(String tag, String block, Engine engine) throws Exception {
        if ("VAR_INPUT".equals(tag) || "VAR_OUTPUT".equals(tag)) {
            processVar(tag, block, engine);
        } else if ("FUZZIFY".equals(tag)) {
            processFuzzify(block, engine);
        } else if ("DEFUZZIFY".equals(tag)) {
            processDefuzzify(block, engine);
        } else if ("RULEBLOCK".equals(tag)) {
            processRuleBlock(block, engine);
        } else {
            throw new RuntimeException(String.format(
                    "[syntax error] unexpected tag <%s> for block:\n%s",
                    tag, block));
        }
    }

    protected void processVar(String tag, String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        reader.readLine();//discard first line (VAR_INPUT)
        String line;
        while ((line = reader.readLine()) != null) {
            List<String> token = Op.split(line, ":");
            if (token.size() != 2) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected property of type "
                        + "(key : value) in line: %s", line));
            }
            String name = Op.validName(token.get(0));
            if ("VAR_INPUT".equals(tag)) {
                engine.addInputVariable(new InputVariable(name));
            } else if ("VAR_OUTPUT".equals(tag)) {
                engine.addOutputVariable(new OutputVariable(name));
            } else {
                throw new RuntimeException(String.format(
                        "[syntax error] unexpected tag <%s> in line: %s",
                        tag, line));
            }
        }

    }

    protected void processFuzzify(String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        String line = reader.readLine();
        String name;
        int index = line.indexOf(' ');
        if (index >= 0) {
            name = Op.validName(line.substring(index + 1));
        } else {
            throw new RuntimeException("[syntax error] expected name of input variable in line: " + line);
        }
        if (!engine.hasInputVariable(name)) {
            throw new RuntimeException(String.format(
                    "[syntax error] engine does not contain input variable <%s> from line: %s",
                    name, line));
        }
        InputVariable inputVariable = engine.getInputVariable(name);
        while ((line = reader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String firstToken = tokenizer.nextToken();
            if ("RANGE".equals(firstToken)) {
                Pair<Double, Double> range = parseRange(line);
                inputVariable.setRange(range.getFirst(), range.getSecond());
            } else if ("ENABLED".equals(firstToken)) {
                inputVariable.setEnabled(parseEnabled(line));
            } else if ("TERM".equals(firstToken)) {
                inputVariable.addTerm(parseTerm(line, engine));
            } else {
                throw new RuntimeException(String.format(
                        "[syntax error] token <%s> not recognized", firstToken));
            }
        }
    }

    protected void processDefuzzify(String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        String line = reader.readLine();
        String name;
        int index = line.indexOf(' ');
        if (index >= 0) {
            name = Op.validName(line.substring(index + 1));
        } else {
            throw new RuntimeException("[syntax error] expected name of output variable in line: " + line);
        }
        if (!engine.hasOutputVariable(name)) {
            throw new RuntimeException(String.format(
                    "[syntax error] engine does not contain output variable <%s> from line: %s",
                    name, line));
        }
        OutputVariable outputVariable = engine.getOutputVariable(name);
        while ((line = reader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String firstToken = tokenizer.nextToken();

            if ("TERM".equals(firstToken)) {
                outputVariable.addTerm(parseTerm(line, engine));
            } else if ("METHOD".equals(firstToken)) {
                outputVariable.setDefuzzifier(parseDefuzzifier(line));
            } else if ("ACCU".equals(firstToken)) {
                outputVariable.fuzzyOutput().setAccumulation(parseSNorm(line));
            } else if ("DEFAULT".equals(firstToken)) {
                Pair<Double, Boolean> defaultAndLock = parseDefaultValue(line);
                outputVariable.setDefaultValue(defaultAndLock.getFirst());
                outputVariable.setLockPreviousOutputValue(defaultAndLock.getSecond()
                        || outputVariable.isLockPreviousOutputValue());
            } else if ("RANGE".equals(firstToken)) {
                Pair<Double, Double> range = parseRange(line);
                outputVariable.setRange(range.getFirst(), range.getSecond());
            } else if ("LOCK".equals(firstToken)) {
                Pair<Boolean, Boolean> output_range = parseLocks(line);
                outputVariable.setLockPreviousOutputValue(output_range.getFirst());
                outputVariable.setLockOutputValueInRange(output_range.getSecond());
            } else if ("ENABLED".equals(firstToken)) {
                outputVariable.setEnabled(parseEnabled(line));
            } else {
                throw new RuntimeException(String.format(
                        "[syntax error] unexpected token <%s>", firstToken));
            }
        }
    }

    protected void processRuleBlock(String block, Engine engine) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(block));
        String line = reader.readLine();
        String name = "";
        int index = line.indexOf(' ');
        if (index >= 0) {
            name = line.substring(index + 1); //does not need to be valid name
        }
        RuleBlock ruleBlock = new RuleBlock(name);
        engine.addRuleBlock(ruleBlock);

        while ((line = reader.readLine()) != null) {
            String firstToken = line.substring(0, line.indexOf(' '));
            if ("AND".equals(firstToken)) {
                ruleBlock.setConjunction(parseTNorm(line));
            } else if ("OR".equals(firstToken)) {
                ruleBlock.setDisjunction(parseSNorm(line));
            } else if ("ACT".equals(firstToken)) {
                ruleBlock.setActivation(parseTNorm(line));
            } else if ("ENABLED".equals(firstToken)) {
                ruleBlock.setEnabled(parseEnabled(line));
            } else if ("RULE".equals(firstToken)) {
                int ruleStart = line.indexOf(':');
                if (ruleStart < 0) {
                    ruleStart = "RULE".length();
                }
                String ruleText = line.substring(ruleStart + 1).trim();
                Rule rule = new Rule(ruleText);
                try {
                    rule.load(engine);
                } finally {
                    ruleBlock.addRule(rule);
                }
            } else {
                throw new RuntimeException(String.format(
                        "[syntax error] keyword <%s> not recognized in line %s",
                        firstToken, line));
            }
        }
    }

    protected TNorm parseTNorm(String line) {
        List<String> token = Op.split(line, ":");
        if (token.size() != 2) {
            throw new RuntimeException("[syntax error] "
                    + "expected property of type (key : value) in line: " + line);
        }
        String name = token.get(1).trim();
        String className = name;
        if ("NONE".equals(name)) {
            className = "";
        } else if ("MIN".equals(name)) {
            className = Minimum.class.getSimpleName();
        } else if ("PROD".equals(name)) {
            className = AlgebraicProduct.class.getSimpleName();
        } else if ("BDIF".equals(name)) {
            className = BoundedDifference.class.getSimpleName();
        } else if ("DPROD".equals(name)) {
            className = DrasticProduct.class.getSimpleName();
        } else if ("EPROD".equals(name)) {
            className = EinsteinProduct.class.getSimpleName();
        } else if ("HPROD".equals(name)) {
            className = HamacherProduct.class.getSimpleName();
        } else if ("NMIN".equals(name)) {
            className = NilpotentMinimum.class.getSimpleName();
        }
        return FactoryManager.instance().tnorm().constructObject(className);
    }

    protected SNorm parseSNorm(String line) {
        List<String> token = Op.split(line, ":");
        if (token.size() != 2) {
            throw new RuntimeException("[syntax error] "
                    + "expected property of type (key : value) in line: " + line);
        }
        String name = token.get(1).trim();
        String className = name;
        if ("NONE".equals(name)) {
            className = "";
        } else if ("MAX".equals(name)) {
            className = Maximum.class.getSimpleName();
        } else if ("ASUM".equals(name)) {
            className = AlgebraicSum.class.getSimpleName();
        } else if ("BSUM".equals(name)) {
            className = BoundedSum.class.getSimpleName();
        } else if ("NSUM".equals(name)) {
            className = NormalizedSum.class.getSimpleName();
        } else if ("DSUM".equals(name)) {
            className = DrasticSum.class.getSimpleName();
        } else if ("ESUM".equals(name)) {
            className = EinsteinSum.class.getSimpleName();
        } else if ("HSUM".equals(name)) {
            className = HamacherSum.class.getSimpleName();
        } else if ("NMAX".equals(name)) {
            className = NilpotentMaximum.class.getSimpleName();
        }
        return FactoryManager.instance().snorm().constructObject(className);
    }

    protected Term parseTerm(String line, Engine engine) {
        String spacedLine = "";
        for (char c : line.toCharArray()) {
            if (c == '(' || c == ')' || c == ',') {
                spacedLine += " " + c + " ";
            } else if (c == ':') {
                spacedLine += " :";
            } else if (c == '=') {
                spacedLine += "= ";
            } else {
                spacedLine += c;
            }
        }

        final int S_KWTERM = 1, S_NAME = 2, S_ASSIGN = 3,
                S_TERM_CLASS = 4, S_PARAMETERS = 5;
        int state = S_KWTERM;
        StringTokenizer tokenizer = new StringTokenizer(spacedLine);
        String token, name = "", termClass = "";
        List<String> parameters = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (state == S_KWTERM && "TERM".equals(token)) {
                state = S_NAME;
                continue;
            }
            if (state == S_NAME) {
                name = token;
                state = S_ASSIGN;
                continue;
            }
            if (state == S_ASSIGN && ":=".equals(token)) {
                state = S_TERM_CLASS;
                continue;
            }
            if (state == S_TERM_CLASS) {
                if (Op.isNumeric(token)) {
                    termClass = Constant.class.getSimpleName();
                    parameters.add(token);
                } else if ("(".equals(token)) {
                    termClass = Discrete.class.getSimpleName();
                } else {
                    termClass = token;
                }
                state = S_PARAMETERS;
                continue;
            }
            if (state == S_PARAMETERS) {
                if (!Function.class.getSimpleName().equals(termClass)
                        && ("(".equals(token) || ")".equals(token) || ",".equals(token))) {
                    continue;
                }
                if (";".equals(token)) {
                    break;
                }
                parameters.add(token.trim());
            }
        }

        if (state <= S_ASSIGN) {
            throw new RuntimeException("[syntax error] malformed term in line: " + line);
        }

        try {
            Term result = FactoryManager.instance().term().constructObject(termClass);
            Term.updateReference(result, engine);
            result.setName(Op.validName(name));
            String separator = " ";
            if (result instanceof Function) {
                separator = "";
            }
            result.configure(Op.join(parameters, separator));
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected Defuzzifier parseDefuzzifier(String line) {
        List<String> token = Op.split(line, ":");
        if (token.size() != 2) {
            throw new RuntimeException("[syntax error] "
                    + "expected property of type (key : value) in line: " + line);
        }
        String name = token.get(1).trim();
        String className = name;
        if ("NONE".equals(name)) {
            className = "";
        }
        if ("COG".equals(className)) {
            className = Centroid.class.getSimpleName();
        } else if ("COA".equals(className)) {
            className = Bisector.class.getSimpleName();
        } else if ("LM".equals(className)) {
            className = SmallestOfMaximum.class.getSimpleName();
        } else if ("RM".equals(className)) {
            className = LargestOfMaximum.class.getSimpleName();
        } else if ("MM".equals(className)) {
            className = MeanOfMaximum.class.getSimpleName();
        } else if ("COGS".equals(className)) {
            className = WeightedAverage.class.getSimpleName();
        } else if ("COGSS".equals(className)) {
            className = WeightedSum.class.getSimpleName();
        }
        return FactoryManager.instance().defuzzifier().constructObject(className);
    }

    protected Pair<Double, Boolean> parseDefaultValue(String line) {
        List<String> token = Op.split(line, ":=");
        if (token.size() != 2) {
            throw new RuntimeException("[syntax error] "
                    + "expected property of type (key := value) in line: " + line);
        }
        List<String> values = Op.split(token.get(1), "|");
        String defaultValue = values.get(0).trim();
        String nc = "";
        if (values.size() == 2) {
            nc = values.get(1).trim();
        }
        double value;
        try {
            value = Op.toDouble(defaultValue);
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "[syntax error] expected numeric value, but found <%s> in line %s",
                    defaultValue, line));
        }

        boolean lockPreviousOutput = nc.equals("NC");
        if (!(lockPreviousOutput || nc.isEmpty())) {
            throw new RuntimeException(String.format(
                    "[syntax error] expected keyword <NC>, but found <%s> in line: %s",
                    nc, line));
        }
        return new Pair<Double, Boolean>(value, lockPreviousOutput);
    }

    protected Pair<Double, Double> parseRange(String line) {
        List<String> token = Op.split(line, ":=");
        if (token.size() != 2) {
            throw new RuntimeException("[syntax error] "
                    + "expected property of type (key := value) in line: " + line);
        }

        String rangeToken = token.get(1);

        String range = "";
        for (char c : rangeToken.toCharArray()) {
            if (c == '(' || c == ')' || c == ' ' || c == ';') {
                continue;
            }
            range += c;
        }

        token = Op.split(range, "..");
        if (token.size() != 2) {
            throw new RuntimeException(String.format("[syntax error] expected property of type"
                    + " 'start .. end', but found <%s> in line: %s", range, line));
        }

        double minimum, maximum;
        int index = -1;
        try {
            minimum = Op.toDouble(token.get(index = 0));
            maximum = Op.toDouble(token.get(index = 1));
        } catch (Exception ex) {
            throw new RuntimeException(String.format(
                    "[syntax error] expected numeric value, but found <%s> in line %s",
                    token.get(index), line));
        }

        return new Pair<Double, Double>(minimum, maximum);
    }

    protected Pair<Boolean, Boolean> parseLocks(String line) {
        int index = line.indexOf(':');
        if (index < 0) {
            throw new RuntimeException("[syntax error] expected property of type "
                    + "'key : value' in line: " + line);
        }
        boolean output, range;
        String value = line.substring(index + 1);
        List<String> flags = Op.split(value, "|");
        if (flags.size() == 1) {
            String flag = flags.get(0).trim();
            output = "PREVIOUS".equals(flag);
            range = ("RANGE".equals(flag));
            if (!(output || range)) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected locking flags "
                        + "<PREVIOUS|RANGE>, but found <%s> in line: %s", flag, line));
            }
        } else if (flags.size() == 2) {
            String flagA = flags.get(0).trim();
            String flagB = flags.get(1).trim();
            output = ("PREVIOUS".equals(flagA) || "PREVIOUS".equals(flagB));
            range = ("RANGE".equals(flagA) || "RANGE".equals(flagB));
            if (!(output && range)) {
                throw new RuntimeException(String.format(
                        "[syntax error] expected locking flags <PREVIOUS|RANGE>, "
                        + "but found <%s|%s> in line %s", flags.get(0), flags.get(1), line));
            }
        } else {
            throw new RuntimeException(String.format(
                    "[syntax error] expected locking flags <PREVIOUS|RANGE>, "
                    + "but found <%s> in line: ", value, line));
        }

        return new Pair<Boolean, Boolean>(output, range);
    }

    protected boolean parseEnabled(String line) {
        List<String> tokens = Op.split(line, ":");
        if (tokens.size() != 2) {
            throw new RuntimeException("[syntax error] expected property of type "
                    + "(key : value) in line: " + line);
        }

        String bool = tokens.get(1).trim();
        if ("TRUE".equals(bool)) {
            return true;
        }
        if ("FALSE".equals(bool)) {
            return false;
        }
        throw new RuntimeException("[syntax error] expected boolean <TRUE|FALSE>, "
                + "but found <" + line + ">");
    }

    @Override
    public FclImporter clone() throws CloneNotSupportedException {
        return (FclImporter) super.clone();
    }

}
