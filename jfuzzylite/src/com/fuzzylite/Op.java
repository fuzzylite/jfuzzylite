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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class Op {

    public static class Pair<Y, Z> {

        public Y first;
        public Z second;

        public Pair() {
            this.first = null;
            this.second = null;
        }

        public Pair(Y first, Z second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "{" + this.first + ":" + this.second + "}";
        }
    }

    /*
     * Math Operations
     */
    public static boolean isEq(double a, double b) {
        return Math.abs(a - b) < FuzzyLite.getMachEps();
    }

    public static boolean isEq(double a, double b, double macheps) {
        return Math.abs(a - b) < macheps;
    }

    public static boolean isLt(double a, double b) {
        return !isEq(a, b, FuzzyLite.getMachEps()) && a < b;
    }

    public static boolean isLt(double a, double b, double macheps) {
        return !isEq(a, b, macheps) && a < b;
    }

    public static boolean isLE(double a, double b) {
        return isEq(a, b, FuzzyLite.getMachEps()) || a < b;
    }

    public static boolean isLE(double a, double b, double macheps) {
        return isEq(a, b, macheps) || a < b;
    }

    public static boolean isGt(double a, double b) {
        return !isEq(a, b, FuzzyLite.getMachEps()) && a > b;
    }

    public static boolean isGt(double a, double b, double macheps) {
        return !isEq(a, b, macheps) && a > b;
    }

    public static boolean isGE(double a, double b) {
        return isEq(a, b, FuzzyLite.getMachEps()) || a > b;
    }

    public static boolean isGE(double a, double b, double macheps) {
        return isEq(a, b, macheps) || a > b;
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

    public static double negate(double x) {
        return -x;
    }

    public static double scale(double x,
            double fromMin, double fromMax, double toMin, double toMax) {
        return (toMax - toMin) / (fromMax - fromMin) * (x - fromMin) + toMin;
    }

    public static List<String> split(String string, String delimiter) {
        return split(string, delimiter, true);
    }

    public static List<String> split(String str, String delimiter, boolean ignoreEmpty) {
        List<String> result = new ArrayList<>();
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
        if (position < 0) {
            return true;
        }

        boolean overflow = false;
        if (array[position] < max[position]) {
            ++array[position];
        } else {
            overflow = (position == 0);
            array[position] = min[position];
            --position;
            if (position >= 0) {
                overflow = increment(array, position, min, max);
            }
        }
        return overflow;
    }

    /*
     * String Operations
     */
    public static <T extends Number> String str(T x) {
        String result = "";
        if (Double.isNaN(x.doubleValue())) {
            result = "nan";
        } else if (Double.isInfinite(x.doubleValue())) {
            if (isLt(x.doubleValue(), 0.0)) {
                result = "-";
            }
            result += "inf";
        } else if (isEq(x.doubleValue(), 0.0)) {
            result = FuzzyLite.getFormatter().format(0.0);
        } else {
            result = FuzzyLite.getFormatter().format(x);
        }
        return result;
    }

    public static <T> String join(Collection<T> x, String separator) {
        String result = "";
        for (Iterator<T> it = x.iterator(); it.hasNext();) {
            T item = it.next();
            if (item instanceof Number) {
                result += Op.str((Number) item);
            } else {
                result += item.toString();
            }
            if (it.hasNext()) {
                result += separator;
            }
        }
        return result;
    }

    public static String join(long[] x, String separator) {
        String result = "";
        for (int i = 0; i < x.length; ++i) {
            result += str(x[i]);
            if (i + 1 < x.length) {
                result += separator;
            }
        }
        return result;
    }

    public static String join(int[] x, String separator) {
        String result = "";
        for (int i = 0; i < x.length; ++i) {
            result += str(x[i]);
            if (i + 1 < x.length) {
                result += separator;
            }
        }
        return result;
    }

    public static String join(double[] x, String separator) {
        String result = "";
        for (int i = 0; i < x.length; ++i) {
            result += str(x[i]);
            if (i + 1 < x.length) {
                result += separator;
            }
        }
        return result;
    }

    public static String join(float[] x, String separator) {
        String result = "";
        for (int i = 0; i < x.length; ++i) {
            result += str(x[i]);
            if (i + 1 < x.length) {
                result += separator;
            }
        }
        return result;
    }

    public static String join(String[] x, String separator) {
        String result = "";
        for (int i = 0; i < x.length; ++i) {
            result += x[i];
            if (i + 1 < x.length) {
                result += separator;
            }
        }
        return result;
    }

    @SafeVarargs
    public static <T> String join(String separator, T... x) {
        String result = "";
        for (int i = 0; i < x.length; ++i) {
            T item = x[i];
            if (item instanceof Number) {
                result += Op.str((Number) item);
            } else {
                result += item.toString();
            }
            if (i + 1 < x.length) {
                result += separator;
            }
        }
        return result;
    }

    public static String makeValidId(String id) {
        if (id == null) {
            return null;
        }
        String result = "";
        for (char c : id.toCharArray()) {
            if (c == '_' || c == '.' || Character.isLetterOrDigit(c)) {
                result += c;
            }
        }
        return result;
    }

}
