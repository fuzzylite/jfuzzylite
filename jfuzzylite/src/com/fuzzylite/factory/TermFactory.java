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

/**
 *
 * @author jcrada
 */
public class TermFactory extends Factory<Term> {

    public TermFactory() {
        register(Bell.class);
        register(Constant.class);
        register(Discrete.class);
        register(Function.class);
        register(Gaussian.class);
        register(GaussianProduct.class);
        register(Linear.class);
        register(PiShape.class);
        register(Ramp.class);
        register(Rectangle.class);
        register(SigmoidDifference.class);
        register(Sigmoid.class);
        register(SigmoidProduct.class);
        register(SShape.class);
        register(Trapezoid.class);
        register(Triangle.class);
        register(ZShape.class);
    }

    @Override
    public Term createInstance(String simpleName) {
        Term result = super.createInstance(simpleName);
        if (result instanceof Function) {
            ((Function) result).loadBuiltInFunctions();
        }
        return result;
    }
}
