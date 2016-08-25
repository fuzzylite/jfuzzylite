/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite;

import static com.fuzzylite.Op.str;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
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

/**
 The Engine class is the core class of the library as it groups the necessary
 components of a fuzzy logic controller.

 @author Juan Rada-Vilela, Ph.D.
 @see InputVariable
 @see OutputVariable
 @see RuleBlock
 @since 4.0
 */
public class Engine implements Op.Cloneable {

    private String name;
    private List<InputVariable> inputVariables;
    private List<OutputVariable> outputVariables;
    private List<RuleBlock> ruleBlocks;

    public enum Type {
        /**
         Mamdani: When the output variables have IntegralDefuzzifier%s
         */
        Mamdani,
        /**
         Larsen: When Mamdani and AlgebraicProduct is the implication operator
         of the rule blocks
         */
        Larsen,
        /**
         TakagiSugeno: When output variables have WeightedDefuzzifier%s of type
         TakagiSugeno and the output variables have Constant, Linear, or
         Function terms
         */
        TakagiSugeno,
        /**
         Tsukamoto: When output variables have WeightedDefuzzifier%s of type
         Tsukamoto and the output variables only have monotonic terms (Concave,
         Ramp, Sigmoid, SShape, and ZShape)
         */
        Tsukamoto,
        /**
         InverseTsukamoto: When output variables have WeightedDefuzzifier%s of
         type TakagiSugeno and the output variables do not only have Constant,
         Linear or Function terms
         */
        InverseTsukamoto,
        /**
         Hybrid: When output variables have different defuzzifiers
         */
        Hybrid,
        /**
         Unknown: When output variables have no defuzzifiers
         */
        Unknown;
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

    /**
     Configures the engine with the given operators

     @param conjunction is a TNorm registered in the TNormFactory
     @param disjunction is an SNorm registered in the SNormFactory
     @param implication is an TNorm registered in the TNormFactory
     @param aggregation is an SNorm registered in the SNormFactory
     @param defuzzifier is a defuzzifier registered in the DefuzzifierFactory
     */
    public void configure(String conjunction, String disjunction,
            String implication, String aggregation, String defuzzifier) {
        TNormFactory tnormFactory = FactoryManager.instance().tnorm();
        SNormFactory snormFactory = FactoryManager.instance().snorm();

        TNorm conjunctionObject = tnormFactory.constructObject(conjunction);
        SNorm disjunctionObject = snormFactory.constructObject(disjunction);
        TNorm implicationObject = tnormFactory.constructObject(implication);
        SNorm aggregationObject = snormFactory.constructObject(aggregation);
        Defuzzifier defuzzifierObject = FactoryManager.instance().defuzzifier().constructObject(defuzzifier);

        configure(conjunctionObject, disjunctionObject,
                implicationObject, aggregationObject, defuzzifierObject);
    }

