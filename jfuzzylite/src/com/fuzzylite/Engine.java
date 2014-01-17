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
package com.fuzzylite;

import static com.fuzzylite.Op.str;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.factory.DefuzzifierFactory;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.factory.SNormFactory;
import com.fuzzylite.factory.TNormFactory;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.ZShape;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author jcrada
 */
public class Engine {

    protected String name;
    protected List<InputVariable> inputVariables;
    protected List<OutputVariable> outputVariables;
    protected List<RuleBlock> ruleBlocks;
    protected List<Hedge> hedges;

    public Engine() {
        this("");
    }

    public Engine(String name) {
        this.name = name;
        this.inputVariables = new ArrayList<>();
        this.outputVariables = new ArrayList<>();
        this.ruleBlocks = new ArrayList<>();
        this.hedges = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void restart() {
        for (InputVariable inputVariable : this.inputVariables) {
            inputVariable.setInputValue(Double.NaN);
        }
        for (OutputVariable outputVariable : this.outputVariables) {
            outputVariable.fuzzyOutput().clear();
            outputVariable.setLastValidOutput(Double.NaN);
        }
    }

    public void process() {
        for (OutputVariable outputVariable : outputVariables) {
            outputVariable.fuzzyOutput().clear();
        }
        /*
         * BEGIN: Debug information
         */
        if (FuzzyLite.debug()) {
            Logger logger = FuzzyLite.logger();
            for (InputVariable inputVariable : this.inputVariables) {
                double inputValue = inputVariable.getInputValue();
                if (inputVariable.isEnabled()) {
                    logger.fine(String.format(
                            "%s.input = %s\n%s.fuzzy = %s",
                            inputVariable.getName(), str(inputValue),
                            inputVariable.getName(), inputVariable.fuzzify(inputValue)));
                } else {
                    logger.fine(String.format(
                            "%s.enabled = false", inputVariable.getName()));
                }
            }
        }
        /*
         * END: Debug information
         */

        for (RuleBlock ruleBlock : ruleBlocks) {
            if (ruleBlock.isEnabled()) {
                ruleBlock.activate();
            }
        }

        /*
         * BEGIN: Debug information
         */
        if (FuzzyLite.debug()) {
            Logger logger = FuzzyLite.logger();
            for (OutputVariable outputVariable : this.outputVariables) {
                if (outputVariable.isEnabled()) {
                    logger.fine(String.format("%s.default = %s",
                            outputVariable.getName(), str(outputVariable.getDefaultValue())));
                    logger.fine(String.format("%s.lockRange = %s",
                            outputVariable.getName(), String.valueOf(outputVariable.isLockingOutputRange())));
                    logger.fine(String.format("%s.lockValid = %s",
                            outputVariable.getName(), String.valueOf(outputVariable.isLockingValidOutput())));

                    //no locking is ever performed during this debugging block;
                    double outputValue = outputVariable.defuzzifyNoLocks();
                    logger.fine(String.format("%s.output = %s",
                            outputVariable.getName(), str(outputValue)));
                    logger.fine(String.format("%s.fuzzy = %s",
                            outputVariable.getName(), outputVariable.fuzzify(outputValue)));
                    logger.fine(outputVariable.fuzzyOutput().toString());
                    logger.fine("==========================");
                } else {
                    logger.fine(String.format("%s.enabled = false", outputVariable.getName()));
                }
            }
        }
        /*
         * END: Debug information
         */
    }

    public void configure(String conjunction, String disjunction,
            String activation, String accumulation, String defuzzifier) {
        configure(conjunction, disjunction, activation, accumulation, defuzzifier,
                IntegralDefuzzifier.DEFAULT_RESOLUTION);
    }

    public void configure(String conjunction, String disjunction,
            String activation, String accumulation, String defuzzifier, int resolution) {
        TNormFactory tnormFactory = FactoryManager.instance().tnorm();
        SNormFactory snormFactory = FactoryManager.instance().snorm();

        TNorm objConjunction = tnormFactory.createInstance(conjunction);
        SNorm objDisjunction = snormFactory.createInstance(disjunction);
        TNorm objActivation = tnormFactory.createInstance(activation);
        SNorm objAccumulation = snormFactory.createInstance(accumulation);

        DefuzzifierFactory defuzzifierFactory
                = FactoryManager.instance().defuzzifier();
        Defuzzifier objDefuzzifier = defuzzifierFactory.createInstance(defuzzifier);
        if (objDefuzzifier instanceof IntegralDefuzzifier) {
            ((IntegralDefuzzifier) objDefuzzifier).setResolution(resolution);
        }
        configure(objConjunction, objDisjunction, objActivation, objAccumulation, objDefuzzifier);
    }

    public void configure(TNorm conjunction, SNorm disjunction,
            TNorm activation, SNorm accumulation,
            Defuzzifier defuzzifier) {
        for (RuleBlock ruleblock : this.ruleBlocks) {
            ruleblock.setConjunction(conjunction);
            ruleblock.setDisjunction(disjunction);
            ruleblock.setActivation(activation);
        }
        for (OutputVariable outputVariable : this.outputVariables) {
            outputVariable.setDefuzzifier(defuzzifier);
            outputVariable.fuzzyOutput().setAccumulation(accumulation);
        }
    }

    public boolean isReady() {
        return isReady(new StringBuilder());
    }

    public boolean isReady(StringBuilder message) {
        message.setLength(0);
        if (this.inputVariables.isEmpty()) {
            message.append("- Engine has no input variables\n");
        }
        for (int i = 0; i < this.inputVariables.size(); ++i) {
            InputVariable inputVariable = this.inputVariables.get(i);
            if (inputVariable == null) {
                message.append(String.format(
                        "- Engine has a null input variable at index <%d>\n", i));
            } else if (inputVariable.getTerms().isEmpty()) {
                //ignore because sometimes inputs can be empty: takagi-sugeno/matlab/slcpp1.fis
                //message.append(String.format("- Input variable <%s> has no terms\n", inputVariable.getName()));
            }
        }

        if (this.outputVariables.isEmpty()) {
            message.append("- Engine has no output variables\n");
        }
        for (int i = 0; i < this.outputVariables.size(); ++i) {
            OutputVariable outputVariable = this.outputVariables.get(i);
            if (outputVariable == null) {
                message.append(String.format(
                        "- Engine has a null output variable at index <%d>\n", i));
            } else {
                if (outputVariable.getTerms().isEmpty()) {
                    message.append(String.format(
                            "- Output variable <%s> has no terms\n", outputVariable.getName()));
                }
                Defuzzifier defuzzifier = outputVariable.getDefuzzifier();
                if (defuzzifier == null) {
                    message.append(String.format(
                            "- Output variable <%s> has no defuzzifier\n",
                            outputVariable.getName()));
                } else if (defuzzifier instanceof IntegralDefuzzifier
                        && outputVariable.fuzzyOutput().getAccumulation() == null) {
                    message.append(String.format(
                            "- Output variable <%s> has no Accumulation\n"));
                }
            }
        }

        if (this.ruleBlocks.isEmpty()) {
            message.append("- Engine has no rule blocks\n");
        }
        for (int i = 0; i < this.ruleBlocks.size(); ++i) {
            RuleBlock ruleBlock = this.ruleBlocks.get(i);
            if (ruleBlock == null) {
                message.append(String.format(
                        "- Engine has a null rule block at index <%d>\n", i));
            } else {
                if (ruleBlock.getRules().isEmpty()) {
                    message.append(String.format(
                            "- Rule block <%s> has no rules\n", ruleBlock.getName()));
                }
                int requiresConjunction = 0;
                int requiresDisjunction = 0;
                for (int r = 0; r < this.ruleBlocks.size(); ++r) {
                    Rule rule = ruleBlock.getRule(r);
                    if (rule == null) {
                        message.append(String.format(
                                "- Rule block <%s> has a null rule at index <%d>\n",
                                ruleBlock.getName(), r));
                    } else {
                        int thenIndex = rule.getText().indexOf(" " + Rule.FL_THEN + " ");
                        if (rule.getText().indexOf(" " + Rule.FL_AND + " ") < thenIndex) {
                            ++requiresConjunction;
                        }
                        if (rule.getText().indexOf(" " + Rule.FL_OR + " ") < thenIndex) {
                            ++requiresDisjunction;
                        }
                    }
                }
                if (requiresConjunction > 0 && ruleBlock.getConjunction() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no Conjunction\n", ruleBlock.getName()));
                    message.append(String.format(
                            "- Rule block <%s> has %d rules that require Conjunction", ruleBlock.getName(), requiresConjunction));
                }
                if (requiresDisjunction > 0 && ruleBlock.getDisjunction() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no Disjunction\n", ruleBlock.getName()));
                    message.append(String.format(
                            "- Rule block <%s> has %d rules that require Disjunction", ruleBlock.getName(), requiresDisjunction));
                }
                if (ruleBlock.getActivation() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no Activation\n", ruleBlock.getName()));
                }
            }
        }
        return message.length() == 0;
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    public enum Type {

        NONE, MAMDANI, LARSEN, TAKAGI_SUGENO, TSUKAMOTO, INVERSE_TSUKAMOTO, UNKNOWN;
    };

    public Type type() {
        if (outputVariables.isEmpty()) {
            return Type.NONE;
        }
        //mamdani
        boolean mamdani = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Terms cannot be Constant or Linear
            for (Term term : outputVariable.getTerms()) {
                mamdani &= term != null
                        && !(term instanceof Constant || term instanceof Linear);
            }
            //Defuzzifier must be integral
            mamdani &= outputVariable.getDefuzzifier() instanceof IntegralDefuzzifier;
        }

        boolean larsen = mamdani;
        //Larsen is Mamdani with AlgebraicProduct as Activation
        if (mamdani) {
            for (RuleBlock ruleBlock : ruleBlocks) {
                larsen &= ruleBlock.getActivation() instanceof AlgebraicProduct;
            }
        }
        if (larsen) {
            return Type.LARSEN;
        }
        if (mamdani) {
            return Type.MAMDANI;
        }
        //else keep checking

        boolean takagiSugeno = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Takagi-Sugeno has only Constant, Linear or Function terms
            for (Term term : outputVariable.getTerms()) {
                takagiSugeno &= term instanceof Constant
                        || term instanceof Linear
                        || term instanceof Function;
            }
            //and the defuzzifier cannot be integral
            Defuzzifier defuzzifier = outputVariable.getDefuzzifier();
            takagiSugeno &= defuzzifier != null && !(defuzzifier instanceof IntegralDefuzzifier);
        }
        if (takagiSugeno) {
            return Type.TAKAGI_SUGENO;
        }

        boolean tsukamoto = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Tsukamoto has only monotonic terms: Ramp, Sigmoid, SShape, or ZShape
            for (Term term : outputVariable.getTerms()) {
                tsukamoto &= term instanceof Ramp
                        || term instanceof Sigmoid
                        || term instanceof SShape
                        || term instanceof ZShape;
            }
            //and the defuzzifier cannot be integral
            Defuzzifier defuzzifier = outputVariable.getDefuzzifier();
            tsukamoto &= defuzzifier != null && !(defuzzifier instanceof IntegralDefuzzifier);
        }
        if (tsukamoto) {
            return Type.TSUKAMOTO;
        }

