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
package com.fuzzylite.factory;

/**
 *
 * @author jcrada
 */
public class FactoryManager {

    protected static FactoryManager instance;

    public synchronized static FactoryManager instance() {
        if (instance == null) {
            instance = new FactoryManager();
            instance.setDefuzzifier(new DefuzzifierFactory());
            instance.setHedge(new HedgeFactory());
            instance.setSNorm(new SNormFactory());
            instance.setTNorm(new TNormFactory());
            instance.setTerm(new TermFactory());
        }
        return instance;
    }

    protected DefuzzifierFactory defuzzifier;
    protected HedgeFactory hedge;
    protected SNormFactory sNorm;
    protected TNormFactory tNorm;
    protected TermFactory term;

    protected FactoryManager() {
    }

    public DefuzzifierFactory defuzzifier() {
        return defuzzifier;
    }

    public void setDefuzzifier(DefuzzifierFactory defuzzifier) {
        this.defuzzifier = defuzzifier;
    }

    public HedgeFactory hedge() {
        return hedge;
    }

    public void setHedge(HedgeFactory hedge) {
        this.hedge = hedge;
    }

    public SNormFactory snorm() {
        return sNorm;
    }

    public void setSNorm(SNormFactory sNorm) {
        this.sNorm = sNorm;
    }

    public TNormFactory tnorm() {
        return tNorm;
    }

    public void setTNorm(TNormFactory tNorm) {
        this.tNorm = tNorm;
    }

    public TermFactory term() {
        return term;
    }

    public void setTerm(TermFactory term) {
        this.term = term;
    }
}
