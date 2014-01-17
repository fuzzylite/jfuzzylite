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
package com.fuzzylite.term;

import java.util.ArrayList;
import java.util.List;

import com.fuzzylite.Op;

/**
 *
 * @author jcrada
 */
public class Discrete extends Term {

    public List<Double> x, y;

    public Discrete() {
        this("");
    }

    public Discrete(String name) {
        this(name, new ArrayList<Double>(), new ArrayList<Double>());
    }

    public Discrete(String name, List<Double> x, List<Double> y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String parameters() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.size(); ++i) {
            result.append(Op.join(" ", x.get(i), y.get(i)));
            if (i + 1 < x.size()) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");

        if (values.size() % 2 != 0) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires an even set of parameters values (x,y), "
                    + "but found <%d> values ",
                    this.getClass().getSimpleName(), values.size()));
        }

        x.clear();
        y.clear();
        for (int i = 0; i + 1 < values.size(); i += 2) {
            x.add(Op.toDouble(values.get(i)));
            y.add(Op.toDouble(values.get(i + 1)));
        }
    }

    public static Discrete create(String name, double... xy) {
        if (xy.length < 2 || xy.length % 2 != 0) {
            throw new RuntimeException("[discrete term] expected an even number of parameters "
                    + "matching (x,y)+, but passed <" + xy.length + ">");
        }
        List<Double> x = new ArrayList<>(xy.length / 2);
        List<Double> y = new ArrayList<>(xy.length / 2);
        for (int i = 0; i < xy.length; i += 2) {
            x.add(xy[i]);
            y.add(xy[i + 1]);
        }
        return new Discrete(name, x, y);
    }

    @Override
    public double membership(double _x_) {
        if (Double.isNaN(_x_)) {
            return Double.NaN;
        }
        if (x.isEmpty() || y.isEmpty()) {
            return 0.0;
        }
        if (x.size() != y.size()) {
            throw new RuntimeException("[discrete term] vectors x[" + x.size() + "] "
                    + "and y[" + y.size() + "] have different sizes");
        }

        /*                ______________________
         *               /                      \
         *              /                        \
         * ____________/                          \____________
         *            x[0]                      x[n-1]
         */
        if (Op.isLE(_x_, x.get(0))) {
            return y.get(0);
        }
        if (Op.isGE(_x_, x.get(x.size() - 1))) {
            return y.get(x.size() - 1);
        }

        int lower = -1, upper = -1;
        for (int i = 0; i < x.size(); ++i) {
            if (Op.isEq(x.get(i), _x_)) {
                return y.get(i);
            }
            //approximate on the left
            if (Op.isLt(x.get(i), _x_)) {
                lower = i;
            }
            if (Op.isGt(x.get(i), _x_)) {
                upper = i;
                break;
            }
        }

        if (upper < 0) {
            upper = x.size() - 1;
        }
        if (lower < 0) {
            lower = 0;
        }
        return Op.scale(_x_, x.get(lower), x.get(upper), y.get(lower), y.get(upper));
    }

    @Override
    public Discrete clone() {
        return new Discrete(this.name,
                new ArrayList<>(this.x),
                new ArrayList<>(this.y));
    }

}