    /**
     Configures the engine with clones of the given operators.

     @param conjunction is the operator to process the propositions joined by
     `and` in the antecedent of the rules
     @param disjunction is the operator to process the propositions joined by
     `or` in the antecedent of the rules
     @param implication is the operator to modify the consequents of the rules
     based on the activation degree of the antecedents of the rules
     @param aggregation is the operator to aggregate the resulting implications
     of the rules
     @param defuzzifier is the operator to transform the aggregated implications
     into a single scalar value
     */
    public void configure(TNorm conjunction, SNorm disjunction,
            TNorm implication, SNorm aggregation, Defuzzifier defuzzifier) {
        try {
            for (RuleBlock ruleblock : this.ruleBlocks) {
                ruleblock.setConjunction(conjunction == null ? null : conjunction.clone());
                ruleblock.setDisjunction(disjunction == null ? null : disjunction.clone());
                ruleblock.setImplication(implication == null ? null : implication.clone());
            }
            for (OutputVariable outputVariable : this.outputVariables) {
                outputVariable.setDefuzzifier(defuzzifier == null ? null : defuzzifier.clone());
                outputVariable.fuzzyOutput().setAggregation(aggregation == null ? null : aggregation.clone());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     Indicates whether the engine has been configured correctly and is ready for
     operation. In more advanced engines, the result of this method should be
     taken as a suggestion and not as a prerequisite to operate the engine.

     @return whether the engine is ready to operate
     */
    public boolean isReady() {
        return isReady(new StringBuilder());
    }

    /**
     Indicates whether the engine has been configured correctly and is ready for
     operation. In more advanced engines, the result of this method should be
     taken as a suggestion and not as a prerequisite to operate the engine.

     @param message (if not null) contains the configuration errors of the
     engine
     @return whether the engine is ready to operate
     */
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
            }
            /*else if (inputVariable.getTerms().isEmpty()) {
            ignore because sometimes inputs can be empty: takagi-sugeno/matlab/slcpp1.fis
            message.append(String.format("- Input variable <%s> has no terms\n", inputVariable.getName()));
            }*/
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
                        && outputVariable.fuzzyOutput().getAggregation() == null) {
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
                int requiresImplication = 0;
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
                                Variable variable = proposition.getVariable();
                                if (variable instanceof OutputVariable) {
                                    OutputVariable outputVariable = (OutputVariable) variable;
                                    if (outputVariable.getDefuzzifier() instanceof IntegralDefuzzifier) {
                                        ++requiresImplication;
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
                if (requiresImplication > 0 && ruleBlock.getImplication() == null) {
                    message.append(String.format(
                            "- Rule block <%s> has no implication operator\n", ruleBlock.getName()));
                    message.append(String.format("- Rule block <%s> has %d rules that require implication operator", ruleBlock.getName(), requiresImplication));
                }
            }
        }
        return message.length() == 0;
    }

    /**
     Restarts the engine by setting the values of the input variables to fl::nan
     and clearing the output variables

     @see Variable::setValue()
     @see OutputVariable::clear()
     */
    public void restart() {
        for (InputVariable inputVariable : this.inputVariables) {
            inputVariable.setValue(Double.NaN);
        }
        for (OutputVariable outputVariable : this.outputVariables) {
            outputVariable.clear();
        }
    }

    /**
     Processes the engine in its current state as follows: (a) Clears the
     aggregated fuzzy output variables, (b) Activates the rule blocks, and (c)
     Defuzzifies the output variables

     @see Aggregated::clear()
     @see RuleBlock::activate()
     @see OutputVariable::defuzzify()
     */
    public void process() {
        for (OutputVariable outputVariable : outputVariables) {
            outputVariable.fuzzyOutput().clear();
        }
        /*
         * BEGIN: Debug information
         */
        if (FuzzyLite.isDebugging()) {
            for (InputVariable inputVariable : this.inputVariables) {
                double inputValue = inputVariable.getValue();
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
        if (FuzzyLite.isDebugging()) {
            for (OutputVariable outputVariable : this.outputVariables) {
                if (outputVariable.isEnabled()) {
                    FuzzyLite.logger().fine(String.format("%s.default = %s",
                            outputVariable.getName(), str(outputVariable.getDefaultValue())));
                    FuzzyLite.logger().fine(String.format("%s.lockValueInRange = %s",
                            outputVariable.getName(), String.valueOf(outputVariable.isLockValueInRange())));
                    FuzzyLite.logger().fine(String.format("%s.lockPreviousValue= %s",
                            outputVariable.getName(), String.valueOf(outputVariable.isLockPreviousValue())));

                    //no locking is ever performed during this debugging block;
                    double outputValue = outputVariable.getValue();
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

    /**
     Gets the name of the engine

     @return the name of the engine
     */
    public String getName() {
        return name;
    }

    /**
     Sets the name of the engine

     @param name is the name of the engine
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Returns a string representation of the engine in the FuzzyLite Language

     @return a string representation of the engine in the FuzzyLite Language
     */
    @Override
    public String toString() {
        return new FllExporter().toString(this);
    }

    /**
     Infers the type of the engine based on its current configuration

     @param reason stores a string representation explaining the reasons for the
     inferred type (if the pointer passed is not `fl::null`)
     @return the inferred type of the engine based on its current configuration
     */
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
        //Larsen is Mamdani with AlgebraicProduct as Implication
        if (mamdani) {
            for (RuleBlock ruleBlock : ruleBlocks) {
                larsen &= ruleBlock.getImplication() instanceof AlgebraicProduct;
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

            if (takagiSugeno && weightedDefuzzifier != null) {
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

            if (tsukamoto && weightedDefuzzifier != null) {
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

    /**
     Infers the type of the engine based on its current configuration

     @return the inferred type of the engine based on its current configuration
     */
    public Type type() {
        return type(new StringBuilder());
    }

    /**
     Creates a clone of the engine

     @return a clone of the engine
     */
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
                term.updateReference(result);
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

    /**
     Returns a list that contains the input variables followed by the output
     variables in the order of insertion

     @return a list that contains the input variables followed by the output
     variables in the order of insertion
     */
    public List<Variable> variables() {
        List<Variable> result = new ArrayList<Variable>(inputVariables.size() + outputVariables.size());
        result.addAll(inputVariables);
        result.addAll(outputVariables);
        return result;
    }

    /**
     Sets the value of the given input variable. The cost of this method is
     O(n), where n is the number of input variables in the engine. For
     performance, please get the variables by index.

     @param name is the name of the input variable
     @param value is the value for the input variable
     */
    public void setInputValue(String name, double value) {
        InputVariable inputVariable = getInputVariable(name);
        inputVariable.setValue(value);
    }

    /**
     Gets the input variable of the given name after iterating the input
     variables. The cost of this method is O(n), where n is the number of input
     variables in the engine. For performance, please get the variables by
     index.

     @param name is the name of the input variable
     @return input variable of the given name
     @throws RuntimeException if there is no variable with the given name
     */
    public InputVariable getInputVariable(String name) {
        for (InputVariable inputVariable : this.inputVariables) {
            if (name.equals(inputVariable.getName())) {
                return inputVariable;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no input variable by name <%s>", name));
    }

    /**
     Gets the input variable at the given index

     @param index is the given index
     @return the input variable at the given index
     */
    public InputVariable getInputVariable(int index) {
        return this.inputVariables.get(index);
    }

    /**
     Adds the input variable

     @param inputVariable is the input variable
     */
    public void addInputVariable(InputVariable inputVariable) {
        this.inputVariables.add(inputVariable);
    }

    /**
     Removes the given input variable from the list of input variables.

     @param inputVariable is the input variable to remove
     @return whether the input variable was contained in the list of input
     variables
     */
    public boolean removeInputVariable(InputVariable inputVariable) {
        return this.inputVariables.remove(inputVariable);
    }

    /**
     Removes the given input variable from the list of input variables.

     @param name is the name of the input variable
     @return the input variable of the given name
     @throws RuntimeException if there is no variable with the given name
     */
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

    /**
     Removes the input variable at the given index.

     @param index is the given index
     @return the input variable at the given index
     */
    public InputVariable removeInputVariable(int index) {
        return this.inputVariables.remove(index);

    }

    /**
     Indicates whether an input variable of the given name is in the input
     variables

     @param name is the name of the input variable
     @return whether an input variable is registered with the given name
     */
    public boolean hasInputVariable(String name) {
        for (InputVariable inputVariable : this.inputVariables) {
            if (inputVariable.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     Returns the number of input variables added to the engine

     @return the number of input variables added to the engine
     */
    public int numberOfInputVariables() {
        return this.inputVariables.size();
    }

    /**
     Returns a mutable list of input variables

     @return a mutable list of input variables
     */
    public List<InputVariable> getInputVariables() {
        return this.inputVariables;
    }

    /**
     Sets the list of input variables

     @param inputVariables is the list of input variables
     */
    public void setInputVariables(List<InputVariable> inputVariables) {
        this.inputVariables = inputVariables;
    }

    /**
     Gets the value of the given output variable. The cost of this method is
     O(n), where n is the number of output variables in the engine. For
     performance, please get the variables by index.

     @param name is the name of the output variable
     @return the value of the given output variable
     */
    public double getOutputValue(String name) {
        return getOutputVariable(name).getValue();
    }

    /**
     Gets the output variable of the given name after iterating the output
     variables. The cost of this method is O(n), where n is the number of output
     variables in the engine. For performance, please get the variables by
     index.

     @param name is the name of the output variable
     @return output variable of the given name
     @throws RuntimeException if there is no variable with the given name
     */
    public OutputVariable getOutputVariable(String name) {
        for (OutputVariable outputVariable : this.outputVariables) {
            if (outputVariable.getName().equals(name)) {
                return outputVariable;
            }
        }
        throw new RuntimeException(String.format(
                "[engine error] no output variable by name <%s>", name));
    }

    /**
     Gets the output variable at the given index

     @param index is the given index
     @return the output variable at the given index
     */
    public OutputVariable getOutputVariable(int index) {
        return this.outputVariables.get(index);
    }

    /**
     Adds the output variable

     @param outputVariable is the output variable
     */
    public void addOutputVariable(OutputVariable outputVariable) {
        this.outputVariables.add(outputVariable);
    }

    /**
     Removes the given output variable

     @param outputVariable is the output variable
     @return whether the output variable was contained in the list of output
     variables
     */
    public boolean removeOutputVariable(OutputVariable outputVariable) {
        return this.outputVariables.remove(outputVariable);
    }

    /**
     Removes the output variable of the given name.

     @param name is the name of the output variable
     @return the output variable of the given name
     @throws RuntimeException if there is no variable with the given name
     */
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

    /**
     Removes the output variable at the given index

     @param index is the given index
     @return the output variable at the given index
     */
    public OutputVariable removeOutputVariable(int index) {
        return this.outputVariables.remove(index);
    }

    /**
     Indicates whether an output variable of the given name is in the output
     variables

     @param name is the name of the output variable
     @return whether an output variable is registered with the given name
     */
    public boolean hasOutputVariable(String name) {
        for (OutputVariable outputVariable : this.outputVariables) {
            if (outputVariable.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     Returns the number of output variables added to the engine

     @return the number of output variables added to the engine
     */
    public int numberOfOutputVariables() {
        return this.outputVariables.size();
    }

    /**
     Returns a mutable vector of output variables

     @return a mutable vector of output variables
     */
    public List<OutputVariable> getOutputVariables() {
        return this.outputVariables;
    }

    /**
     Sets the vector of output variables

     @param outputVariables is the vector of output variables
     */
    public void setOutputVariables(List<OutputVariable> outputVariables) {
        this.outputVariables = outputVariables;
    }

    /**
     Gets the rule block of the given name after iterating the rule blocks. The
     cost of this method is O(n), where n is the number of rule blocks in the
     engine. For performance, please get the rule blocks by index.

     @param name is the name of the rule block
     @return rule block of the given name
     @throws RuntimeException if there is no block with the given name
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

    /**
     Gets the rule block at the given index

     @param index is the given index
     @return the rule block at the given index
     */
    public RuleBlock getRuleBlock(int index) {
        return this.ruleBlocks.get(index);
    }

    /**
     Adds the rule block

     @param ruleBlock is the rule block
     */
    public void addRuleBlock(RuleBlock ruleBlock) {
        this.ruleBlocks.add(ruleBlock);
    }

    /**
     Removes the given rule block.

     @param ruleBlock is the rule block
     @return whether the rule block was contained in the list of rule blocks
     */
    public boolean removeRuleBlock(RuleBlock ruleBlock) {
        return this.ruleBlocks.remove(ruleBlock);
    }

    /**
     Removes the rule block of the given name.

     @param name is the name of the rule block
     @return the rule block of the given name
     @throws RuntimeException if there is no rule block with the given name
     */
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

    /**
     Removes the rule block at the given index.

     @param index is the given index
     @return the rule block at the given index
     */
    public RuleBlock removeRuleBlock(int index) {
        return this.ruleBlocks.remove(index);
    }

    /**
     Removes the rule block of the given name.

     @param name is the name of the rule block
     @return the rule block of the given name
     @throws RuntimeException if there is no block with the given name
     */
    public boolean hasRuleBlock(String name) {
        for (RuleBlock ruleBlock : this.ruleBlocks) {
            if (ruleBlock.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     Returns the number of rule blocks added to the engine

     @return the number of rule blocks added to the engine
     */
    public int numberOfRuleBlocks() {
        return this.ruleBlocks.size();
    }

    /**
     Returns a mutable list of rule blocks

     @return a mutable list of rule blocks
     */
    public List<RuleBlock> getRuleBlocks() {
        return this.ruleBlocks;
    }

    /**
     Sets the list of rule blocks

     @param ruleBlocks is the list of rule blocks
     */
    public void setRuleBlocks(List<RuleBlock> ruleBlocks) {
        this.ruleBlocks = ruleBlocks;
    }
}
