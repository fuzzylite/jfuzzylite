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
import static com.fuzzylite.Op.str;
import com.fuzzylite.defuzzifier.Bisector;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.LargestOfMaximum;
import com.fuzzylite.defuzzifier.MeanOfMaximum;
import com.fuzzylite.defuzzifier.SmallestOfMaximum;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.defuzzifier.WeightedSum;
import com.fuzzylite.norm.Norm;
import com.fuzzylite.norm.s.AlgebraicSum;
import com.fuzzylite.norm.s.BoundedSum;
import com.fuzzylite.norm.s.DrasticSum;
import com.fuzzylite.norm.s.EinsteinSum;
import com.fuzzylite.norm.s.HamacherSum;
import com.fuzzylite.norm.s.Maximum;
import com.fuzzylite.norm.s.NormalizedSum;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.norm.t.BoundedDifference;
import com.fuzzylite.norm.t.DrasticProduct;
import com.fuzzylite.norm.t.EinsteinProduct;
import com.fuzzylite.norm.t.HamacherProduct;
import com.fuzzylite.norm.t.Minimum;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

/**
 *
 * @author jcrada
 */
public class FclExporter extends Exporter {

    protected String indent;

    public FclExporter() {
        this("  ");
    }

    public FclExporter(String indent) {
        this.indent = indent;
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    @Override
    public String toString(Engine engine) {
        StringBuilder result = new StringBuilder();

        result.append(String.format(
                "FUNCTION_BLOCK %s\n", engine.getName()));

        result.append("\n");

        result.append("VAR_INPUT\n");
        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.append(String.format(indent + "%s: REAL;\n", inputVariable.getName()));
        }
        result.append("END_VAR\n");

        result.append("\n");

        result.append("VAR_OUTPUT\n");
        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.append(String.format(indent + "%s: REAL;\n", outputVariable.getName()));
        }
        result.append("END_VAR\n");

