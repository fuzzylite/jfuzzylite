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

import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FuzzyLite {

    /*Extended DecimalFormat to provide atomic setting of RoundingMode
     Rounding HALF_UP to match most results from fuzzylite C++ rounding mode
     */
    private static class FLDecimalFormat extends DecimalFormat {

		private static final long serialVersionUID = 1L;

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
    public static final String LONG_VERSION = "5.0b1504";
    public static final String LICENSE = "GNU Lesser General Public License v3.0";
    public static final String AUTHOR = "Juan Rada-Vilela, Ph.D.";
    public static final String COMPANY = "FuzzyLite Limited";
    public static final String WEBSITE = "http://www.fuzzylite.com/";
    private static DecimalFormat DF = new FLDecimalFormat("0.000");
    private static int DECIMALS = 3;
    private static double MACHEPS = 1e-6; //Machine epsilon to differentiate numbers
    private static boolean debug = false;

    private static Logger LOGGER = Logger.getLogger("com.fuzzylite");

    static {
        String configurationFile = "/logging.properties";
        final InputStream inputStream = FuzzyLite.class.getResourceAsStream(configurationFile);
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (Exception ex) {
            System.out.println(String.format("WARNING: Could not load default %s file", configurationFile));
            System.out.println(ex);
        }
//        FuzzyLite.logger().info("info");
//        FuzzyLite.logger().warning("warning");
//        FuzzyLite.logger().severe("severe");
//        FuzzyLite.logger().fine("fine");
//        FuzzyLite.logger().finer("finer");
//        FuzzyLite.logger().finest("finest");
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static void setLogger(Logger logger) {
        LOGGER = logger;
    }

    public static java.text.DecimalFormat getFormatter() {
        return DF;
    }

    public static int getDecimals() {
        return DECIMALS;
    }

    public static void setDecimals(int decimals) {
        DECIMALS = decimals;
        StringBuilder pattern = new StringBuilder("0.".length() + decimals);
        pattern.append("0.");
        for (int i = 0; i < decimals; ++i) {
            pattern.append('0');
        }
        DF = new FLDecimalFormat(pattern.toString());
    }

    public static double getMachEps() {
        return MACHEPS;
    }

    public static void setMachEps(double macheps) {
        MACHEPS = macheps;
    }

    public static void setLogging(boolean logging) {
        if (logging) {
            logger().setLevel(debug ? Level.FINE : Level.INFO);
        } else {
            logger().setLevel(Level.OFF);
        }
    }

    public static boolean isLogging() {
        return !(logger().getLevel() == null || Level.OFF.equals(logger().getLevel()));
    }

    public static void setDebug(boolean debug) {
        FuzzyLite.debug = debug;
        if (isLogging()) {
            logger().setLevel(debug ? Level.FINE : Level.INFO);
        }
    }

    public static boolean debug() {
        return FuzzyLite.debug;
    }

}
