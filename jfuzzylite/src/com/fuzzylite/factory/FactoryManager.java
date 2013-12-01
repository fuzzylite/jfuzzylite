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

import com.fuzzylite.defuzzifier.Bisector;
import com.fuzzylite.defuzzifier.Centroid;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.LargestOfMaximum;
import com.fuzzylite.defuzzifier.MeanOfMaximum;
import com.fuzzylite.defuzzifier.SmallestOfMaximum;
import com.fuzzylite.defuzzifier.WeightedAverage;
import com.fuzzylite.defuzzifier.WeightedSum;
import com.fuzzylite.hedge.Any;
import com.fuzzylite.hedge.Extremely;
import com.fuzzylite.hedge.Hedge;
import com.fuzzylite.hedge.Not;
import com.fuzzylite.hedge.Seldom;
import com.fuzzylite.hedge.Somewhat;
import com.fuzzylite.hedge.Very;
import com.fuzzylite.norm.SNorm;
import com.fuzzylite.norm.TNorm;
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
import com.fuzzylite.term.Bell;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Gaussian;
import com.fuzzylite.term.GaussianProduct;
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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jcrada
 */
public class FactoryManager {
    //TODO: Create independent factories.
    
    protected static FactoryManager instance;
    
    public synchronized static FactoryManager instance() {
        if (instance == null) {
            instance = new FactoryManager();
            instance.loadTNorms();
            instance.loadSNorms();
            instance.loadHedges();
            instance.loadDefuzzifiers();
            instance.loadTerms();
        }
        return instance;
    }
    protected Map<Class, Factory> map;
    
    protected FactoryManager() {
        this.map = new HashMap<>();
    }
    
    protected void loadTNorms() {
        Factory<TNorm> tnorm = new Factory<>();
        tnorm.register(Minimum.class);
        tnorm.register(AlgebraicProduct.class);
        tnorm.register(BoundedDifference.class);
        tnorm.register(DrasticProduct.class);
        tnorm.register(EinsteinProduct.class);
        tnorm.register(HamacherProduct.class);
        register(TNorm.class, tnorm);
    }
    
    protected void loadSNorms() {
        Factory<SNorm> snorm = new Factory<>();
        snorm.register(Maximum.class);
        snorm.register(AlgebraicSum.class);
        snorm.register(BoundedSum.class);
        snorm.register(NormalizedSum.class);
        snorm.register(DrasticSum.class);
        snorm.register(EinsteinSum.class);
        snorm.register(HamacherSum.class);
        register(SNorm.class, snorm);
    }
    
    protected void loadDefuzzifiers() {
        Factory<Defuzzifier> defuzzifier = new Factory<>();
        defuzzifier.register(Centroid.class);
        defuzzifier.register(Bisector.class);
        defuzzifier.register(SmallestOfMaximum.class);
        defuzzifier.register(MeanOfMaximum.class);
        defuzzifier.register(LargestOfMaximum.class);
        defuzzifier.register(WeightedAverage.class);
        defuzzifier.register(WeightedSum.class);
        register(Defuzzifier.class, defuzzifier);
    }
    
    protected void loadHedges() {
        Factory<Hedge> hedge = new Factory<>();
        hedge.register(Any.class.getSimpleName().toLowerCase(), Any.class);
        hedge.register(Extremely.class.getSimpleName().toLowerCase(), Extremely.class);
        hedge.register(Not.class.getSimpleName().toLowerCase(), Not.class);
        hedge.register(Seldom.class.getSimpleName().toLowerCase(), Seldom.class);
        hedge.register(Somewhat.class.getSimpleName().toLowerCase(), Somewhat.class);
        hedge.register(Very.class.getSimpleName().toLowerCase(), Very.class);
        register(Hedge.class, hedge);
    }
    
    protected void loadTerms() {
        Factory<Term> term = new Factory<>();
        term.register(Bell.class);
        term.register(Discrete.class);
        term.register(Function.class);
        term.register(Gaussian.class);
        term.register(GaussianProduct.class);
        term.register(PiShape.class);
        term.register(Ramp.class);
        term.register(Rectangle.class);
        term.register(SShape.class);
        term.register(Sigmoid.class);
        term.register(SigmoidDifference.class);
        term.register(SigmoidProduct.class);
        term.register(Trapezoid.class);
        term.register(Triangle.class);
        term.register(ZShape.class);
        register(Term.class, term);
    }
    
    public <T> void register(Class<T> clazz, Factory<T> factory) {
        this.map.put(clazz, factory);
    }
    
    public <T> void deregister(Class<T> clazz) {
        this.map.remove(clazz);
    }
    
    public <T> boolean hasRegistered(Class<T> clazz) {
        return this.map.containsKey(clazz);
    }
    
    @SuppressWarnings("unchecked")
    public <T> Factory<T> getFactory(Class<T> clazz) {
        return map.get(clazz);
    }
    
    public Map<Class, Factory> getFactoryMap() {
        return this.map;
    }
    
    public static void main(String[] args) throws Exception {
        FactoryManager.instance().getFactory(TNorm.class).createInstance("Minimum");
    }
}
