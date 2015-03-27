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
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.norm.Norm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.fuzzylite.variable.Variable;

public class FllExporter extends Exporter {

    private String indent;
    private String separator;

    public FllExporter() {
        this("  ", "\n");
    }

    public FllExporter(String indent, String separator) {
        this.indent = indent;
        this.separator = separator;
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public String toString(Engine engine) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("Engine: %s%s", engine.getName(), separator));
        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.append(toString(inputVariable));
        }
        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.append(toString(outputVariable));
        }
        for (RuleBlock ruleBlock : engine.getRuleBlocks()) {
            result.append(toString(ruleBlock));
        }
        return result.toString();
    }

    public String toString(Variable variable) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("Variable: %s%s", variable.getName(), separator));
        result.append(String.format("%senabled: %s%s", indent,
                String.valueOf(variable.isEnabled()), separator));
        result.append(String.format("%srange: %s%s", indent,
                Op.join(" ", variable.getMinimum(), variable.getMaximum()), separator));
        for (Term term : variable.getTerms()) {
            result.append(String.format("%s%s%s", indent, toString(term), separator));
        }
        return result.toString();
    }

    public String toString(InputVariable inputVariable) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("InputVariable: %s%s", inputVariable.getName(), separator));
        result.append(String.format("%senabled: %s%s", indent,
                String.valueOf(inputVariable.isEnabled()), separator));
        result.append(String.format("%srange: %s%s", indent,
                Op.join(" ", inputVariable.getMinimum(), inputVariable.getMaximum()), separator));
        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("%s%s%s", indent, toString(term), separator));
        }
        return result.toString();
    }

    public String toString(OutputVariable outputVariable) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("OutputVariable: %s%s", outputVariable.getName(), separator));
        result.append(String.format("%senabled: %s%s", indent,
                String.valueOf(outputVariable.isEnabled()), separator));
        result.append(String.format("%srange: %s%s", indent,
                Op.join(" ", outputVariable.getMinimum(), outputVariable.getMaximum()), separator));
        result.append(String.format("%saccumulation: %s%s", indent,
                toString(outputVariable.fuzzyOutput().getAccumulation()), separator));
        result.append(String.format("%sdefuzzifier: %s%s", indent,
                toString(outputVariable.getDefuzzifier()), separator));
        result.append(String.format("%sdefault: %s%s", indent,
                Op.str(outputVariable.getDefaultValue()), separator));
        result.append(String.format("%slock-previous: %s%s", indent,
                String.valueOf(outputVariable.isLockedPreviousOutputValue()), separator));
        result.append(String.format("%slock-range: %s%s", indent,
                String.valueOf(outputVariable.isLockedOutputValueInRange()), separator));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("%s%s%s", indent, toString(term), separator));
        }
        return result.toString();
    }

    public String toString(RuleBlock ruleBlock) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("RuleBlock: %s%s", ruleBlock.getName(), separator));
        result.append(String.format("%senabled: %s%s", indent,
                String.valueOf(ruleBlock.isEnabled()), separator));
        result.append(String.format("%sconjunction: %s%s", indent,
                toString(ruleBlock.getConjunction()), separator));
        result.append(String.format("%sdisjunction: %s%s", indent,
                toString(ruleBlock.getDisjunction()), separator));
        result.append(String.format("%sactivation: %s%s", indent,
                toString(ruleBlock.getActivation()), separator));
        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format("%s%s%s", indent, toString(rule), separator));
        }
        return result.toString();
    }

    public String toString(Rule rule) {
        return "rule: " + rule.getText();
    }

    public String toString(Term term) {
        return "term: " + term.getName()
                + " " + term.getClass().getSimpleName()
                + " " + term.parameters();
    }

    public String toString(Norm norm) {
        if (norm == null) {
            return "none";
        }
        return norm.getClass().getSimpleName();
    }

    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "none";
        }
        if (defuzzifier instanceof IntegralDefuzzifier) {
            return defuzzifier.getClass().getSimpleName() + " "
                    + ((IntegralDefuzzifier) defuzzifier).getResolution();
        }
        return defuzzifier.getClass().getSimpleName();
    }

}
