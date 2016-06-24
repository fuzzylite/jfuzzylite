/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite;

import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FuzzyLite {

    public static final String NAME = "jfuzzylite";
    public static final String VERSION = "6.0";
    public static final String LIBRARY = NAME + " " + VERSION;
    public static final String LICENSE = "FuzzyLite License";
    public static final String AUTHOR = "Juan Rada-Vilela, Ph.D.";
    public static final String COMPANY = "FuzzyLite Limited";
    public static final String WEBSITE = "http://www.fuzzylite.com/";
    private static int DECIMALS = 3;
    protected static double MACHEPS = 1e-6; //Machine epsilon to differentiate numbers
    private static boolean debugging = false;

    private static DecimalFormat DF = new DecimalFormat("0.000");

    static {
        DF.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private static Logger LOGGER = Logger.getLogger("com.fuzzylite");

    static {
        String configurationFile = "/logging.properties";
        final InputStream inputStream = FuzzyLite.class.getResourceAsStream(configurationFile);
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (Exception ex) {
            System.out.println(String.format("WARNING: Could not load default %s file", configurationFile));
            System.out.println(ex);
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {

            }
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
        DF = new DecimalFormat(pattern.toString());
        DF.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static double getMachEps() {
        return MACHEPS;
    }

    public static void setMachEps(double macheps) {
        MACHEPS = macheps;
    }

    public static void setLogging(boolean logging) {
        if (logging) {
            LOGGER.setLevel(debugging ? Level.FINE : Level.INFO);
        } else {
            LOGGER.setLevel(Level.OFF);
        }
    }

    public static boolean isLogging() {
        return !(LOGGER.getLevel() == null || Level.OFF.equals(LOGGER.getLevel()));
    }

    public static void setDebugging(boolean debugging) {
        FuzzyLite.debugging = debugging;
        if (isLogging()) {
            LOGGER.setLevel(debugging ? Level.FINE : Level.INFO);
        }
    }

    public static boolean isDebugging() {
        return FuzzyLite.debugging;
    }

}
