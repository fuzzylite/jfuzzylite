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
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.activation.Activation;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.hedge.Hedge;
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

public class CppExporter extends Exporter {

    private boolean usingNamespace;
    private boolean usingVariableNames;

    public CppExporter() {
        this(false, true);
    }

    public CppExporter(boolean prefixNamespace, boolean exportVariableNames) {
        this.usingNamespace = prefixNamespace;
        this.usingVariableNames = exportVariableNames;
    }

    protected String fl() {
        return fl("");
    }

    protected String fl(String clazz) {
        return this.usingNamespace ? "fl::" + clazz : clazz;
    }

    public void setUsingNamespace(boolean usingNamespace) {
        this.usingNamespace = usingNamespace;
    }

    public boolean isUsingNamespace() {
        return this.usingNamespace;
    }

    public void setUsingVariableNames(boolean usingVariableNames) {
        this.usingVariableNames = usingVariableNames;
    }

    public boolean isUsingVariableNames() {
        return usingVariableNames;
    }

    @Override
    public String toString(Engine engine) {
        StringBuilder result = new StringBuilder();
        if (!isUsingNamespace()) {
            result.append("using namespace fl;\n\n");
        }
        result.append(fl("Engine* ") + "engine = new " + fl("Engine;\n"));
        result.append(String.format(
                "engine->setName(\"%s\");\n", engine.getName()));

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

    public String toString(InputVariable inputVariable, Engine engine) {
        String name;
        if (isUsingVariableNames()) {
            name = Op.validName(inputVariable.getName());
        } else {
            name = "inputVariable";
            if (engine.numberOfInputVariables() > 1) {
                name += engine.getInputVariables().indexOf(inputVariable) + 1;
            }
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                fl("InputVariable* ") + "%s = new " + fl("InputVariable;\n"), name));
        result.append(String.format(
                "%s->setEnabled(%s);\n", name, String.valueOf(inputVariable.isEnabled())));
        result.append(String.format(
                "%s->setName(\"%s\");\n", name, inputVariable.getName()));
        result.append(String.format(
                "%s->setRange(%s, %s);\n", name,
                toString(inputVariable.getMinimum()), toString(inputVariable.getMaximum())));
        result.append(String.format(
                "%s->setLockValueInRange(%s);\n", name, String.valueOf(inputVariable.isLockValueInRange())));
        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("%s->addTerm(%s);\n",
                    name, toString(term)));
        }
        result.append(String.format(
                "engine->addInputVariable(%s);\n", name));
        return result.toString();
    }

    public String toString(OutputVariable outputVariable, Engine engine) {
        String name;
        if (isUsingVariableNames()) {
            name = Op.validName(outputVariable.getName());
        } else {
            name = "outputVariable";
            if (engine.numberOfOutputVariables() > 1) {
                name += engine.getOutputVariables().indexOf(outputVariable) + 1;
            }
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                fl("OutputVariable* ") + "%s = new " + fl("OutputVariable;\n"), name));
        result.append(String.format(
                "%s->setEnabled(%s);\n", name, String.valueOf(outputVariable.isEnabled())));
        result.append(String.format(
                "%s->setName(\"%s\");\n", name, outputVariable.getName()));
        result.append(String.format(
                "%s->setRange(%s, %s);\n", name,
                toString(outputVariable.getMinimum()), toString(outputVariable.getMaximum())));
        result.append(String.format(
                "%s->setLockValueInRange(%s);\n", name, String.valueOf(outputVariable.isLockValueInRange())));
        result.append(String.format(
                "%s->fuzzyOutput()->setAggregation(%s);\n",
                name, toString(outputVariable.fuzzyOutput().getAggregation())));
        result.append(String.format(
                "%s->setDefuzzifier(%s);\n", name,
                toString(outputVariable.getDefuzzifier())));
        result.append(String.format(
                "%s->setDefaultValue(%s);\n", name,
                toString(outputVariable.getDefaultValue())));
        result.append(String.format(
                "%s->setLockPreviousValue(%s);\n", name,
                String.valueOf(outputVariable.isLockPreviousValue())));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("%s->addTerm(%s);\n",
                    name, toString(term)));
        }
        result.append(String.format(
                "engine->addOutputVariable(%s);\n", name));
        return result.toString();
    }

    public String toString(RuleBlock ruleBlock, Engine engine) {
        String name = "ruleBlock";
        if (engine.numberOfRuleBlocks() > 1) {
            name += engine.getRuleBlocks().indexOf(ruleBlock) + 1;
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                fl() + "RuleBlock* %s = new " + fl() + "RuleBlock;\n", name));
        result.append(String.format(
                "%s->setEnabled(%s);\n", name, String.valueOf(ruleBlock.isEnabled())));
        result.append(String.format(
                "%s->setName(\"%s\");\n", name, ruleBlock.getName()));
        result.append(String.format(
                "%s->setConjunction(%s);\n", name, toString(ruleBlock.getConjunction())));
        result.append(String.format(
                "%s->setDisjunction(%s);\n", name, toString(ruleBlock.getDisjunction())));
        result.append(String.format(
                "%s->setImplication(%s);\n", name, toString(ruleBlock.getImplication())));
        result.append(String.format(
                "%s->setActivation(%s);\n", name, toString(ruleBlock.getActivation())));

        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format("%s->addRule("
                    + "fl::Rule::parse(\"%s\", engine));\n",
                    name, rule.getText()));
        }
        result.append(String.format(
                "engine->addRuleBlock(%s);\n", name));
        return result.toString();
    }

    public String toString(double value) {
        if (Double.isNaN(value)) {
            return "fl::nan";
        } else if (Double.isInfinite(value)) {
            return value > 0 ? "fl::inf" : "-fl::inf";
        }
        return str(value);
    }

    public String toString(Term term) {
        if (term == null) {
            return "fl::null";
        }
        if (term instanceof Discrete) {
            Discrete t = (Discrete) term;
            List<Double> xy = Discrete.toList(t.getXY());
            String result = String.format(fl("%s") + "::create(\"%s\", %d, %s)",
                    Discrete.class.getSimpleName(), term.getName(),
                    xy.size(), Op.join(xy, ", "));
            return result;
        }
        if (term instanceof Function) {
            Function t = (Function) term;
            String result = String.format(fl("%s") + "::create(\"%s\", \"%s\", engine)",
                    Function.class.getSimpleName(), term.getName(),
                    t.getFormula());
            return result;
        }
        if (term instanceof Linear) {
            Linear t = (Linear) term;
            String result = String.format(fl("%s") + "::create(\"%s\", engine, %s)",
                    Linear.class.getSimpleName(), term.getName(), Op.join(t.getCoefficients(), ", "));
            return result;
        }

        String result = String.format("new " + fl("%s") + "(\"%s\", %s)",
                term.getClass().getSimpleName(), term.getName(),
                term.parameters().replaceAll(Pattern.quote(" "), ", "));
        return result;
    }

    public String toString(Hedge hedge) {
        return "new " + fl(hedge.getClass().getSimpleName());
    }

    public String toString(Norm norm) {
        if (norm == null) {
            return "fl::null";
        }
        return "new " + fl(norm.getClass().getSimpleName());
    }

    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "fl::null";
        }
        if (defuzzifier instanceof IntegralDefuzzifier) {
            return String.format("new " + fl("%s(%d)"),
                    defuzzifier.getClass().getSimpleName(),
                    ((IntegralDefuzzifier) defuzzifier).getResolution());
        }
        if (defuzzifier instanceof WeightedDefuzzifier) {
            return String.format("new " + fl("%s(\"%s\")"),
                    defuzzifier.getClass().getSimpleName(),
                    ((WeightedDefuzzifier) defuzzifier).getType());
        }
        return "new " + fl(defuzzifier.getClass().getSimpleName());
    }

    public String toString(Activation activation) {
        if (activation == null) {
            return "fl::null";
        }
        String parameters = activation.parameters().trim();
        if (parameters.isEmpty()) {
            return "new " + fl(activation.getClass().getSimpleName());
        }
        List<String> values = Op.split(parameters, " ");
        for (int i = 0; i < values.size(); ++i) {
            String parameter = values.get(i);
            values.set(i, Op.isNumeric(parameter) ? parameter : "\"" + parameter + "\"");
        }
        return String.format("new %s(%s)", 
                fl(activation.getClass().getSimpleName()),
                Op.join(values, ", "));
    }

    @Override
    public CppExporter clone() throws CloneNotSupportedException {
        return (CppExporter) super.clone();
    }

}