        result.append("\n");

        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.append(toString(inputVariable)).append("\n");
        }

        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.append(toString(outputVariable)).append("\n");
        }

        for (RuleBlock ruleBlock : engine.getRuleBlocks()) {
            result.append(toString(ruleBlock)).append("\n");
        }

        result.append("END_FUNCTION_BLOCK\n");

        return result.toString();
    }

    public String toString(InputVariable inputVariable) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("FUZZIFY %s\n", inputVariable.getName()));
        if (!inputVariable.isEnabled()) {
            result.append(String.format(indent + "ENABLED : %s;\n",
                    String.valueOf(inputVariable.isEnabled()).toUpperCase()));
        }
        result.append(String.format(indent + "RANGE := (%s .. %s);\n",
                Op.str(inputVariable.getMinimum()), Op.str(inputVariable.getMaximum())));

        for (Term term : inputVariable.getTerms()) {
            result.append(String.format(indent + "TERM %s := %s;\n",
                    term.getName(), toString(term)));
        }
        result.append("END_FUZZIFY\n");
        return result.toString();
    }

    public String toString(OutputVariable outputVariable) {
        StringBuilder result = new StringBuilder();

        result.append(String.format("DEFUZZIFY %s\n", outputVariable.getName()));
        if (!outputVariable.isEnabled()) {
            result.append(String.format(indent + "ENABLED : %s;\n",
                    String.valueOf(outputVariable.isEnabled()).toUpperCase()));
        }
        result.append(String.format(indent + "RANGE := (%s .. %s);\n",
                Op.str(outputVariable.getMinimum()), Op.str(outputVariable.getMaximum())));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format(indent + "TERM %s := %s;\n", term.getName(), toString(term)));
        }
        if (outputVariable.getDefuzzifier() != null) {
            result.append(String.format(indent + "METHOD : %s;\n",
                    toString(outputVariable.getDefuzzifier())));
        }
        if (outputVariable.fuzzyOutput().getAccumulation() != null) {
            result.append(String.format(indent + "ACCU : %s;\n",
                    toString(outputVariable.fuzzyOutput().getAccumulation())));
        }
        result.append(String.format(indent + "DEFAULT := %s",
                str(outputVariable.getDefaultValue())));
        if (outputVariable.isLockingValidOutput()) {
            result.append(" | NC");
        }
        result.append(";\n");
        if (outputVariable.isLockingOutputRange()) {
            result.append(indent).append("LOCK : RANGE;\n");
        }
        result.append("END_DEFUZZIFY\n");
        return result.toString();
    }

    public String toString(RuleBlock ruleBlock) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("RULEBLOCK %s\n", ruleBlock.getName()));
        if (!ruleBlock.isEnabled()) {
            result.append(String.format(indent + "ENABLED : %s;\n",
                    String.valueOf(ruleBlock.isEnabled()).toUpperCase()));
        }
        if (ruleBlock.getConjunction() != null) {
            result.append(String.format(indent + "AND : %s;\n", toString(ruleBlock.getConjunction())));
        }
        if (ruleBlock.getDisjunction() != null) {
            result.append(String.format(indent + "OR : %s;\n", toString(ruleBlock.getDisjunction())));
        }
        if (ruleBlock.getActivation() != null) {
            result.append(String.format(indent + "ACT : %s;\n", toString(ruleBlock.getActivation())));
        }

        int index = 1;
        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format(indent + "RULE %d : %s\n", index++, rule.getText()));
        }
        result.append("END_RULEBLOCK\n");
        return result.toString();
    }

    public String toString(Term term) {
        if (term == null) {
            return "";
        }

        if (term instanceof Discrete) {
            StringBuilder result = new StringBuilder();
            Discrete discrete = (Discrete) term;
            for (int i = 0; i < discrete.x.size(); ++i) {
                result.append(String.format("(%s, %s)",
                        Op.str(discrete.x.get(i)), Op.str(discrete.y.get(i))));
                if (i + 1 < discrete.x.size()) {
                    result.append(" ");
                }
            }
            return result.toString();
        }

        if (term instanceof Constant) {
            Constant constant = (Constant) term;
            return Op.str(constant.getValue());
        }

        return term.getClass().getSimpleName() + " " + term.parameters();
    }

    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "";
        }
        if (defuzzifier instanceof Centroid) {
            return "COG";
        }
        if (defuzzifier instanceof Bisector) {
            return "COA";
        }
        if (defuzzifier instanceof SmallestOfMaximum) {
            return "LM";
        }
        if (defuzzifier instanceof LargestOfMaximum) {
            return "RM";
        }
        if (defuzzifier instanceof MeanOfMaximum) {
            return "MM";
        }
        if (defuzzifier instanceof WeightedAverage) {
            return "COGS";
        }
        if (defuzzifier instanceof WeightedSum) {
            return "COGSS";
        }
        return defuzzifier.getClass().getSimpleName();
    }

    public String toString(Norm norm) {
        if (norm == null) {
            return "";
        }
        //T-Norms
        if (norm instanceof Minimum) {
            return "MIN";
        }
        if (norm instanceof AlgebraicProduct) {
            return "PROD";
        }
        if (norm instanceof BoundedDifference) {
            return "BDIF";
        }
        if (norm instanceof DrasticProduct) {
            return "DPROD";
        }
        if (norm instanceof EinsteinProduct) {
            return "EPROD";
        }
        if (norm instanceof HamacherProduct) {
            return "HPROD";
        }

        //S-Norms
        if (norm instanceof Maximum) {
            return "MAX";
        }
        if (norm instanceof AlgebraicSum) {
            return "ASUM";
        }
        if (norm instanceof NormalizedSum) {
            return "NSUM";
        }
        if (norm instanceof BoundedSum) {
            return "BSUM";
        }
        if (norm instanceof DrasticSum) {
            return "DSUM";
        }
        if (norm instanceof EinsteinSum) {
            return "ESUM";
        }
        if (norm instanceof HamacherSum) {
            return "HSUM";
        }
        return norm.getClass().getSimpleName();
    }

}
