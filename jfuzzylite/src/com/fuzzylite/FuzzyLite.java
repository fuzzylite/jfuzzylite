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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jcrada
 */
public class FuzzyLite {

    //Extended DecimalFormat to provide atomic setting of RoundingMode
    //Rounding HALF_UP to match most results from fuzzylite C++ rounding mode
    protected static class FLDecimalFormat extends DecimalFormat {

        public FLDecimalFormat(String pattern) {
            this(pattern, RoundingMode.HALF_UP);
        }

        public FLDecimalFormat(String pattern, RoundingMode roundingMode) {
            super(pattern);
            setRoundingMode(roundingMode);
        }

    }
    public static final String NAME = "jfuzzylite";
    public static final String VERSION = "1.0";
    public static final String LONG_VERSION = "1.0b1401";
    public static final String AUTHOR = "Juan Rada-Vilela";
    protected static DecimalFormat DF = new FLDecimalFormat("0.000");
    protected static int DECIMALS = 3;
    protected static double MACHEPS = 1e-5; //Machine epsilon to differentiate numbers

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
