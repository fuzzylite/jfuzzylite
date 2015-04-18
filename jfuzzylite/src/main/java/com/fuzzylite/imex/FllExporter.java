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
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.norm.Norm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.fuzzylite.variable.Variable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        List<String> result = new LinkedList<String>();
        result.add(String.format("Engine: %s", engine.getName()));
        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.add(toString(inputVariable));
        }
        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.add(toString(outputVariable));
        }
        for (RuleBlock ruleBlock : engine.getRuleBlocks()) {
            result.add(toString(ruleBlock));
        }
        return Op.join(result, separator);
    }

    public String toStringVariables(Collection<? extends Variable> variables) {
        List<String> result = new LinkedList<String>();
        for (Variable variable : variables) {
            result.add(toString(variable));
        }
        return Op.join(result, separator);
    }

    public String toStringInputVariables(Collection<InputVariable> variables) {
        List<String> result = new LinkedList<String>();
        for (InputVariable variable : variables) {
            result.add(toString(variable));
        }
        return Op.join(result, separator);
    }

    public String toStringOutputVariables(Collection<OutputVariable> variables) {
        List<String> result = new LinkedList<String>();
        for (OutputVariable variable : variables) {
            result.add(toString(variable));
        }
        return Op.join(result, separator);
    }

    public String toStringRuleBlocks(Collection<RuleBlock> ruleBlocks) {
        List<String> result = new LinkedList<String>();
        for (RuleBlock ruleBlock : ruleBlocks) {
            result.add(toString(ruleBlock));
        }
        return Op.join(result, separator);
    }

    public String toString(Variable variable) {
        List<String> result = new LinkedList<String>();
        result.add(String.format("Variable: %s", variable.getName()));
        result.add(String.format("%senabled: %s", indent,
                String.valueOf(variable.isEnabled())));
        result.add(String.format("%srange: %s", indent,
                Op.join(" ", variable.getMinimum(), variable.getMaximum())));
        for (Term term : variable.getTerms()) {
            result.add(String.format("%s%s", indent, toString(term)));
        }
        return Op.join(result, separator);
    }

    public String toString(InputVariable inputVariable) {
        List<String> result = new LinkedList<String>();
        result.add(String.format("InputVariable: %s", inputVariable.getName()));
        result.add(String.format("%senabled: %s", indent,
                String.valueOf(inputVariable.isEnabled())));
        result.add(String.format("%srange: %s", indent,
                Op.join(" ", inputVariable.getMinimum(), inputVariable.getMaximum())));
        for (Term term : inputVariable.getTerms()) {
            result.add(String.format("%s%s", indent, toString(term)));
        }
        return Op.join(result, separator);
    }

    public String toString(OutputVariable outputVariable) {
        List<String> result = new LinkedList<String>();
        result.add(String.format("OutputVariable: %s", outputVariable.getName()));
        result.add(String.format("%senabled: %s", indent,
                String.valueOf(outputVariable.isEnabled())));
        result.add(String.format("%srange: %s", indent,
                Op.join(" ", outputVariable.getMinimum(), outputVariable.getMaximum())));
        result.add(String.format("%saccumulation: %s", indent,
                toString(outputVariable.fuzzyOutput().getAccumulation())));
        result.add(String.format("%sdefuzzifier: %s", indent,
                toString(outputVariable.getDefuzzifier())));
        result.add(String.format("%sdefault: %s", indent,
                Op.str(outputVariable.getDefaultValue())));
        result.add(String.format("%slock-previous: %s", indent,
                String.valueOf(outputVariable.isLockPreviousOutputValue())));
        result.add(String.format("%slock-range: %s", indent,
                String.valueOf(outputVariable.isLockOutputValueInRange())));
        for (Term term : outputVariable.getTerms()) {
            result.add(String.format("%s%s", indent, toString(term)));
        }
        return Op.join(result, separator);
    }

    public String toString(RuleBlock ruleBlock) {
        List<String> result = new LinkedList<String>();
        result.add(String.format("RuleBlock: %s", ruleBlock.getName()));
        result.add(String.format("%senabled: %s", indent,
                String.valueOf(ruleBlock.isEnabled())));
        result.add(String.format("%sconjunction: %s", indent,
                toString(ruleBlock.getConjunction())));
        result.add(String.format("%sdisjunction: %s", indent,
                toString(ruleBlock.getDisjunction())));
        result.add(String.format("%sactivation: %s", indent,
                toString(ruleBlock.getActivation())));
        for (Rule rule : ruleBlock.getRules()) {
            result.add(String.format("%s%s", indent, toString(rule)));
        }
        return Op.join(result, separator);
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
        String result = defuzzifier.getClass().getSimpleName();
        if (defuzzifier instanceof IntegralDefuzzifier) {
            return result + " " + ((IntegralDefuzzifier) defuzzifier).getResolution();
        } else if (defuzzifier instanceof WeightedDefuzzifier) {
            return result + " " + ((WeightedDefuzzifier) defuzzifier).getType().toString();
        }
        return result;
    }

    @Override
    public FllExporter clone() throws CloneNotSupportedException {
        return (FllExporter) super.clone();
    }

}
