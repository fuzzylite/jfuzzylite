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
import com.fuzzylite.norm.Norm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author jcrada
 */
public class JavaExporter extends Exporter {

    @Override
    public String toString(Engine engine) {
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
        String name = "inputVariable";
        if (engine.numberOfInputVariables() > 1) {
            name += engine.getInputVariables().indexOf(inputVariable) + 1;
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
        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("%s.addTerm(%s);\n",
                    name, toString(term)));
        }
        result.append(String.format(
                "engine.addInputVariable(%s);\n", name));
        return result.toString();
    }

    public String toString(OutputVariable outputVariable, Engine engine) {
        String name = "outputVariable";
        if (engine.numberOfOutputVariables() > 1) {
            name += engine.getOutputVariables().indexOf(outputVariable) + 1;
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
                "%s.fuzzyOutput().setAccumulation(%s);\n",
                name, toString(outputVariable.fuzzyOutput().getAccumulation())));
        result.append(String.format(
                "%s.setDefuzzifier(%s);\n", name,
                toString(outputVariable.getDefuzzifier())));
        result.append(String.format(
                "%s.setDefaultValue(%s);\n", name,
                toString(outputVariable.getDefaultValue())));
        result.append(String.format(
                "%s.setLockValidOutput(%s);\n", name,
                outputVariable.isLockingValidOutput()));
        result.append(String.format(
                "%s.setLockOutputRange(%s);\n", name,
                outputVariable.isLockingOutputRange()));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("%s.addTerm(%s);\n",
                    name, toString(term)));
        }
        result.append(String.format(
                "engine.addOutputVariable(%s);\n", name));
        return result.toString();
    }

    public String toString(RuleBlock ruleBlock, Engine engine) {
        String name = "ruleBlock";
        if (engine.numberOfRuleBlocks() > 1) {
            name += engine.getRuleBlocks().indexOf(ruleBlock) + 1;
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
                "%s.setActivation(%s);\n", name, toString(ruleBlock.getActivation())));
        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format("%s.addRule(Rule.parse(\"%s\", engine));\n",
                    name, rule.getText()));
        }
        result.append(String.format(
                "engine.addRuleBlock(%s);\n", name));
        return result.toString();
    }

    public String toString(Term term) {
        if (term == null) {
            return "null";
        }
        if (term instanceof Discrete) {
            Discrete t = (Discrete) term;
            List<Double> xy = new ArrayList<>();
            for (int i = 0; i < t.x.size(); ++i) {
                xy.add(t.x.get(i));
                xy.add(t.y.get(i));
            }
            String result = String.format("%s.create(\"%s\", %s)",
                    Discrete.class.getSimpleName(), term.getName(),
                    Op.join(xy, ", "));
            return result;
        }
        if (term instanceof Function) {
            Function t = (Function) term;
            String result = String.format("%s.create(\"%s\", \"%s\", engine, true)",
                    Function.class.getSimpleName(), term.getName(),
                    t.getFormula());
            return result;
        }
        if (term instanceof Linear) {
            Linear t = (Linear) term;
            String result = String.format("%s.create(\"%s\", engine.getInputVariables(), %s)",
                    Linear.class.getSimpleName(), term.getName(),
                    Op.join(t.getCoefficients(), ", "));
            return result;
        }

        String result = String.format("new %s(\"%s\", %s)",
                term.getClass().getSimpleName(), term.getName(),
                term.parameters().replaceAll(Pattern.quote(" "), ", "));
        return result;
    }

    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "null";
        }
        if (defuzzifier instanceof IntegralDefuzzifier) {
            IntegralDefuzzifier integralDefuzzifier = (IntegralDefuzzifier) defuzzifier;
            String result = String.format("new %s(%d)",
                    integralDefuzzifier.getClass().getSimpleName(),
                    integralDefuzzifier.getResolution());
            return result;
        }
        String result = String.format("new %s()",
                defuzzifier.getClass().getSimpleName());
        return result;
    }

    public String toString(Norm norm) {
        if (norm == null) {
            return "null";
        }
        String result = String.format("new %s()", norm.getClass().getSimpleName());
        return result;
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

}
