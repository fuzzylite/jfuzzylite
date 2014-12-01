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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FuzzyLite {

    //Extended DecimalFormat to provide atomic setting of RoundingMode
    //Rounding HALF_UP to match most results from fuzzylite C++ rounding mode
    private static class FLDecimalFormat extends DecimalFormat {

        public FLDecimalFormat(String pattern) {
            this(pattern, RoundingMode.HALF_UP);
        }

        public FLDecimalFormat(String pattern, RoundingMode roundingMode) {
            super(pattern);
            setRoundingMode(roundingMode);
        }

    }
    public static final String NAME = "jfuzzylite";
    public static final String VERSION = "5.0";
    public static final String LONG_VERSION = "5.0b1412";
    public static final String AUTHOR = "Juan Rada-Vilela, Ph.D.";
    private static DecimalFormat DF = new FLDecimalFormat("0.000");
    private static int DECIMALS = 3;
    private static double MACHEPS = 1e-6; //Machine epsilon to differentiate numbers

    public static Logger logger() {
        return Logger.getLogger("fuzzylite");
    }

    public static java.text.DecimalFormat getFormatter() {
        return DF;
    }

    public static int getDecimals() {
        return DECIMALS;
    }

    public static void setDecimals(int decimals) {
        DECIMALS = decimals;
        String pattern = "0.";
        for (int i = 0; i < decimals; ++i) {
            pattern += "0";
        }
        DF = new FLDecimalFormat(pattern);
    }

    public static double getMachEps() {
        return MACHEPS;
    }

    public static void setMachEps(double macheps) {
        MACHEPS = macheps;
    }

    public static void setLogging(boolean logging) {
        if (logging) {
            if (logger().getLevel() == null || Level.OFF.equals(logger().getLevel())) {
                logger().setLevel(Level.INFO);
            }
        } else {
            logger().setLevel(Level.OFF);
        }
    }

    public static boolean isLogging() {
        return !(logger().getLevel() == null || Level.OFF.equals(logger().getLevel()));
    }

    public static boolean debug() {
        return logger().getLevel() != null
                && logger().getLevel().intValue() < Level.INFO.intValue();
    }

    public static void setDebug(boolean debug) {
        if (debug) {
            if (logger().getLevel() == null
                    || logger().getLevel().intValue() >= Level.INFO.intValue()) {
                logger().setLevel(Level.FINER);
            }
        } else {
            if (logger().getLevel() != null
                    && logger().getLevel().intValue() < Level.INFO.intValue()) {
                logger().setLevel(Level.INFO);
            }
        }
    }

}
