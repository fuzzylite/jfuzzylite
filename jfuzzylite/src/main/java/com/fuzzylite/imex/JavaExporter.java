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
import com.fuzzylite.activation.Activation;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.norm.Norm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

import java.util.List;
import java.util.regex.Pattern;

/**
 The JavaExporter class is an Exporter that translates an Engine and its
 components to the `Java` programming language using the `jfuzzylite` library.

 @author Juan Rada-Vilela, Ph.D.
 @see CppExporter
 @see Exporter
 @since 4.0
 */
public class JavaExporter extends Exporter {

    private boolean usingVariableNames;

    public JavaExporter() {
        this(true);
    }

    public JavaExporter(boolean usingVariableNames) {
        this.usingVariableNames = usingVariableNames;
    }

    /**
     Gets whether variables are exported using their names (e.g.,
     `power.setValue(Double.NaN)`) instead of numbered identifiers (e.g.,
     `inputVariable1.setValue(Double.NaN)`)

     @return whether variables are exported using their names
     */
    public boolean isUsingVariableNames() {
        return usingVariableNames;
    }

    /**
     Sets whether variables are exported using their names (e.g.,
     `power.setValue(Double.NaN)`) instead of numbered identifiers (e.g.,
     `inputVariable1.setValue(Double.NaN)`)

     @param usingVariableNames indicates whether variables are exported using
     their names
     */
    public void setUsingVariableNames(boolean usingVariableNames) {
        this.usingVariableNames = usingVariableNames;
    }

