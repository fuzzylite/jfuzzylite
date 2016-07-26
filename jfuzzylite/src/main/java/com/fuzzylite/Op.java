/*
 Copyright (C) 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite(TM).

 jfuzzylite is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with
 jfuzzylite. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite(R) is a registered trademark of FuzzyLite Limited.
 jfuzzylite(TM) is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Op {

    public static double min(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return a < b ? a : b;
    }

    public static double max(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return a > b ? a : b;
    }

    public static double bound(double x, double min, double max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public static boolean in(double x, double min, double max) {
        return in(x, min, max, true, true);
    }

    public static boolean in(double x, double min, double max, boolean geq,
            boolean leq) {
        boolean left = geq ? isGE(x, min) : isGt(x, min);
        boolean right = leq ? isLE(x, max) : isLt(x, max);
        return (left && right);
    }

    public static boolean isFinite(double x) {
        return !(Double.isNaN(x) || Double.isInfinite(x));
    }

    /*
     * Math Operations
     */
    public static boolean isEq(double a, double b) {
        return a == b || Math.abs(a - b) < FuzzyLite.MACHEPS || (Double.isNaN(a) && Double.isNaN(b));
    }

    public static boolean isEq(double a, double b, double macheps) {
        return a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b));
    }

    public static boolean isNEq(double a, double b) {
        return !(a == b || Math.abs(a - b) < FuzzyLite.MACHEPS || (Double.isNaN(a) && Double.isNaN(b)));
    }

    public static boolean isNEq(double a, double b, double macheps) {
        return !(a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b)));
    }

    public static boolean isLt(double a, double b) {
        return !(a == b || Math.abs(a - b) < FuzzyLite.MACHEPS || (Double.isNaN(a) && Double.isNaN(b)))
                && a < b;
    }

    public static boolean isLt(double a, double b, double macheps) {
        return !(a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b)))
                && a < b;
    }

    public static boolean isLE(double a, double b) {
        return a == b || Math.abs(a - b) < FuzzyLite.MACHEPS || (Double.isNaN(a) && Double.isNaN(b))
                || a < b;
    }

    public static boolean isLE(double a, double b, double macheps) {
        return a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b))
                || a < b;
    }

    public static boolean isGt(double a, double b) {
        return !(a == b || Math.abs(a - b) < FuzzyLite.MACHEPS || (Double.isNaN(a) && Double.isNaN(b)))
                && a > b;
    }

    public static boolean isGt(double a, double b, double macheps) {
        return !(a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b)))
                && a > b;
    }

    public static boolean isGE(double a, double b) {
        return a == b || Math.abs(a - b) < FuzzyLite.MACHEPS || (Double.isNaN(a) && Double.isNaN(b))
                || a > b;
    }

    public static boolean isGE(double a, double b, double macheps) {
        return a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b))
                || a > b;
    }

    public static double eq(double a, double b) {
        return isEq(a, b) ? 1.0 : 0.0;
    }

    public static double neq(double a, double b) {
        return isNEq(a, b) ? 1.0 : 0.0;
    }

    public static double lt(double a, double b) {
        return isLt(a, b) ? 1.0 : 0.0;
    }

    public static double le(double a, double b) {
        return isLE(a, b) ? 1.0 : 0.0;
    }

    public static double gt(double a, double b) {
        return isGt(a, b) ? 1.0 : 0.0;
    }

    public static double ge(double a, double b) {
        return isGE(a, b) ? 1.0 : 0.0;
    }

    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        return a / b;
    }

    public static double modulo(double a, double b) {
        return a % b;
    }

    public static double logicalAnd(double a, double b) {
        return (isEq(a, 1.0) && isEq(b, 1.0)) ? 1.0 : 0.0;
    }

    public static double logicalOr(double a, double b) {
        return (isEq(a, 1.0) || isEq(b, 1.0)) ? 1.0 : 0.0;
    }

    public static double logicalNot(double a) {
        return isEq(a, 1.0) ? 0.0 : 1.0;
    }

    public static double negate(double x) {
        return -x;
    }

    public static double scale(double x, double fromMin, double fromMax,
            double toMin, double toMax) {
        return (toMax - toMin) / (fromMax - fromMin) * (x - fromMin) + toMin;
    }

    public static double scale(double x, double fromMin, double fromMax,
            double toMin, double toMax, boolean bounded) {
        double result = (toMax - toMin) / (fromMax - fromMin) * (x - fromMin)
                + toMin;
        return bounded ? Op.bound(x, toMin, toMax) : result;
    }

    public static double sum(double[] x) {
        double result = 0.0;
        for (double i : x) {
            result += i;
        }
        return result;
    }

    public static double mean(double[] x) {
        return sum(x) / x.length;
    }

    public static double variance(double[] x) {
        return variance(x, mean(x));
    }

    public static double variance(double[] x, double mean) {
        if (x.length == 0) {
            return Double.NaN;
        }
        if (x.length == 1) {
            return 0.0;
        }
        double result = 0.0;
        for (double i : x) {
            result += (i - mean) * (i - mean);
        }
        result /= -1 + x.length;
        return result;
    }

    public static double standardDeviation(double[] x) {
        return standardDeviation(x, mean(x));
    }

    public static double standardDeviation(double[] x, double mean) {
        if (x.length == 0) {
            return Double.NaN;
        }
        if (x.length == 1) {
            return 0.0;
        }
        return Math.sqrt(variance(x, mean));
    }

    public static List<String> split(String string, String delimiter) {
        return split(string, delimiter, true);
    }

    public static List<String> split(String str, String delimiter,
            boolean ignoreEmpty) {
        List<String> result = new ArrayList<String>();
        if (str.isEmpty() || delimiter.isEmpty()) {
            result.add(str);
            return result;
        }
        int position = 0, next = 0;
        while (next >= 0) {
            next = str.indexOf(delimiter, position);
            String token;
            if (next < 0) {
                token = str.substring(position);
            } else {
                token = str.substring(position, next);
            }
            if (!(token.isEmpty() && ignoreEmpty)) {
                result.add(token);
            }
            if (next >= 0) {
                position = next + delimiter.length();
            }
        }
        return result;
    }

    public static double toDouble(String x, double alternative) {
        try {
            return toDouble(x);
        } catch (Exception ex) {
            return alternative;
        }
    }

    public static double toDouble(String x) throws NumberFormatException {
        if ("nan".equals(x)) {
            return Double.NaN;
        }
        if ("inf".equals(x)) {
            return Double.POSITIVE_INFINITY;
        }
        if ("-inf".equals(x)) {
            return Double.NEGATIVE_INFINITY;
        }

        return Double.parseDouble(x);
    }

    public static double[] toDoubles(String x) throws NumberFormatException {
        String[] tokens = x.split("\\s+");
        double[] result = new double[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            result[i] = Op.toDouble(tokens[i]);
        }
        return result;
    }

    public static double[] toDoubles(String x, double alternative) throws NumberFormatException {
        String[] tokens = x.split("\\s+");
        double[] result = new double[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            result[i] = Op.toDouble(tokens[i], alternative);
        }
        return result;
    }

    public static boolean isNumeric(String x) {
        try {
            toDouble(x);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean increment(int[] array, int[] min, int[] max) {
        return increment(array, array.length - 1, min, max);
    }

    public static boolean increment(int[] array, int position, int[] min, int[] max) {
        if (array.length == 0 || position < 0) {
            return false;
        }

        boolean incremented = true;
        if (array[position] < max[position]) {
            ++array[position];
        } else {
            incremented = !(position == 0);
            array[position] = min[position];
            --position;
            if (position >= 0) {
                incremented = increment(array, position, min, max);
            }
        }
        return incremented;
    }

    /*
     * String Operations
     */
    public static <T extends Number> String str(T x) {
        return str(x, FuzzyLite.getFormatter());
    }

    public static <T extends Number> String str(T x, DecimalFormat formatter) {
        if (Double.isNaN(x.doubleValue())) {
            return "nan";
        }
        if (Double.isInfinite(x.doubleValue())) {
            return isLt(x.doubleValue(), 0.0) ? "-inf" : "inf";
        }
        return formatter.format(x);
    }

    public static <T> String join(Collection<T> x, String separator) {
        StringBuilder result = new StringBuilder();
        for (Iterator<T> it = x.iterator(); it.hasNext();) {
            T item = it.next();
            if (item instanceof Number) {
                result.append(Op.str((Number) item));
            } else {
                result.append(item.toString());
            }
            if (it.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String join(long[] x, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; ++i) {
            result.append(str(x[i]));
            if (i + 1 < x.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String join(int[] x, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; ++i) {
            result.append(str(x[i]));
            if (i + 1 < x.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String join(double[] x, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; ++i) {
            result.append(str(x[i]));
            if (i + 1 < x.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String join(float[] x, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; ++i) {
            result.append(str(x[i]));
            if (i + 1 < x.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String join(String[] x, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; ++i) {
            result.append(x[i]);
            if (i + 1 < x.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> String join(String separator, T... x) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; ++i) {
            T item = x[i];
            if (item instanceof Number) {
                result.append(Op.str((Number) item));
            } else {
                result.append(item.toString());
            }
            if (i + 1 < x.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String validName(String id) {
        if (id == null || id.trim().isEmpty()) {
            return "unnamed";
        }
        StringBuilder result = new StringBuilder();
        for (char c : id.toCharArray()) {
            if (c == '_' || c == '.' || Character.isLetterOrDigit(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Classes
     */
    public static interface Cloneable extends java.lang.Cloneable {

        public Object clone() throws CloneNotSupportedException;
    }

    public static class Pair<Y, Z> {

        private Y first;
        private Z second;

        public Pair() {
            this(null, null);
        }

        public Pair(Y first, Z second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "{" + this.first + ":" + this.second + "}";
        }

        public Y getFirst() {
            return first;
        }

        public void setFirst(Y first) {
            this.first = first;
        }

        public Z getSecond() {
            return second;
        }

        public void setSecond(Z second) {
            this.second = second;
        }
    }

}