        boolean inverseTsukamoto = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Terms cannot be Constant or Linear, like Mamdani
            for (Term term : outputVariable.getTerms()) {
                inverseTsukamoto &= term != null
                        && !(term instanceof Constant || term instanceof Linear);
            }
            //Defuzzifier cannot be integral
            Defuzzifier defuzzifier = outputVariable.getDefuzzifier();
            inverseTsukamoto &= defuzzifier != null && !(defuzzifier instanceof IntegralDefuzzifier);
        }
        if (inverseTsukamoto) {
            return Type.INVERSE_TSUKAMOTO;
        }

        return Type.UNKNOWN;
    }


    /*
     * InputVariables
     */
    public void setInputValue(String name, double value) {
        InputVariable inputVariable = getInputVariable(name);
        inputVariable.setInputValue(value);
    }

    public InputVariable getInputVariable(String name) {
        for (InputVariable inputVariable : this.inputVariables) {
            if (name.equals(inputVariable.getName())) {
                return inputVariable;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no input variable by name <%s>", name));
    }

    public InputVariable getInputVariable(int index) {
        return this.inputVariables.get(index);
    }

    public void addInputVariable(InputVariable inputVariable) {
        this.inputVariables.add(inputVariable);
    }

    public InputVariable removeInputVariable(InputVariable inputVariable) {
        return this.inputVariables.remove(inputVariable) ? inputVariable : null;
    }

    public InputVariable removeInputVariable(String name) {
        for (Iterator<InputVariable> it = this.inputVariables.iterator(); it.hasNext();) {
            InputVariable inputVariable = it.next();
            if (name.equals(inputVariable.getName())) {
                it.remove();
                return inputVariable;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no input variable by name <%s>", name));
    }

    public InputVariable removeInputVariable(int index) {
        return this.inputVariables.remove(index);

    }

    public boolean hasInputVariable(String name) {
        for (InputVariable inputVariable : this.inputVariables) {
            if (name.equals(inputVariable.getName())) {
                return true;
            }
        }
        return false;
    }

    public int numberOfInputVariables() {
        return this.inputVariables.size();
    }

    public List<InputVariable> getInputVariables() {
        return this.inputVariables;
    }

    public void setInputVariables(List<InputVariable> inputVariables) {
        this.inputVariables = inputVariables;
    }

    /*
     * OutputVariables
     */
    public double getOutputValue(String name) {
        OutputVariable outputVariable = getOutputVariable(name);
        return outputVariable.defuzzify();
    }

    public OutputVariable getOutputVariable(String name) {
        for (OutputVariable outputVariable : this.outputVariables) {
            if (name.equals(outputVariable.getName())) {
                return outputVariable;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no output variable by name <%s>", name));
    }

    public OutputVariable getOutputVariable(int index) {
        return this.outputVariables.get(index);
    }

    public void addOutputVariable(OutputVariable outputVariable) {
        this.outputVariables.add(outputVariable);
    }

    public OutputVariable removeOutputVariable(OutputVariable outputVariable) {
        return this.outputVariables.remove(outputVariable) ? outputVariable : null;
    }

    public OutputVariable removeOutputVariable(String name) {
        for (Iterator<OutputVariable> it = this.outputVariables.iterator(); it.hasNext();) {
            OutputVariable outputVariable = it.next();
            if (name.equals(outputVariable.getName())) {
                it.remove();
                return outputVariable;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no output variable by name <%s>", name));
    }

    public OutputVariable removeOutputVariable(int index) {
        return this.outputVariables.remove(index);
    }

    public boolean hasOutputVariable(String name) {
        for (OutputVariable outputVariable : this.outputVariables) {
            if (name.equals(outputVariable.getName())) {
                return true;
            }
        }
        return false;
    }

    public int numberOfOutputVariables() {
        return this.outputVariables.size();
    }

    public List<OutputVariable> getOutputVariables() {
        return this.outputVariables;
    }

    public void setOutputVariables(List<OutputVariable> outputVariables) {
        this.outputVariables = outputVariables;
    }

    /*
     * RuleBlocks
     */
    public RuleBlock getRuleBlock(String name) {
        for (RuleBlock ruleBlock : this.ruleBlocks) {
            if (name.equals(ruleBlock.getName())) {
                return ruleBlock;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no rule block by name <%s>", name));
    }

    public RuleBlock getRuleBlock(int index) {
        return this.ruleBlocks.get(index);
    }

    public void addRuleBlock(RuleBlock ruleBlock) {
        this.ruleBlocks.add(ruleBlock);
    }

    public RuleBlock removeRuleBlock(RuleBlock ruleBlock) {
        return this.ruleBlocks.remove(ruleBlock) ? ruleBlock : null;
    }

    public RuleBlock removeRuleBlock(String name) {
        for (Iterator<RuleBlock> it = this.ruleBlocks.iterator(); it.hasNext();) {
            RuleBlock ruleBlock = it.next();
            if (name.equals(ruleBlock.getName())) {
                it.remove();
                return ruleBlock;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no rule block by name <%s>", name));
    }

    public RuleBlock removeRuleBlock(int index) {
        return this.ruleBlocks.remove(index);
    }

    public boolean hasRuleBlock(String name) {
        for (RuleBlock ruleBlock : this.ruleBlocks) {
            if (name.equals(ruleBlock.getName())) {
                return true;
            }
        }
        return false;
    }

    public int numberOfRuleBlocks() {
        return this.ruleBlocks.size();
    }

    public List<RuleBlock> getRuleBlocks() {
        return this.ruleBlocks;
    }

    public void setRuleBlocks(List<RuleBlock> ruleBlocks) {
        this.ruleBlocks = ruleBlocks;
    }

    /*
     * Hedges
     */
    public Hedge getHedge(String name) {
        for (Hedge hedge : this.hedges) {
            if (name.equals(hedge.getName())) {
                return hedge;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no hedge by name <%s>", name));
    }

    public Hedge getHedge(int index) {
        return this.hedges.get(index);
    }

    public void addHedge(Hedge hedge) {
        this.hedges.add(hedge);
    }

    public Hedge removeHedge(Hedge hedge) {
        return this.hedges.remove(hedge) ? hedge : null;
    }

    public Hedge removeHedge(String name) {
        for (Iterator<Hedge> it = this.hedges.iterator(); it.hasNext();) {
            Hedge hedge = it.next();
            if (name.equals(hedge.getName())) {
                it.remove();
                return hedge;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no hedge by name <%s>", name));
    }

    public Hedge removeHedge(int index) {
        return this.hedges.remove(index);
    }

    public boolean hasHedge(String name) {
        for (Hedge hedge : this.hedges) {
            if (name.equals(hedge.getName())) {
                return true;
            }
        }
        return false;
    }

    public int numberOfHedges() {
        return this.hedges.size();
    }

    public List<Hedge> getHedges() {
        return this.hedges;
    }

    public void setHedges(List<Hedge> hedges) {
        this.hedges = hedges;
    }

}
