/*
 jfuzzylite (TM), a fuzzy logic control library in Java.
 Copyright (C) 2010-2016 FuzzyLite Limited. All rights reserved.
 Author: Juan Rada-Vilela, Ph.D. <jcrada@fuzzylite.com>

 This file is part of jfuzzylite.

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 jfuzzylite is a trademark of FuzzyLite Limited.
 fuzzylite (R) is a registered trademark of FuzzyLite Limited.
 */
package com.fuzzylite.defuzzifier;

import com.fuzzylite.FuzzyLite;
import com.fuzzylite.Op;
import com.fuzzylite.term.Concave;
import com.fuzzylite.term.Constant;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Ramp;
import com.fuzzylite.term.SShape;
import com.fuzzylite.term.Sigmoid;
import com.fuzzylite.term.Term;
import com.fuzzylite.term.ZShape;

/**
 The WeightedDefuzzifier class is the base class for defuzzifiers which compute
 a weighted function on the fuzzy set without requiring to integrate over the
 fuzzy set.

 @author Juan Rada-Vilela, Ph.D.
 @since 5.0
 */
public abstract class WeightedDefuzzifier extends Defuzzifier {

    /**
     The Type enum indicates the type of the WeightedDefuzzifier based the terms
     included in the fuzzy set.
     */
    public enum Type {
        /**
         Automatic: Automatically inferred from the terms
         */
        Automatic,
        /**
         TakagiSugeno: Manually set to TakagiSugeno (or Inverse Tsukamoto)
         */
        TakagiSugeno,
        /**
         Tsukamoto: Manually set to Tsukamoto
         */
        Tsukamoto
    }

    private Type type;

    public WeightedDefuzzifier() {
        this(Type.Automatic);
    }

    public WeightedDefuzzifier(String type) {
        this(Type.valueOf(type));
    }

    public WeightedDefuzzifier(Type type) {
        this.type = type;
    }

    /**
     Gets the type of the weighted defuzzifier

     @return the type of the weighted defuzzifier
     */
    public Type getType() {
        return type;
    }

    /**
     Sets the type of the weighted defuzzifier

     @param type is the type of the weighted defuzzifier
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     Infers the type of the defuzzifier based on the given term. If the given
     term is Constant, Linear or Function, then the type is TakagiSugeno;
     otherwise, the type is Tsukamoto

     @param term is the given term
     @return the inferred type of the defuzzifier based on the given term
     */
    public Type inferType(Term term) {
        if (term instanceof Constant || term instanceof Linear || term instanceof Function) {
            return Type.TakagiSugeno;
        }
        return Type.Tsukamoto;
    }

    /**
     Indicates if the given term is monotonic

     @param term is the given term
     @return whether the given term is monotonic
     */
    public boolean isMonotonic(Term term) {
        return term instanceof Concave
                || term instanceof Ramp
                || term instanceof Sigmoid
                || term instanceof SShape
                || term instanceof ZShape;
    }

    /**
     Computes the Tsukamoto @f$z@f$-value for the given monotonic term. If the
     term is not monotonic, then the TakagiSugeno (or InverseTsukamoto)

     @f$z@f$-value is computed.

     @param monotonic is the monotonic term
     @param activationDegree is the activation degree for the term
     @param minimum is the minimum value of the range of the term
     @param maximum is the maximum value of the range of the term
     @return the Tsukamoto @f$z@f$-value for the given monotonic term, or the
     TakagiSugeno (or InverseTsukamoto) @f$z@f$-value if the term is not
     monotonic.
     */
    public double tsukamoto(Term monotonic, double activationDegree,
            double minimum, double maximum) {
        double w = activationDegree;
        double z = Double.NaN;

        boolean isTsukamoto = true;

        if (monotonic instanceof Ramp) {
            Ramp ramp = (Ramp) monotonic;
            z = Op.scale(w, 0, 1, ramp.getStart(), ramp.getEnd());

        } else if (monotonic instanceof Sigmoid) {
            Sigmoid sigmoid = (Sigmoid) monotonic;
            if (Op.isEq(w, 1.0)) {
                if (Op.isGE(sigmoid.getSlope(), 0.0)) {
                    z = maximum;
                } else {
                    z = minimum;
                }

            } else if (Op.isEq(w, 0.0)) {
                if (Op.isGE(sigmoid.getSlope(), 0.0)) {
                    z = minimum;
                } else {
                    z = maximum;
                }

            } else {
                double a = sigmoid.getSlope();
                double b = sigmoid.getInflection();
                z = b + (Math.log(1.0 / w - 1.0) / -a);
            }

        } else if (monotonic instanceof SShape) {
            SShape sshape = (SShape) monotonic;
            double difference = sshape.getEnd() - sshape.getStart();
            double a = sshape.getStart() + Math.sqrt(w * difference * difference / 2.0);
            double b = sshape.getEnd() + Math.sqrt(difference * difference * (w - 1.0) / -2.0);
            if (Math.abs(w - monotonic.membership(a))
                    < Math.abs(w - monotonic.membership(b))) {
                z = a;
            } else {
                z = b;
            }

        } else if (monotonic instanceof ZShape) {
            ZShape zshape = (ZShape) monotonic;
            double difference = zshape.getEnd() - zshape.getStart();
            double a = zshape.getStart() + Math.sqrt(difference * difference * (w - 1) / -2.0);
            double b = zshape.getEnd() + Math.sqrt(w * difference * difference / 2.0);
            if (Math.abs(w - monotonic.membership(a))
                    < Math.abs(w - monotonic.membership(b))) {
                z = a;
            } else {
                z = b;
            }

        } else if (monotonic instanceof Concave) {
            Concave concave = (Concave) monotonic;
            double i = concave.getInflection();
            double e = concave.getEnd();
            z = (i - e) / concave.membership(w) + 2 * e - i;

        } else {
            isTsukamoto = false;
        }

        if (isTsukamoto) {
            double fz = monotonic.membership(z);
            //Compare difference between estimated and true value
            if (FuzzyLite.isDebugging()) {
                if (!Op.isEq(w, fz, 1e-2)) {
                    FuzzyLite.logger().fine(String.format(
                            "[tsukamoto warning] difference <%s> might suggest an inaccurate "
                            + "computation of z because it is expected w=f(z) in %s term <%s>, "
                            + "but w=%s f(z)=%s and z=%s", Op.str(Math.abs(w - fz)),
                            monotonic.getClass().getSimpleName(), monotonic.getName(),
                            Op.str(w), Op.str(fz), Op.str(z)));
                }
            }
        } else {
            // else fallback to the regular Takagi-Sugeno or inverse Tsukamoto (according to term)
            z = monotonic.membership(w);
        }
        return z;
    }

    @Override
    public WeightedDefuzzifier clone() throws CloneNotSupportedException {
        return (WeightedDefuzzifier) super.clone();
    }
}
