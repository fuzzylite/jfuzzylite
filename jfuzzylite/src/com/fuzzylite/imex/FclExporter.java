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
import com.fuzzylite.term.Bell;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Gaussian;
import com.fuzzylite.term.GaussianProduct;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.PiShape;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.Rectangle;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.SigmoidDifference;
import com.fuzzylite.term.SigmoidProduct;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.Trapezoid;
import com.fuzzylite.term.Triangle;
import com.fuzzylite.term.ZShape;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class FclExporter extends Exporter {

    @Override
    public String toString(Engine engine) {
        StringBuilder result = new StringBuilder();

        result.append(String.format(
                "FUNCTION_BLOCK %s\n", engine.getName()));

        result.append("\n");

        result.append("VAR_INPUT\n");
        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.append(String.format("  %s: REAL;\n", inputVariable.getName()));
        }
        result.append("END_VAR\n");

        result.append("\n");

        result.append("VAR_OUTPUT\n");
        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.append(String.format("  %s: REAL;\n", outputVariable.getName()));
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

    protected String toString(InputVariable inputVariable) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("FUZZIFY %s\n", inputVariable.getName()));
        result.append(String.format("  RANGE := (%s .. %s);\n",
                Op.str(inputVariable.getMinimum()), Op.str(inputVariable.getMaximum())));

        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("  TERM %s := %s;\n",
                    term.getName(), toString(term)));
        }
        result.append("END_FUZZIFY\n");
        return result.toString();
    }

    protected String toString(OutputVariable outputVariable) {
        StringBuilder result = new StringBuilder();

        result.append(String.format("DEFUZZIFY %s\n", outputVariable.getName()));
        result.append(String.format("  RANGE := (%s .. %s);\n",
                Op.str(outputVariable.getMinimum()), Op.str(outputVariable.getMaximum())));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("  TERM %s := %s;\n", term.getName(), toString(term)));
        }

        if (outputVariable.isLockOutputRange() || outputVariable.isLockValidOutput()) {
            String lock = "";
            if (outputVariable.isLockOutputRange()) {
                lock += "RANGE";
            }
            if (outputVariable.isLockValidOutput()) {
                if (!lock.isEmpty()) {
                    lock += " | ";
                }
                lock += "VALID";
            }
            result.append(String.format("  LOCK : %s;\n", lock));
        }
        if (outputVariable.getDefuzzifier() != null) {
            result.append(String.format("  METHOD : %s;\n",
                    toString(outputVariable.getDefuzzifier())));
        }
        if (outputVariable.output().getAccumulation() != null) {
            result.append(String.format("  ACCU : %s;\n",
                    toString(outputVariable.output().getAccumulation())));
        }
        result.append(String.format("  DEFAULT := %s;\n",
                str(outputVariable.getDefaultValue())));

        result.append("END_DEFUZZIFY\n");
        return result.toString();
    }

    protected String toString(RuleBlock ruleBlock) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("RULEBLOCK %s\n", ruleBlock.getName()));
        if (ruleBlock.getConjunction() != null) {
            result.append(String.format("  AND : %s;\n", toString(ruleBlock.getConjunction())));
        }
        if (ruleBlock.getDisjunction() != null) {
            result.append(String.format("  OR : %s;\n", toString(ruleBlock.getDisjunction())));
        }
        if (ruleBlock.getActivation() != null) {
            result.append(String.format("  ACT : %s;\n", toString(ruleBlock.getActivation())));
        }

        int index = 1;
        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format("  RULE %d : %s\n", index++, rule.getText()));
        }
        result.append("END_RULEBLOCK\n");
        return result.toString();
    }

    protected String toString(Term term) {
        if (term == null) {
            return "null";
        }
        if (term instanceof Bell) {
            Bell t = (Bell) term;
            return String.format("%s (%s)",
                    Bell.class.getSimpleName(),
                    Op.join(", ", t.getCenter(), t.getWidth(), t.getSlope()));
        }
        if (term instanceof Constant) {
            Constant t = (Constant) term;
            return String.format("%s", str(t.getValue()));
        }
        if (term instanceof Discrete) {
            Discrete t = (Discrete) term;
            List<Double> xy = new ArrayList<>();
            for (int i = 0; i < t.x.size(); ++i) {
                xy.add(t.x.get(i));
                xy.add(t.y.get(i));
            }
            return String.format("(%s)", Op.join(xy, ", "));
        }

        if (term instanceof Function) {
            Function t = (Function) term;
            return String.format("%s (%s)",
                    Function.class.getSimpleName(), t.getText());
        }
        if (term instanceof Gaussian) {
            Gaussian t = (Gaussian) term;
            return String.format("%s (%s)",
                    Gaussian.class.getSimpleName(),
                    Op.join(", ", t.getMean(), t.getStandardDeviation()));
        }
        if (term instanceof GaussianProduct) {
            GaussianProduct t = (GaussianProduct) term;
            return String.format("%s (%s)",
                    GaussianProduct.class.getSimpleName(),
                    Op.join(", ", t.getMeanA(), t.getStandardDeviationA(),
                            t.getMeanB(), t.getStandardDeviationB()));
        }
        if (term instanceof Linear) {
            Linear t = (Linear) term;
            return String.format("%s (%s)",
                    Linear.class.getSimpleName(),
                    Op.join(t.getCoefficients(), ", "));
        }
        if (term instanceof PiShape) {
            PiShape t = (PiShape) term;
            return String.format("%s (%s)",
                    PiShape.class.getSimpleName(),
                    Op.join(", ", t.getBottomLeft(), t.getTopLeft(),
                            t.getTopRight(), t.getBottomRight()));
        }
        if (term instanceof Ramp) {
            Ramp t = (Ramp) term;
            return String.format("%s (%s)",
                    Ramp.class.getSimpleName(),
                    Op.join(", ", t.getStart(), t.getEnd()));
        }
        if (term instanceof Rectangle) {
            Rectangle t = (Rectangle) term;
            return String.format("%s (%s)",
                    Rectangle.class.getSimpleName(),
                    Op.join(", ", t.getMinimum(), t.getMaximum()));
        }
        if (term instanceof SigmoidDifference) {
            SigmoidDifference t = (SigmoidDifference) term;
            return String.format("%s (%s)",
                    SigmoidDifference.class.getSimpleName(),
                    Op.join(", ", t.getLeft(), t.getRising(),
                            t.getFalling(), t.getRight()));
        }
        if (term instanceof Sigmoid) {
            Sigmoid t = (Sigmoid) term;
            return String.format("%s (%s)",
                    Sigmoid.class.getSimpleName(),
                    Op.join(", ", t.getInflection(), t.getSlope()));
        }
        if (term instanceof SigmoidProduct) {
            SigmoidProduct t = (SigmoidProduct) term;
            return String.format("%s (%s)",
                    SigmoidProduct.class.getSimpleName(),
                    Op.join(", ", t.getLeft(), t.getRising(),
                            t.getFalling(), t.getRight()));
        }
        if (term instanceof SShape) {
            SShape t = (SShape) term;
            return String.format("%s (%s)",
                    SShape.class.getSimpleName(),
                    Op.join(", ", t.getStart(), t.getEnd()));
        }
        if (term instanceof Trapezoid) {
            Trapezoid t = (Trapezoid) term;
            return String.format("%s (%s)",
                    Trapezoid.class.getSimpleName(),
                    Op.join(", ", t.getA(), t.getB(), t.getC(), t.getD()));
        }
        if (term instanceof Triangle) {
            Triangle t = (Triangle) term;
            return String.format("%s (%s)",
                    Triangle.class.getSimpleName(),
                    Op.join(", ", t.getA(), t.getB(), t.getC()));
        }
        if (term instanceof ZShape) {
            ZShape t = (ZShape) term;
            return String.format("%s (%s)",
                    ZShape.class.getSimpleName(),
                    Op.join(", ", t.getStart(), t.getEnd()));
        }

        return term.toString();
    }

    protected String toString(Defuzzifier defuzzifier) {
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

    protected String toString(Norm norm) {
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
