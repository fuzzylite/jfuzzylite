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

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 *
 * @author jcrada
 */
public class FuzzyLite {

    public static final String NAME = "jfuzzylite";
    public static final String VERSION = "1.0";
    public static final String LONG_VERSION = "1.0b1401";
    public static final String AUTHOR = "Juan Rada-Vilela";
    protected static DecimalFormat DF = new DecimalFormat("0.000");
    protected static int DECIMALS = 3;
    protected static double MACHEPS = 1e-5; //Machine epsilon to differentiate numbers

    public static Logger logger() {
        return Logger.getGlobal();
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
        DF = new DecimalFormat(pattern);
    }

    public static DecimalFormat getFormatter() {
        return DF;
    }

    public static double getMachEps() {
        return MACHEPS;
    }

    public static void setMachEps(double macheps) {
        MACHEPS = macheps;
    }

    public static void main(String[] args) {
//        logger().setLevel(Level.INFO);
        logger().info("Some log + " + logger().getLevel());
    }

}
