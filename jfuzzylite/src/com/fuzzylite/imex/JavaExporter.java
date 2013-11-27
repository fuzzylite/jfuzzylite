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
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.example.ts.SimpleDimmer;
import com.fuzzylite.norm.Norm;
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
public class JavaExporter extends Exporter {

    @Override
    public String toString(Engine engine) {
//        String header = "";
//        header += "//package name;\n\n";
//        header += "import com.fuzzylite.*;\n";
//        header += "import com.fuzzylite.defuzzifier.*;\n";
//        header += "import com.fuzzylite.hedge.*;\n";
//        header += "import com.fuzzylite.norm.s.*;\n";
//        header += "import com.fuzzylite.norm.t.*;\n";
//        header += "import com.fuzzylite.rule.*;\n";
//        header += "import com.fuzzylite.term.*;\n";
//        header += "import com.fuzzylite.variable.*;\n";      
        StringBuilder result = new StringBuilder();

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

    public String toString(InputVariable inputVariable, Engine engine) {
        int index = engine.getInputVariables().indexOf(inputVariable) + 1;
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "InputVariable inputVariable%d = new InputVariable();\n", index));
        result.append(String.format(
                "inputVariable%d.setName(\"%s\");\n", index, inputVariable.getName()));
        result.append(String.format(
                "inputVariable%d.setRange(%s, %s);\n", index,
                toString(inputVariable.getMinimum()), toString(inputVariable.getMaximum())));
        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("inputVariable%d.addTerm(%s);\n",
                    index, toString(term)));
        }
        result.append(String.format(
                "engine.addInputVariable(inputVariable%d);\n", index));
        return result.toString();
    }

    public String toString(OutputVariable outputVariable, Engine engine) {
        int index = engine.getOutputVariables().indexOf(outputVariable) + 1;
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "OutputVariable outputVariable%d = new OutputVariable();\n", index));
        result.append(String.format(
                "outputVariable%d.setName(\"%s\");\n", index, outputVariable.getName()));
        result.append(String.format(
                "outputVariable%d.setRange(%s, %s);\n", index,
                toString(outputVariable.getMinimum()), toString(outputVariable.getMaximum())));
        result.append(String.format(
                "outputVariable%d.setLockOutputRange(%s);\n", index,
                outputVariable.isLockOutputRange()));
        result.append(String.format(
                "outputVariable%d.setLockValidOutput(%s);\n", index,
                outputVariable.isLockValidOutput()));
        result.append(String.format(
                "outputVariable%d.setDefaultValue(%s);\n", index,
                toString(outputVariable.getDefaultValue())));
        result.append(String.format(
                "outputVariable%d.setDefuzzifier(%s);\n", index,
                toString(outputVariable.getDefuzzifier())));
        result.append(String.format(
                "outputVariable%d.getOutput().setAccumulation(%s);\n",
                index, toString(outputVariable.getOutput().getAccumulation())));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("outputVariable%d.addTerm(%s);\n",
                    index, toString(term)));
        }
        result.append(String.format(
                "engine.addOutputVariable(outputVariable%d);\n", index));
        return result.toString();
    }

    public String toString(RuleBlock ruleBlock, Engine engine) {
        int index = engine.getRuleBlocks().indexOf(ruleBlock) + 1;
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "RuleBlock ruleBlock%d = new RuleBlock();\n", index));
        result.append(String.format(
                "ruleBlock%d.setName(\"%s\");\n", index, ruleBlock.getName()));
        result.append(String.format(
                "ruleBlock%d.setConjunction(%s);\n", index, toString(ruleBlock.getConjunction())));
        result.append(String.format(
                "ruleBlock%d.setDisjunction(%s);\n", index, toString(ruleBlock.getDisjunction())));
        result.append(String.format(
                "ruleBlock%d.setActivation(%s);\n", index, toString(ruleBlock.getActivation())));
        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format(
                    "ruleBlock%d.addRule(Rule.parse(\"%s\", engine));\n",
                    index, rule.getText()));
        }
        result.append(String.format(
                "engine.addRuleBlock(ruleBlock%d);\n", index));
        return result.toString();
    }

    public String toString(Term term) {
        if (term == null) {
            return "null";
        }
        if (term instanceof Bell) {
            Bell t = (Bell) term;
            return String.format("new %s(\"%s\", %s)",
                    Bell.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getCenter(), t.getWidth(), t.getSlope()));
        }
        if (term instanceof Constant) {
            Constant t = (Constant) term;
            return String.format("new %s(\"%s\", %s)",
                    Constant.class.getSimpleName(), term.getName(),
                    str(t.getValue()));
        }
        if (term instanceof Discrete) {
            Discrete t = (Discrete) term;
            List<Double> xy = new ArrayList<>();
            for (int i = 0; i < t.x.size(); ++i) {
                xy.add(t.x.get(i));
                xy.add(t.y.get(i));
            }
            return String.format("%s.create(\"%s\", %s)",
                    Discrete.class.getSimpleName(), term.getName(),
                    Op.join(xy, ", "));
        }
        if (term instanceof Function) {
            Function t = (Function) term;
            return String.format("%s.create(\"%s\", \"%s\", engine, true)",
                    Function.class.getSimpleName(), term.getName(),
                    t.getText());
        }
        if (term instanceof Gaussian) {
            Gaussian t = (Gaussian) term;
            return String.format("new %s(\"%s\", %s)",
                    Gaussian.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getMean(), t.getStandardDeviation()));
        }
        if (term instanceof GaussianProduct) {
            GaussianProduct t = (GaussianProduct) term;
            return String.format("new %s(\"%s\", %s)",
                    GaussianProduct.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getMeanA(), t.getStandardDeviationA(),
                            t.getMeanB(), t.getStandardDeviationB()));
        }
        if (term instanceof Linear) {
            Linear t = (Linear) term;
            return String.format("%s.create(\"%s\", engine.getInputVariables(), %s)",
                    Linear.class.getSimpleName(), term.getName(),
                    Op.join(t.getCoefficients(), ", "));
        }
        if (term instanceof PiShape) {
            PiShape t = (PiShape) term;
            return String.format("new %s(\"%s\", %s)",
                    PiShape.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getBottomLeft(), t.getTopLeft(),
                            t.getTopRight(), t.getBottomRight()));
        }
        if (term instanceof Ramp) {
            Ramp t = (Ramp) term;
            return String.format("new %s(\"%s\", %s)",
                    Ramp.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getStart(), t.getEnd()));
        }
        if (term instanceof Rectangle) {
            Rectangle t = (Rectangle) term;
            return String.format("new %s(\"%s\", %s)",
                    Rectangle.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getMinimum(), t.getMaximum()));
        }
        if (term instanceof SigmoidDifference) {
            SigmoidDifference t = (SigmoidDifference) term;
            return String.format("new %s(\"%s\", %s)",
                    SigmoidDifference.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getLeft(), t.getRising(),
                            t.getFalling(), t.getRight()));
        }
        if (term instanceof Sigmoid) {
            Sigmoid t = (Sigmoid) term;
            return String.format("new %s(\"%s\", %s)",
                    Sigmoid.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getInflection(), t.getSlope()));
        }
        if (term instanceof SigmoidProduct) {
            SigmoidProduct t = (SigmoidProduct) term;
            return String.format("new %s(\"%s\", %s)",
                    SigmoidProduct.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getLeft(), t.getRising(),
                            t.getFalling(), t.getRight()));
        }
        if (term instanceof SShape) {
            SShape t = (SShape) term;
            return String.format("new %s(\"%s\", %s)",
                    SShape.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getStart(), t.getEnd()));
        }
        if (term instanceof Trapezoid) {
            Trapezoid t = (Trapezoid) term;
            return String.format("new %s(\"%s\", %s)",
                    Trapezoid.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getA(), t.getB(), t.getC(), t.getD()));
        }
        if (term instanceof Triangle) {
            Triangle t = (Triangle) term;
            return String.format("new %s(\"%s\", %s)",
                    Triangle.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getA(), t.getB(), t.getC()));
        }
        if (term instanceof ZShape) {
            ZShape t = (ZShape) term;
            return String.format("new %s(\"%s\", %s)",
                    ZShape.class.getSimpleName(), term.getName(),
                    Op.join(", ", t.getStart(), t.getEnd()));
        }

        return term.toString();
    }

    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "null";
        }
        if (defuzzifier instanceof IntegralDefuzzifier) {
            IntegralDefuzzifier integralDefuzzifier = (IntegralDefuzzifier) defuzzifier;
            return String.format("new %s(%d)",
                    integralDefuzzifier.getClass().getSimpleName(),
                    integralDefuzzifier.getResolution());
        }
        return String.format("new %s()",
                defuzzifier.getClass().getSimpleName());
    }

    public String toString(Norm norm) {
        if (norm == null) {
            return "null";
        }
        return String.format("new %s()", norm.getClass().getSimpleName());
    }

    public String toString(double value) {
        if (Double.isNaN(value)) {
            return "Double.NaN";
        } else if (Double.isInfinite(value)) {
            return value > 0 ? "Double.POSITIVE_INFINITY"
                    : "Double.NEGATIVE_INFINITY";
        }
        return str(value);
    }

    public static void main(String[] args) {
        System.out.println(new JavaExporter().toString(new SimpleDimmer()));
    }

}
