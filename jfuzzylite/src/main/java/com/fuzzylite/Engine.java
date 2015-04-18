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
package com.fuzzylite;

import static com.fuzzylite.Op.str;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.factory.DefuzzifierFactory;
import com.fuzzylite.factory.FactoryManager;
import com.fuzzylite.factory.SNormFactory;
import com.fuzzylite.factory.TNormFactory;
import com.fuzzylite.imex.FllExporter;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
import com.fuzzylite.norm.t.AlgebraicProduct;
import com.fuzzylite.rule.Consequent;
import com.fuzzylite.rule.Proposition;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import com.fuzzylite.variable.Variable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Engine implements Op.Cloneable {

    private String name;
    private List<InputVariable> inputVariables;
    private List<OutputVariable> outputVariables;
    private List<RuleBlock> ruleBlocks;

    public enum Type {

        Mamdani, Larsen, TakagiSugeno, Tsukamoto, InverseTsukamoto,
        Hybrid, Unknown;
    };

    public Engine() {
        this("");
    }

    public Engine(String name) {
        this.name = name;
        this.inputVariables = new ArrayList<InputVariable>();
        this.outputVariables = new ArrayList<OutputVariable>();
        this.ruleBlocks = new ArrayList<RuleBlock>();
    }

    public void configure(String conjunction, String disjunction,
            String activation, String accumulation, String defuzzifier) {
        TNormFactory tnormFactory = FactoryManager.instance().tnorm();
        SNormFactory snormFactory = FactoryManager.instance().snorm();

        TNorm objConjunction = tnormFactory.constructObject(conjunction);
        SNorm objDisjunction = snormFactory.constructObject(disjunction);
        TNorm objActivation = tnormFactory.constructObject(activation);
        SNorm objAccumulation = snormFactory.constructObject(accumulation);

        DefuzzifierFactory defuzzifierFactory = FactoryManager.instance().defuzzifier();
        Defuzzifier objDefuzzifier = defuzzifierFactory.constructObject(defuzzifier);
        configure(objConjunction, objDisjunction, objActivation, objAccumulation, objDefuzzifier);
    }

    public void configure(TNorm conjunction, SNorm disjunction,
            TNorm activation, SNorm accumulation,
            Defuzzifier defuzzifier) {
        try {
            for (RuleBlock ruleblock : this.ruleBlocks) {
                ruleblock.setConjunction(conjunction == null ? null : conjunction.clone());
                ruleblock.setDisjunction(disjunction == null ? null : disjunction.clone());
                ruleblock.setActivation(activation == null ? null : activation.clone());
            }
            for (OutputVariable outputVariable : this.outputVariables) {
                outputVariable.setDefuzzifier(defuzzifier == null ? null : defuzzifier.clone());
                outputVariable.fuzzyOutput().setAccumulation(accumulation == null ? null : accumulation.clone());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
                            "- Output variable <%s> has no Accumulation\n",
                            outputVariable.getName()));
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
                int requiresActivation = 0;
                for (int r = 0; r < this.ruleBlocks.size(); ++r) {
                    Rule rule = ruleBlock.getRule(r);
                    if (rule == null) {
                        message.append(String.format(
                                "- Rule block <%s> has a null rule at index <%d>\n",
                                ruleBlock.getName(), r));
                    } else {
                        int thenIndex = rule.getText().indexOf(" " + Rule.FL_THEN + " ");
                        int andIndex = rule.getText().indexOf(" " + Rule.FL_AND + " ");
                        int orIndex = rule.getText().indexOf(" " + Rule.FL_OR + " ");
                        if (andIndex != -1 && andIndex < thenIndex) {
                            ++requiresConjunction;
                        }
                        if (orIndex != -1 && orIndex < thenIndex) {
                            ++requiresDisjunction;
                        }
                        if (rule.isLoaded()) {
                            Consequent consequent = rule.getConsequent();
                            for (Proposition proposition : consequent.getConclusions()) {
                                if (proposition.getVariable() instanceof OutputVariable) {
                                    OutputVariable outputVariable = (OutputVariable) proposition.getVariable();
                                    if (outputVariable.getDefuzzifier() instanceof IntegralDefuzzifier) {
                                        ++requiresActivation;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (requiresConjunction > 0 && ruleBlock.getConjunction() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no conjunction operator\n", ruleBlock.getName()));
                    message.append(String.format(
                            "- Rule block <%s> has %d rules that require conjunction operator", ruleBlock.getName(), requiresConjunction));
                }
                if (requiresDisjunction > 0 && ruleBlock.getDisjunction() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no disjunction operator\n", ruleBlock.getName()));
                    message.append(String.format(
                            "- Rule block <%s> has %d rules that require disjunction operator", ruleBlock.getName(), requiresDisjunction));
                }
                if (requiresActivation > 0 && ruleBlock.getActivation() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no activation operator\n", ruleBlock.getName()));
                    message.append(String.format(
                            "- Rule block <%s> has %d rules that require activation operator", ruleBlock.getName(), requiresActivation));
                }
            }
        }
        return message.length() == 0;
    }

    public void restart() {
        for (InputVariable inputVariable : this.inputVariables) {
            inputVariable.setInputValue(Double.NaN);
        }
        for (OutputVariable outputVariable : this.outputVariables) {
            outputVariable.clear();
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
            for (InputVariable inputVariable : this.inputVariables) {
                double inputValue = inputVariable.getInputValue();
                if (inputVariable.isEnabled()) {
                    FuzzyLite.logger().fine(String.format(
                            "%s.input = %s\n%s.fuzzy = %s",
                            inputVariable.getName(), str(inputValue),
                            inputVariable.getName(), inputVariable.fuzzify(inputValue)));
                } else {
                    FuzzyLite.logger().fine(String.format(
                            "%s.enabled = false", inputVariable.getName()));
                }
            }
        }
        /*
         * END: Debug information
         */

        for (RuleBlock ruleBlock : this.ruleBlocks) {
            if (ruleBlock.isEnabled()) {
                ruleBlock.activate();
            }
        }

        for (OutputVariable outputVariable : this.outputVariables) {
            outputVariable.defuzzify();
        }

        /*
         * BEGIN: Debug information
         */
        if (FuzzyLite.debug()) {
            for (OutputVariable outputVariable : this.outputVariables) {
                if (outputVariable.isEnabled()) {
                    FuzzyLite.logger().fine(String.format("%s.default = %s",
                            outputVariable.getName(), str(outputVariable.getDefaultValue())));
                    FuzzyLite.logger().fine(String.format("%s.lockValueInRange = %s",
                            outputVariable.getName(), String.valueOf(outputVariable.isLockOutputValueInRange())));
                    FuzzyLite.logger().fine(String.format("%s.lockPreviousValue= %s",
                            outputVariable.getName(), String.valueOf(outputVariable.isLockPreviousOutputValue())));

                    //no locking is ever performed during this debugging block;
                    double outputValue = outputVariable.getOutputValue();
                    FuzzyLite.logger().fine(String.format("%s.output = %s",
                            outputVariable.getName(), str(outputValue)));
                    FuzzyLite.logger().fine(String.format("%s.fuzzy = %s",
                            outputVariable.getName(), outputVariable.fuzzify(outputValue)));
                    FuzzyLite.logger().fine(outputVariable.fuzzyOutput().toString());
                    FuzzyLite.logger().fine("==========================");
                } else {
                    FuzzyLite.logger().fine(String.format("%s.enabled = false", outputVariable.getName()));
                }
            }
        }
        /*
         * END: Debug information
         */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    public Type type(StringBuilder reason) {
        reason.setLength(0);
        if (outputVariables.isEmpty()) {
            reason.append("- Engine has no output variables");
            return Type.Unknown;
        }
        //mamdani
        boolean mamdani = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Defuzzifier must be integral
            mamdani &= outputVariable.getDefuzzifier() instanceof IntegralDefuzzifier;
        }

        boolean larsen = mamdani && !ruleBlocks.isEmpty();
        //Larsen is Mamdani with AlgebraicProduct as Activation
        if (mamdani) {
            for (RuleBlock ruleBlock : ruleBlocks) {
                larsen &= ruleBlock.getActivation() instanceof AlgebraicProduct;
            }
        }
        if (larsen) {
            reason.append("- Output variables have integral defuzzifiers\n")
                    .append("- Rule blocks activate using the algebraic product T-Norm");
            return Type.Larsen;
        }
        if (mamdani) {
            reason.append("-Output variables have integral defuzzifiers");
            return Type.Mamdani;
        }
        //else keep checking

        boolean takagiSugeno = true;
        for (OutputVariable outputVariable : outputVariables) {
            WeightedDefuzzifier weightedDefuzzifier = null;
            if (outputVariable.getDefuzzifier() instanceof WeightedDefuzzifier) {
                weightedDefuzzifier = (WeightedDefuzzifier) outputVariable.getDefuzzifier();
                takagiSugeno &= weightedDefuzzifier.getType() == WeightedDefuzzifier.Type.Automatic
                        || weightedDefuzzifier.getType() == WeightedDefuzzifier.Type.TakagiSugeno;
            } else {
                takagiSugeno = false;
            }

            if (takagiSugeno) {
                //Takagi-Sugeno has only Constant, Linear or Function terms
                for (Iterator<Term> it = outputVariable.getTerms().iterator();
                        takagiSugeno && it.hasNext();) {
                    takagiSugeno &= weightedDefuzzifier.inferType(it.next())
                            == WeightedDefuzzifier.Type.TakagiSugeno;
                }
            }
        }
        if (takagiSugeno) {
            reason.append("- Output variables have weighted defuzzifiers\n")
                    .append("- Output variables have constant, linear or function terms");
            return Type.TakagiSugeno;
        }

        boolean tsukamoto = true;
        for (OutputVariable outputVariable : outputVariables) {
            WeightedDefuzzifier weightedDefuzzifier = null;
            if (outputVariable.getDefuzzifier() instanceof WeightedDefuzzifier) {
                weightedDefuzzifier = (WeightedDefuzzifier) outputVariable.getDefuzzifier();
                tsukamoto &= weightedDefuzzifier.getType() == WeightedDefuzzifier.Type.Automatic
                        || weightedDefuzzifier.getType() == WeightedDefuzzifier.Type.Tsukamoto;
            } else {
                tsukamoto = false;
            }

            if (tsukamoto) {
                //Tsukamoto has only monotonic terms: Concave, Ramp, Sigmoid, SShape, or ZShape
                for (Iterator<Term> it = outputVariable.getTerms().iterator();
                        tsukamoto && it.hasNext();) {
                    tsukamoto &= weightedDefuzzifier.isMonotonic(it.next());
                }
            }
        }
        if (tsukamoto) {
            reason.append("- Output variables have weighted defuzzifiers\n")
                    .append("- Output variables only have monotonic terms");
            return Type.Tsukamoto;
        }

        //Inverse Tsukamoto
        boolean inverseTsukamoto = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Defuzzifier cannot be integral
            Defuzzifier defuzzifier = outputVariable.getDefuzzifier();
            inverseTsukamoto &= defuzzifier != null && defuzzifier instanceof WeightedDefuzzifier;
        }
        if (inverseTsukamoto) {
            reason.append("- Output variables have weighted defuzzifiers\n")
                    .append("- Output variables do not only have constant, linear or function terms\n")
                    .append("- Output variables do not only have monotonic terms\n");
            return Type.InverseTsukamoto;
        }

        boolean hybrid = true;
        for (OutputVariable outputVariable : outputVariables) {
            //Output variables have non-null defuzzifiers
            hybrid &= outputVariable.getDefuzzifier() != null;
        }
        if (hybrid) {
            reason.append("- Output variables have different defuzzifiers");
            return Type.Hybrid;
        }

        reason.append("- There are output variables without a defuzzifier");
        return Type.Unknown;
    }

    public Type type() {
        return type(new StringBuilder());
    }

    @Override
    public Engine clone() throws CloneNotSupportedException {
        Engine result = (Engine) super.clone();
        result.inputVariables = new ArrayList<InputVariable>(this.inputVariables.size());
        for (InputVariable inputVariable : this.inputVariables) {
            result.inputVariables.add(inputVariable.clone());
        }
        result.outputVariables = new ArrayList<OutputVariable>(this.outputVariables.size());
        for (OutputVariable outputVariable : this.outputVariables) {
            result.outputVariables.add(outputVariable.clone());
        }
        for (Variable variable : this.variables()) {
            for (Term term : variable.getTerms()) {
                Term.updateReference(term, this);
            }
        }
        result.ruleBlocks = new ArrayList<RuleBlock>(this.ruleBlocks.size());
        for (RuleBlock ruleBlock : this.ruleBlocks) {
            RuleBlock ruleBlockClone = ruleBlock.clone();
            try {
                ruleBlockClone.loadRules(result);
            } finally {
                result.ruleBlocks.add(ruleBlockClone);
            }
        }

        return result;
    }

    public List<Variable> variables() {
        List<Variable> result = new ArrayList<Variable>(inputVariables.size() + outputVariables.size());
        result.addAll(inputVariables);
        result.addAll(outputVariables);
        return result;
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

    public boolean removeInputVariable(InputVariable inputVariable) {
        return this.inputVariables.remove(inputVariable);
    }

    public InputVariable removeInputVariable(String name) {
        for (Iterator<InputVariable> it = this.inputVariables.iterator(); it.hasNext();) {
            InputVariable inputVariable = it.next();
            if (inputVariable.getName().equals(name)) {
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
            if (inputVariable.getName().equals(name)) {
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
        return getOutputVariable(name).getOutputValue();
    }

    public OutputVariable getOutputVariable(String name) {
        for (OutputVariable outputVariable : this.outputVariables) {
            if (outputVariable.getName().equals(name)) {
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

    public boolean removeOutputVariable(OutputVariable outputVariable) {
        return this.outputVariables.remove(outputVariable);
    }

    public OutputVariable removeOutputVariable(String name) {
        for (Iterator<OutputVariable> it = this.outputVariables.iterator(); it.hasNext();) {
            OutputVariable outputVariable = it.next();
            if (outputVariable.getName().equals(name)) {
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
            if (outputVariable.getName().equals(name)) {
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
            if (ruleBlock.getName().equals(name)) {
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

    public boolean removeRuleBlock(RuleBlock ruleBlock) {
        return this.ruleBlocks.remove(ruleBlock);
    }

    public RuleBlock removeRuleBlock(String name) {
        for (Iterator<RuleBlock> it = this.ruleBlocks.iterator(); it.hasNext();) {
            RuleBlock ruleBlock = it.next();
            if (ruleBlock.getName().equals(name)) {
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
            if (ruleBlock.getName().equals(name)) {
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
}
