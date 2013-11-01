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

import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;

/**
 *
 * @author jcrada
 */
public class SigmoidProduct extends Term {

    protected double left, rising;
    protected double falling, right;

    public SigmoidProduct(String name) {
        this(name, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public SigmoidProduct(String name, double left, double rising,
            double falling, double right) {
        this.name = name;
        this.left = left;
        this.rising = rising;
        this.falling = falling;
        this.right = right;
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        double a = 1.0 / (1 + Math.exp(-rising * (x - left)));
        double b = 1.0 / (1 + Math.exp(-falling * (x - right)));
        return a * b;
    }

    @Override
    public String toString() {
        String result = SigmoidProduct.class.getSimpleName();
        result += "(" + Op.join(", ", str(left), str(rising), str(falling), str(right)) + ")";
        return result;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRising() {
        return rising;
    }

    public void setRising(double rising) {
        this.rising = rising;
    }

    public double getFalling() {
        return falling;
    }

    public void setFalling(double falling) {
        this.falling = falling;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }
}
