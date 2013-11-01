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

import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;

import java.util.ArrayList;
import java.util.List;

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

    public Engine(String name) {
        this.name = name;
        this.inputVariables = new ArrayList<>();
        this.outputVariables = new ArrayList<>();
        this.ruleBlocks = new ArrayList<>();
        this.hedges = new ArrayList<>();
    }

    public void process() {
        for (OutputVariable outputVariable : outputVariables) {
            outputVariable.getOutput().getTerms().clear();
        }
        for (RuleBlock ruleBlock : ruleBlocks) {
            ruleBlock.activateRules();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InputVariable> getInputVariables() {
        return inputVariables;
    }

    public void setInputVariables(List<InputVariable> inputVariables) {
        this.inputVariables = inputVariables;
    }

    public void addInputVariable(InputVariable inputVariable) {
        this.inputVariables.add(inputVariable);
    }

    public List<OutputVariable> getOutputVariables() {
        return outputVariables;
    }

    public void setOutputVariables(List<OutputVariable> outputVariables) {
        this.outputVariables = outputVariables;
    }

    public void addOutputVariable(OutputVariable outputVariable) {
        this.outputVariables.add(outputVariable);
    }

    public List<RuleBlock> getRuleBlocks() {
        return ruleBlocks;
    }

    public void setRuleBlocks(List<RuleBlock> ruleBlocks) {
        this.ruleBlocks = ruleBlocks;
    }

    public void addRuleBlock(RuleBlock ruleBlock) {
        this.ruleBlocks.add(ruleBlock);
    }

    public List<Hedge> getHedges() {
        return hedges;
    }

    public void setHedges(List<Hedge> hedges) {
        this.hedges = hedges;
    }

    public void addHedge(Hedge hedgeType) {
        this.hedges.add(hedgeType);
    }
}