    @Override
    public String toString(Engine engine) {
        StringBuilder result = new StringBuilder();
        result.append("//Java code generated with " + FuzzyLite.LIBRARY + ".\n\n");
        result.append("Engine engine = new Engine();\n");
        result.append(String.format(
                "engine.setName(\"%s\");\n", engine.getName()));

        result.append("\n");

        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.append(toString(inputVariable, engine)).append("\n");
        }

        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.append(toString(outputVariable, engine)).append("\n");
        }

        for (RuleBlock ruleBlock : engine.getRuleBlocks()) {
            result.append(toString(ruleBlock, engine)).append("\n");
        }

        return result.toString();
    }

    /**
     Returns a string representation of the InputVariable in the Java
     programming language

     @param inputVariable is the input variable
     @param engine is the engine in which the input variable is registered
     @return a string representation of the input variable in the Java
     programming language
     */
    public String toString(InputVariable inputVariable, Engine engine) {
        String name;
        if (isUsingVariableNames()) {
            name = Op.validName(inputVariable.getName());
        } else {
            name = "inputVariable";
            if (engine.numberOfInputVariables() > 1) {
                name += Integer.toString(engine.getInputVariables().indexOf(inputVariable) + 1);
            }
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "InputVariable %s = new InputVariable();\n", name));
        result.append(String.format(
                "%s.setEnabled(%s);\n", name, String.valueOf(inputVariable.isEnabled())));
        result.append(String.format(
                "%s.setName(\"%s\");\n", name, inputVariable.getName()));
        result.append(String.format(
                "%s.setRange(%s, %s);\n", name,
                toString(inputVariable.getMinimum()), toString(inputVariable.getMaximum())));
        result.append(String.format(
                "%s.setLockValueInRange(%s);\n", name, String.valueOf(inputVariable.isLockValueInRange())));
        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("%s.addTerm(%s);\n", name, toString(term)));
        }
        result.append(String.format("engine.addInputVariable(%s);\n", name));
        return result.toString();
    }

    /**
     Returns a string representation of the OutputVariable in the Java
     programming language

     @param outputVariable is the output variable
     @param engine is the engine in which the output variable is registered
     @return a string representation of the output variable in the Java
     programming language
     */
    public String toString(OutputVariable outputVariable, Engine engine) {
        String name;
        if (isUsingVariableNames()) {
            name = Op.validName(outputVariable.getName());
        } else {
            name = "outputVariable";
            if (engine.numberOfOutputVariables() > 1) {
                name += Integer.toString(engine.getOutputVariables().indexOf(outputVariable) + 1);
            }
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "OutputVariable %s = new OutputVariable();\n", name));
        result.append(String.format(
                "%s.setEnabled(%s);\n", name, String.valueOf(outputVariable.isEnabled())));
        result.append(String.format(
                "%s.setName(\"%s\");\n", name, outputVariable.getName()));
        result.append(String.format(
                "%s.setRange(%s, %s);\n", name,
                toString(outputVariable.getMinimum()), toString(outputVariable.getMaximum())));
        result.append(String.format(
                "%s.setLockValueInRange(%s);\n", name, String.valueOf(outputVariable.isLockValueInRange())));
        result.append(String.format(
                "%s.fuzzyOutput().setAggregation(%s);\n",
                name, toString(outputVariable.fuzzyOutput().getAggregation())));
        result.append(String.format(
                "%s.setDefuzzifier(%s);\n", name,
                toString(outputVariable.getDefuzzifier())));
        result.append(String.format(
                "%s.setDefaultValue(%s);\n", name,
                toString(outputVariable.getDefaultValue())));
        result.append(String.format(
                "%s.setLockPreviousValue(%s);\n", name,
                outputVariable.isLockPreviousValue()));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("%s.addTerm(%s);\n",
                    name, toString(term)));
        }
        result.append(String.format(
                "engine.addOutputVariable(%s);\n", name));
        return result.toString();
    }

    /**
     Returns a string representation of the RuleBlock in the Java programming
     language

     @param ruleBlock is the rule block
     @param engine is the engine in which the rule block is registered
     @return a string representation of the rule block in the Java programming
     language
     */
    public String toString(RuleBlock ruleBlock, Engine engine) {
        String name = "ruleBlock";
        if (engine.numberOfRuleBlocks() > 1) {
            name += Integer.toString(engine.getRuleBlocks().indexOf(ruleBlock) + 1);
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format("RuleBlock %s = new RuleBlock();\n", name));
        result.append(String.format(
                "%s.setEnabled(%s);\n", name, String.valueOf(ruleBlock.isEnabled())));
        result.append(String.format(
                "%s.setName(\"%s\");\n", name, ruleBlock.getName()));
        result.append(String.format(
                "%s.setConjunction(%s);\n", name, toString(ruleBlock.getConjunction())));
        result.append(String.format(
                "%s.setDisjunction(%s);\n", name, toString(ruleBlock.getDisjunction())));
        result.append(String.format(
                "%s.setImplication(%s);\n", name, toString(ruleBlock.getImplication())));
        result.append(String.format(
                "%s.setActivation(%s);\n", name, toString(ruleBlock.getActivation())));

        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format("%s.addRule(Rule.parse(\"%s\", engine));\n",
                    name, rule.getText()));
        }
        result.append(String.format(
                "engine.addRuleBlock(%s);\n", name));
        return result.toString();
    }

    /**
     Returns a string representation of the Term in the Java programming
     language

     @param term is the term
     @return a string representation of the term in the Java programming
     language
     */
    public String toString(Term term) {
        if (term == null) {
            return "null";
        }
        if (term instanceof Discrete) {
            Discrete t = (Discrete) term;
            String result = String.format("%s.create(\"%s\", %s)",
                    Discrete.class.getSimpleName(), term.getName(),
                    Op.join(Discrete.toList(t.getXY()), ", "));
            return result;
        }
        if (term instanceof Function) {
            Function t = (Function) term;
            String result = String.format("%s.create(\"%s\", \"%s\", engine)",
                    Function.class.getSimpleName(), term.getName(),
                    t.getFormula());
            return result;
        }
        if (term instanceof Linear) {
            Linear t = (Linear) term;
            String result = String.format("%s.create(\"%s\", engine, %s)",
                    Linear.class.getSimpleName(), term.getName(),
                    Op.join(t.getCoefficients(), ", "));
            return result;
        }

        String result = String.format("new %s(\"%s\", %s)",
                term.getClass().getSimpleName(), term.getName(),
                term.parameters().replaceAll(Pattern.quote(" "), ", "));
        return result;
    }

    /**
     Returns a string representation of the Defuzzifier in the Java programming
     language

     @param defuzzifier is the defuzzifier
     @return a string representation of the defuzzifier in the Java programming
     language
     */
    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "null";
        }
        if (defuzzifier instanceof IntegralDefuzzifier) {
            return String.format("new %s(%d)",
                    defuzzifier.getClass().getSimpleName(),
                    ((IntegralDefuzzifier) defuzzifier).getResolution());
        }
        if (defuzzifier instanceof WeightedDefuzzifier) {
            return String.format("new %s(\"%s\")",
                    defuzzifier.getClass().getSimpleName(),
                    ((WeightedDefuzzifier) defuzzifier).getType());
        }
        return String.format("new %s()", defuzzifier.getClass().getSimpleName());
    }

    /**
     Returns a string representation of the Activation method in the Java
     programming language

     @param activation is the activation method
     @return a string representation of the activation method in the Java
     programming language
     */
    public String toString(Activation activation) {
        if (activation == null) {
            return "fl:null";
        }
        String parameters = activation.parameters().trim();
        if (parameters.isEmpty()) {
            return "new " + activation.getClass().getSimpleName() + "()";
        }
        List<String> values = Op.split(parameters, " ");
        for (int i = 0; i < values.size(); ++i) {
            String parameter = values.get(i);
            values.set(i, Op.isNumeric(parameter) ? parameter : "\"" + parameter + "\"");
        }
        return String.format("new %s(%s)",
                activation.getClass().getSimpleName(),
                Op.join(values, ", "));
    }

    /**
     Returns a string representation of the Norm in the Java programming
     language

     @param norm is the norm
     @return a string representation of the norm in the Java programming
     language
     */
    public String toString(Norm norm) {
        if (norm == null) {
            return "null";
        }
        return String.format("new %s()", norm.getClass().getSimpleName());
    }

    /**
     Returns a string representation of the scalar value in the Java programming
     language

     @param value is the scalar value
     @return a string representation of the scalar value in the Java programming
     language
     */
    public String toString(double value) {
        if (Double.isNaN(value)) {
            return "Double.NaN";
        } else if (Double.isInfinite(value)) {
            return value > 0 ? "Double.POSITIVE_INFINITY"
                    : "Double.NEGATIVE_INFINITY";
        }
        return Op.str(value);
    }

    @Override
    public JavaExporter clone() throws CloneNotSupportedException {
        return (JavaExporter) super.clone();
    }

}
