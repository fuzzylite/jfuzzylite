/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.Op.Pair;
import com.fuzzylite.activation.Activation;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.factory.TNormFactory;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 The FllImporter class is an Importer that configures an Engine and its
 components utilizing the FuzzyLite Language (FLL), see
 [http://www.fuzzylite.com/fll-fld](http://www.fuzzylite.com/fll-fld) for more
 information.

 @author Juan Rada-Vilela, Ph.D.
 @see FllExporter
 @see Importer
 @since 4.0
 @todo parse methods returning respective instances from blocks of text
 */
public class FllImporter extends Importer {

    private String separator;

    public FllImporter() {
        this("\n");
    }

    public FllImporter(String separator) {
        this.separator = separator;
    }

    /**
     Gets the separator of the language (default separator is a new line '\n')

     @return the separator of the language
     */
    public String getSeparator() {
        return separator;
    }

    /**
     Sets the separator of the language (default separator is a new line '\n')

     @param separator is the separator of the language
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public Engine fromString(String code) {
        Engine engine = new Engine();

        final String fll = Op.join(Op.split(code, getSeparator()), "\n");
        String tag = "";
        List<String> block = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(fll));
        String line;

        while (true) {
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                break;
            }
            if (line == null) {
                break;
            }

            line = Op.split(line.trim(), "#", false).get(0); //remove comments
            int colon = line.indexOf(':');
            if (colon < 0) {
                throw new RuntimeException("[import error] " + "expected a colon at here: " + line);
            }
            String key = line.substring(0, colon).trim();
            String value = line.substring(colon + 1).trim();
            if ("Engine".equals(key)) {
                engine.setName(value);
                continue;
            } else if ("InputVariable".equals(key)
                    || "OutputVariable".equals(key)
                    || "RuleBlock".equals(key)) {
                try {
                    process(tag, Op.join(block, "\n"), engine);
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                block.clear();
                tag = key;
            } //else if (tag.isEmpty()) {}
            block.add(key + ":" + value);
        }
        try {
            process(tag, Op.join(block, "\n"), engine);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                //ignore
            }
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
            } else if ("description".equals(keyValue.getFirst())) {
                inputVariable.setDescription(keyValue.getSecond());
            } else if ("enabled".equals(keyValue.getFirst())) {
                inputVariable.setEnabled(parseBoolean(keyValue.getSecond()));
            } else if ("range".equals(keyValue.getFirst())) {
                Pair<Double, Double> range = parseRange(keyValue.getSecond());
                inputVariable.setRange(range.getFirst(), range.getSecond());
            } else if ("lock-range".equals(keyValue.getFirst())) {
                inputVariable.setLockValueInRange(parseBoolean(keyValue.getSecond()));
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
            } else if ("description".equals(keyValue.getFirst())) {
                outputVariable.setDescription(keyValue.getSecond());
            } else if ("enabled".equals(keyValue.getFirst())) {
                outputVariable.setEnabled(parseBoolean(keyValue.getSecond()));
            } else if ("range".equals(keyValue.getFirst())) {
                Pair<Double, Double> range = parseRange(keyValue.getSecond());
                outputVariable.setRange(range.getFirst(), range.getSecond());
            } else if ("default".equals(keyValue.getFirst())) {
                outputVariable.setDefaultValue(Op.toDouble(keyValue.getSecond()));
            } else if ("lock-previous".equals(keyValue.getFirst())) {
                outputVariable.setLockPreviousValue(parseBoolean(keyValue.getSecond()));
            } else if ("lock-range".equals(keyValue.getFirst())) {
                outputVariable.setLockValueInRange(parseBoolean(keyValue.getSecond()));
            } else if ("defuzzifier".equals(keyValue.getFirst())) {
                outputVariable.setDefuzzifier(parseDefuzzifier(keyValue.getSecond()));
            } else if ("aggregation".equals(keyValue.getFirst())) {
                outputVariable.fuzzyOutput().setAggregation(parseSNorm(keyValue.getSecond()));
            } else if ("accumulation".equals(keyValue.getFirst())) {
                outputVariable.fuzzyOutput().setAggregation(parseSNorm(keyValue.getSecond()));
                FuzzyLite.logger().warning("[warning] obsolete usage of identifier <accumulation: SNorm> in OutputVariable");
                FuzzyLite.logger().info("[information] from version 6.0, the identifier <aggregation: SNorm> should be used");
                FuzzyLite.logger().log(Level.INFO, "[backward compatibility] assumed "
                        + "<aggregation: {0}> instead of <accumulation: {0}>", keyValue.getSecond());
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
            } else if ("description".equals(keyValue.getFirst())) {
                ruleBlock.setDescription(keyValue.getSecond());
            } else if ("enabled".equals(keyValue.getFirst())) {
                ruleBlock.setEnabled(parseBoolean(keyValue.getSecond()));
            } else if ("conjunction".equals(keyValue.getFirst())) {
                ruleBlock.setConjunction(parseTNorm(keyValue.getSecond()));
            } else if ("disjunction".equals(keyValue.getFirst())) {
                ruleBlock.setDisjunction(parseSNorm(keyValue.getSecond()));
            } else if ("implication".equals(keyValue.getFirst())) {
                ruleBlock.setImplication(parseTNorm(keyValue.getSecond()));
            } else if ("activation".equals(keyValue.getFirst())) {
                TNormFactory tnorm = FactoryManager.instance().tnorm();
                //@todo remove backwards compatibility in version 7.0
                if (tnorm.hasConstructor(keyValue.getSecond())) {
                    ruleBlock.setImplication(parseTNorm(keyValue.getSecond()));
                    FuzzyLite.logger().warning("[warning] obsolete usage of identifier <activation: TNorm> in RuleBlock");
                    FuzzyLite.logger().info("[information] from version 6.0, the identifier <implication: TNorm> should be used");
                    FuzzyLite.logger().log(Level.INFO, "[backward compatibility] assumed "
                            + "<implication: {0}> instead of <activation: {0}>", keyValue.getSecond());
                } else {
                    ruleBlock.setActivation(parseActivation(keyValue.getSecond()));
                }
            } else if ("rule".equals(keyValue.getFirst())) {
                Rule rule = new Rule();
                rule.setText(keyValue.getSecond());
                try {
                    rule.load(engine);
                } catch (Exception ex) {
                    FuzzyLite.logger().log(Level.INFO, "Error loading rule: {0}\n{1}",
                            new String[]{rule.getText(), ex.toString()});
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
        term.updateReference(engine);
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

    protected Activation parseActivation(String name) {
        if ("none".equals(name)) {
            return FactoryManager.instance().activation().constructObject("");
        }
        List<String> tokens = Op.split(name, " ");
        Activation result = FactoryManager.instance().activation().constructObject(tokens.get(0));
        List<String> parameters = new LinkedList<String>();
        for (int i = 1; i < tokens.size(); ++i) {
            parameters.add(tokens.get(i));
        }
        result.configure(Op.join(parameters, " "));
        return result;
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

    @Override
    public FllImporter clone() throws CloneNotSupportedException {
        return (FllImporter) super.clone();
    }

}
