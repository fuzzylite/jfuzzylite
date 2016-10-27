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

/**
 The Op class (short for Operation) contains methods for numeric operations,
 string manipulation, and other functions.

 @author Juan Rada-Vilela, Ph.D.
 @since 4.0
 */
public class Op {

    /**
     Returns the minimum between the two parameters

     @param a
     @param b
     @return @f$\min(a,b)@f$
     */
    public static double min(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return a < b ? a : b;
    }

    /**
     Returns the maximum between the two parameters

     @param a
     @param b
     @return @f$\max(a,b)@f$
     */
    public static double max(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return a > b ? a : b;
    }

    /**
     Returns @f$x@f$ bounded in @f$[\min,\max]@f$

     @param x is the value to be bounded
     @param min is the minimum value of the range
     @param max is the maximum value of the range
     @return @f$ \begin{cases} \min & \mbox{if $x < \min$} \cr
     \max & \mbox{if $x > \max$} \cr x & \mbox{otherwise} \end{cases}
     @f$
     */
    public static double bound(double x, double min, double max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    /**
     Indicates whether @f$x@f$ is within the closed boundaries

     @param x is the value
     @param min is the minimum of the range
     @param max is the maximum of the range
     @return @f$ \begin{cases} x \in [\min,\max] & \mbox{if $geq \wedge leq$}
     \cr x \in (\min,\max] & \mbox{if $geq \wedge \bar{leq}$} \cr x \in [\min,
     \max) & \mbox{if $\bar{geq} \wedge leq$} \cr x \in (\min, \max) & \mbox{if
     $\bar{geq} \wedge \bar{leq}$} \cr \end{cases}
     @f$
     */
    public static boolean in(double x, double min, double max) {
        return in(x, min, max, true, true);
    }

    /**
     Indicates whether @f$x@f$ is within the boundaries (open or closed)

     @param x is the value
     @param min is the minimum of the range
     @param max is the maximum of the range
     @param geq determines whether the maximum is a closed interval
     @param leq determines whether the minimum is a closed interval
     @return @f$ \begin{cases} x \in [\min,\max] & \mbox{if $geq \wedge leq$}
     \cr x \in (\min,\max] & \mbox{if $geq \wedge \bar{leq}$} \cr x \in [\min,
     \max) & \mbox{if $\bar{geq} \wedge leq$} \cr x \in (\min, \max) & \mbox{if
     $\bar{geq} \wedge \bar{leq}$} \cr \end{cases}
     @f$
     */
    public static boolean in(double x, double min, double max, boolean geq,
            boolean leq) {
        boolean left = geq ? isGE(x, min) : isGt(x, min);
        boolean right = leq ? isLE(x, max) : isLt(x, max);
        return (left && right);
    }

    /**
     Indicates whether @f$x@f$ is finite, that is, @f$x \not\in \{\pm\infty,
     \mathrm{NaN}\}@f$

     @param x is the value
     @return whether @f$x@f$ is finite
     */
    public static boolean isFinite(double x) {
        return !(Double.isNaN(x) || Double.isInfinite(x));
    }

    /*
     * Math Operations
     */
    /**
     Returns whether @f$a@f$ is equal to @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is equal to @f$b@f$ at the default tolerance
     */
    public static boolean isEq(double a, double b) {
        return a == b || Math.abs(a - b) < FuzzyLite.macheps || (Double.isNaN(a) && Double.isNaN(b));
    }

    /**
     Returns whether @f$a@f$ is equal to @f$b@f$ at the given tolerance

     @param a
     @param b
     @param macheps is the minimum difference upon which two floating-point
     values are considered equivalent
     @return whether @f$a@f$ is equal to @f$b@f$ at the given tolerance
     */
    public static boolean isEq(double a, double b, double macheps) {
        return a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b));
    }

    /**
     Returns whether @f$a@f$ is different from @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is different from @f$b@f$ at the default tolerance
     */
    public static boolean isNEq(double a, double b) {
        return !(a == b || Math.abs(a - b) < FuzzyLite.macheps || (Double.isNaN(a) && Double.isNaN(b)));
    }

    /**
     Returns whether @f$a@f$ is different from @f$b@f$ at the given tolerance

     @param a
     @param b
     @param macheps is the minimum difference upon which two floating-point
     values are considered equivalent
     @return whether @f$a@f$ is different from @f$b@f$ at the given tolerance
     */
    public static boolean isNEq(double a, double b, double macheps) {
        return !(a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b)));
    }

    /**
     Returns whether @f$a@f$ is less than @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is less than @f$b@f$ at the default tolerance
     */
    public static boolean isLt(double a, double b) {
        return !(a == b || Math.abs(a - b) < FuzzyLite.macheps || (Double.isNaN(a) && Double.isNaN(b)))
                && a < b;
    }

    /**
     Returns whether @f$a@f$ is less than @f$b@f$ at the given tolerance

     @param a
     @param b
     @param macheps is the minimum difference upon which two floating-point
     values are considered equivalent
     @return whether @f$a@f$ is less than @f$b@f$ at the given tolerance
     */
    public static boolean isLt(double a, double b, double macheps) {
        return !(a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b)))
                && a < b;
    }

    /**
     Returns whether @f$a@f$ is less than or equal to @f$b@f$ at the default
     tolerance

     @param a
     @param b
     @return whether @f$a@f$ is less than or equal to @f$b@f$ at the default
     tolerance
     */
    public static boolean isLE(double a, double b) {
        return a == b || Math.abs(a - b) < FuzzyLite.macheps || (Double.isNaN(a) && Double.isNaN(b))
                || a < b;
    }

    /**
     Returns whether @f$a@f$ is less than or equal to @f$b@f$ at the given
     tolerance

     @param a
     @param b
     @param macheps is the minimum difference upon which two floating-point
     values are considered equivalent
     @return whether @f$a@f$ is less than or equal to @f$b@f$ at the given
     tolerance
     */
    public static boolean isLE(double a, double b, double macheps) {
        return a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b))
                || a < b;
    }

    /**
     Returns whether @f$a@f$ is greater than @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is greater than @f$b@f$ at the given tolerance
     */
    public static boolean isGt(double a, double b) {
        return !(a == b || Math.abs(a - b) < FuzzyLite.macheps || (Double.isNaN(a) && Double.isNaN(b)))
                && a > b;
    }

    /**
     Returns whether @f$a@f$ is greater than @f$b@f$ at the given tolerance

     @param a
     @param b
     @param macheps is the minimum difference upon which two floating-point
     values are considered equivalent
     @return whether @f$a@f$ is greater than @f$b@f$ at the given tolerance
     */
    public static boolean isGt(double a, double b, double macheps) {
        return !(a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b)))
                && a > b;
    }

    /**
     Returns whether @f$a@f$ is greater than or equal to @f$b@f$ at the default
     tolerance

     @param a
     @param b
     @return whether @f$a@f$ is greater than or equal to @f$b@f$ at the default
     tolerance
     */
    public static boolean isGE(double a, double b) {
        return a == b || Math.abs(a - b) < FuzzyLite.macheps || (Double.isNaN(a) && Double.isNaN(b))
                || a > b;
    }

    /**
     Returns whether @f$a@f$ is greater than or equal to @f$b@f$ at the given
     tolerance

     @param a
     @param b
     @param macheps is the minimum difference upon which two floating-point
     values are considered equivalent
     @return whether @f$a@f$ is greater than or equal to @f$b@f$ at the given
     tolerance
     */
    public static boolean isGE(double a, double b, double macheps) {
        return a == b || Math.abs(a - b) < macheps || (Double.isNaN(a) && Double.isNaN(b))
                || a > b;
    }

    /**
     Returns whether @f$a@f$ is equal to @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is equal to @f$b@f$ at the default tolerance
     */
    public static double eq(double a, double b) {
        return isEq(a, b) ? 1.0 : 0.0;
    }

    /**
     Returns whether @f$a@f$ is different from @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is different from @f$b@f$ at the default tolerance
     */
    public static double neq(double a, double b) {
        return isNEq(a, b) ? 1.0 : 0.0;
    }

    /**
     Returns whether @f$a@f$ is less than @f$b@f$ at the default tolerance

     @param a
     @param b
     @return whether @f$a@f$ is less than @f$b@f$ at the default tolerance
     */
    public static double lt(double a, double b) {
        return isLt(a, b) ? 1.0 : 0.0;
    }

    /**
     Returns whether @f$a@f$ is less than or equal to @f$b@f$ at the default
     tolerance

     @param a
     @param b
     @return whether @f$a@f$ is less than or equal to @f$b@f$ at the default
     tolerance
     */
    public static double le(double a, double b) {
        return isLE(a, b) ? 1.0 : 0.0;
    }

    /**
     Returns whether @f$a@f$ is greater than or equal to @f$b@f$ at the default
     tolerance

     @param a
     @param b
     @return whether @f$a@f$ is greater than or equal to @f$b@f$ at the default
     tolerance
     */
    public static double gt(double a, double b) {
        return isGt(a, b) ? 1.0 : 0.0;
    }

    /**
     Returns whether @f$a@f$ is greater than or equal to @f$b@f$ at the default
     tolerance

     @param a
     @param b
     @return whether @f$a@f$ is greater than or equal to @f$b@f$ at the default
     tolerance
     */
    public static double ge(double a, double b) {
        return isGE(a, b) ? 1.0 : 0.0;
    }

    /**
     Adds two values

     @param a
     @param b
     @return @f$a+b@f$
     */
    public static double add(double a, double b) {
        return a + b;
    }

    /**
     Subtracts two values

     @param a
     @param b
     @return @f$a-b@f$
     */
    public static double subtract(double a, double b) {
        return a - b;
    }

    /**
     Multiplies two values

     @param a
     @param b
     @return @f$a\times b@f$
     */
    public static double multiply(double a, double b) {
        return a * b;
    }

    /**
     Divides two values

     @param a
     @param b
     @return @f$a/b@f$
     */
    public static double divide(double a, double b) {
        return a / b;
    }

    /**
     Computes the modulo

     @param a
     @param b
     @return @f$a \mod b@f$
     */
    public static double modulo(double a, double b) {
        return a % b;
    }

    /**
     Computes the logical AND

     @param a
     @param b
     @return @f$ \begin{cases} 1.0 & \mbox{if $a=1 \wedge b=1$}\cr 0.0 &
     \mbox{otherwise} \end{cases}
     @f$
     */
    public static double logicalAnd(double a, double b) {
        return (isEq(a, 1.0) && isEq(b, 1.0)) ? 1.0 : 0.0;
    }

    /**
     Computes the logical OR

     @param a
     @param b
     @return @f$ \begin{cases} 1.0 & \mbox{if $a=1 \vee b=1$}\cr 0.0 &
     \mbox{otherwise} \end{cases}
     @f$
     */
    public static double logicalOr(double a, double b) {
        return (isEq(a, 1.0) || isEq(b, 1.0)) ? 1.0 : 0.0;
    }

    /**
     Returns the complement of the value

     @param a
     @return @f$ \begin{cases} 0.0 & \mbox{if $a=1$}\cr 1.0 & \mbox{otherwise}
     \end{cases}
     @f$
     */
    public static double logicalNot(double a) {
        return isEq(a, 1.0) ? 0.0 : 1.0;
    }

    /**
     Negates the value

     @param a
     @return -a
     */
    public static double negate(double a) {
        return -a;
    }

    /**
     Linearly interpolates the parameter @f$x@f$ in range `[fromMin,fromMax]` to
     a new value in the range `[toMin,toMax]`

     @param x is the source value to interpolate
     @param fromMin is the minimum value of the source range
     @param fromMax is the maximum value of the source range
     @param toMin is the minimum value of the target range
     @param toMax is the maximum value of the target range
     @return the source value linearly interpolated to the target range:
     @f$ y = y_a + (y_b - y_a) \dfrac{x-x_a}{x_b-x_a} @f$
     */
    public static double scale(double x, double fromMin, double fromMax,
            double toMin, double toMax) {
        return (toMax - toMin) / (fromMax - fromMin) * (x - fromMin) + toMin;
    }

    /**
     Linearly interpolates the parameter @f$x@f$ in range `[fromMin,fromMax]` to
     a new value in the range `[toMin,toMax]`, truncated to the range
     `[toMin,toMax]` if bounded is `true`.

     @param x is the source value to interpolate
     @param fromMin is the minimum value of the source range
     @param fromMax is the maximum value of the source range
     @param toMin is the minimum value of the target range
     @param toMax is the maximum value of the target range
     @param bounded determines whether the resulting value is bounded to the
     range
     @return the source value linearly interpolated to the target range:
     @f$ y = y_a + (y_b - y_a) \dfrac{x-x_a}{x_b-x_a} @f$
     */
    public static double scale(double x, double fromMin, double fromMax,
            double toMin, double toMax, boolean bounded) {
        double result = (toMax - toMin) / (fromMax - fromMin) * (x - fromMin)
                + toMin;
        return bounded ? Op.bound(x, toMin, toMax) : result;
    }

    /**
     Computes the sum of the array

     @param x is the array of elements
     @return the sum of the array
     */
    public static double sum(double[] x) {
        double result = 0.0;
        for (double i : x) {
            result += i;
        }
        return result;
    }

    /**
     Computes the mean of the array

     @param x is the array
     @return @f$\dfrac{\sum_i{x_i}}{|x|}@f$
     */
    public static double mean(double[] x) {
        return sum(x) / x.length;
    }

    /**
     Computes the variance of the array

     @param x is the array
     @return @f$ \sum_i{ (x_i - \bar{x})^2 } / (|x| - 1) @f$
     */
    public static double variance(double[] x) {
        return variance(x, mean(x));
    }

    /**
     Computes the variance of the vector using the given mean

     @param x is the vector
     @param mean is the mean value of the vector
     @return @f$ \sum_i{ (x_i - \bar{x})^2 } / (|x| - 1) @f$
     */
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

    /**
     Computes the standard deviation of the vector

     @param x
     @return @f$ \sqrt{\mbox{variance}(x, \bar{x})} @f$
     */
    public static double standardDeviation(double[] x) {
        return standardDeviation(x, mean(x));
    }

    /**
     Computes the standard deviation of the vector using the given mean

     @param x
     @param mean is the mean value of x
     @return @f$ \sqrt{\mbox{variance}(x, \bar{x})} @f$
     */
    public static double standardDeviation(double[] x, double mean) {
        if (x.length == 0) {
            return Double.NaN;
        }
        if (x.length == 1) {
            return 0.0;
        }
        return Math.sqrt(variance(x, mean));
    }

    /**
     Splits the string around the given delimiter ignoring empty splits

     @param string is the string to split
     @param delimiter is the substrings on which the string will be split
     @return the string split around the given delimiter
     */
    public static List<String> split(String string, String delimiter) {
        return split(string, delimiter, true);
    }

    /**
     Splits the string around the given delimiter

     @param string is the string to split
     @param delimiter is the substrings on which the string will be split
     @param ignoreEmpty whether the empty strings are discarded
     @return the string split around the given delimiter
     */
    public static List<String> split(String string, String delimiter,
            boolean ignoreEmpty) {
        List<String> result = new ArrayList<String>();
        if (string.isEmpty() || delimiter.isEmpty()) {
            result.add(string);
            return result;
        }
        int position = 0, next = 0;
        while (next >= 0) {
            next = string.indexOf(delimiter, position);
            String token;
            if (next < 0) {
                token = string.substring(position);
            } else {
                token = string.substring(position, next);
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

    /**
     Parses the given string into a scalar value without throwing an exception

     @param x is the string to parse
     @param alternative is the value to return if the string does not contain a
     scalar value
     @return the given string into a scalar value or the alternative value if
     the string does not contain a scalar value
     */
    public static double toDouble(String x, double alternative) {
        try {
            return toDouble(x);
        } catch (Exception ex) {
            return alternative;
        }
    }

    /**
     Parses the given string into a scalar value

     @param x is the string to parse
     @return the given string into a scalar value
     @throws NumberFormatException if the string does not contain a scalar value
     */
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

    /**
     Parses the given string into an array of scalar values

     @param x is the string containing space-separated values to parse
     @return the vector of scalar values
     @throws NumberFormatException if the string contains an invalid scalar
     value
     */
    public static double[] toDoubles(String x) throws NumberFormatException {
        String[] tokens = x.split("\\s+");
        double[] result = new double[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            result[i] = Op.toDouble(tokens[i]);
        }
        return result;
    }

    /**
     Parses the given string into a vector of scalar values

     @param x is the string containing space-separated values to parse
     @param alternative is the value to use if an invalid value is found
     @return the vector of scalar values
     */
    public static double[] toDoubles(String x, double alternative) throws NumberFormatException {
        String[] tokens = x.split("\\s+");
        double[] result = new double[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            result[i] = Op.toDouble(tokens[i], alternative);
        }
        return result;
    }

    /**
     Indicates whether the string can be converted to a numeric value.

     @param x
     @return whether the string can be converted to a numeric value
     */
    public static boolean isNumeric(String x) {
        try {
            toDouble(x);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     Increments @f$x@f$ by the unit, treating the entire vector as a number. For
     example, incrementing a few times @f$x_0=\{0,0\}@f$ within boundaries

     @f$[0,1]@f$ results in: @f$x_1=\{0,1\}@f$,

     @f$x_2=\{1,0\}@f$, @f$x_3=\{1,1\}@f$, @f$x_4=\{0,0\}@f$.
     @param array is the array to increment
     @param min is the minimum value of the dimension
     @param max is the maximum value of the dimension
     @return `true` if @f$x@f$ was incremented, `false` otherwise (e.g.,
     incrementing @f$x_3@f$ returns `false`). In earlier versions to 6.0, the
     result was the inverse and indicated whether the counter had overflown
     (most sincere apologies for this change).
     */
    public static boolean increment(int[] array, int[] min, int[] max) {
        return increment(array, array.length - 1, min, max);
    }

    /**
     Increments @f$x@f$ by the unit at the given position, treating the entire
     vector as a number. For example, incrementing

     @f$x_0=\{0,0,0\}@f$ within boundaries @f$[0,1]@f$ at the second position
     results in: @f$x_1=\{0,1,0\}@f$, @f$x_2=\{1,0,0\}@f$,
     @f$x_3=\{1,1,0\}@f$, @f$x_4=\{0,0,0\}@f$.
     @param array is the vector to increment
     @param position is the position of the vector to increment, where smaller
     values lead to higher significance digits
     @param min is the minimum value of the dimension
     @param max is the maximum value of the dimension
     @return `true` if @f$x@f$ was incremented, `false` otherwise (e.g.,
     incrementing @f$x_3@f$ returns `false`). In earlier versions to 6.0, the
     result was the inverse and indicated whether the counter had overflown
     (most sincere apologies for this change).
     */
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

    /**
     Returns a string representation of the given value

     @tparam T determines the class type of the value
     @param x is the value
     @return a string representation of the given value
     */
    public static <T extends Number> String str(T x) {
        return str(x, FuzzyLite.getFormatter());
    }

    /**
     Returns a string representation of the given value

     @tparam T determines the class type of the value
     @param x is the value
     @param formatter is the decimal formatter of scalar values
     @return a string representation of the given value
     */
    public static <T extends Number> String str(T x, DecimalFormat formatter) {
        if (Double.isNaN(x.doubleValue())) {
            return "nan";
        }
        if (Double.isInfinite(x.doubleValue())) {
            return isLt(x.doubleValue(), 0.0) ? "-inf" : "inf";
        }
        return formatter.format(x);
    }

    /**
     Joins an array of elements by the given separator into a single string. The
     elements are represented as strings utilizing the Op.str() method on each
     element

     @tparam T determines the class type of the value
     @param x is the array of elements
     @param separator is the string to add between the elements
     @return a single string joining the array of elements by the given
     separator
     */
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

    /**
     Joins an array of elements by the given separator into a single string. The
     elements are represented as strings utilizing the Op.str() method on each
     element

     @param x is the array of elements
     @param separator is the string to add between the elements
     @return a single string joining the array of elements by the given
     separator
     */
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

    /**
     Joins an array of elements by the given separator into a single string. The
     elements are represented as strings utilizing the Op.str() method on each
     element

     @param x is the array of elements
     @param separator is the string to add between the elements
     @return a single string joining the array of elements by the given
     separator
     */
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

    /**
     Joins an array of elements by the given separator into a single string. The
     elements are represented as strings utilizing the Op.str() method on each
     element

     @param x is the array of elements
     @param separator is the string to add between the elements
     @return a single string joining the array of elements by the given
     separator
     */
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

    /**
     Joins an array of elements by the given separator into a single string. The
     elements are represented as strings utilizing the Op.str() method on each
     element

     @param x is the array of elements
     @param separator is the string to add between the elements
     @return a single string joining the array of elements by the given
     separator
     */
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

    /**
     Joins an array of elements by the given separator into a single string.

     @param x is the array of elements
     @param separator is the string to add between the elements
     @return a single string joining the array of elements by the given
     separator
     */
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

    /**
     Joins a variadic number of elements by the given separator into a single
     string. The elements are represented as strings utilizing the Op.str()
     method on each element

     @tparam T determines the type of elements in the array
     @param separator is the string to add between the elements
     @param items are the elements to join
     @return a single string joining the variadic number of elements by the
     given separator
     */
    @SuppressWarnings("unchecked")
    public static <T> String join(String separator, T... items) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < items.length; ++i) {
            T item = items[i];
            if (item instanceof Number) {
                result.append(Op.str((Number) item));
            } else {
                result.append(item.toString());
            }
            if (i + 1 < items.length) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    /**
     Returns a valid name for variables

     @param name
     @return an name whose characters are in `[a-zA-Z_0-9.]`
     */
    public static String validName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "unnamed";
        }
        StringBuilder result = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (c == '_' || c == '.' || Character.isLetterOrDigit(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     Cloneable is an interface that provides a public method to clone
     */
    public interface Cloneable extends java.lang.Cloneable {

        Object clone() throws CloneNotSupportedException;
    }

    /**
     Pair is a handy class to store pairs of elements of any class type

     @param <Y> is the class of the first element
     @param <Z> is the class of the second element
     */
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

        /**
         Gets the first element stored in the pair

         @return the first element stored in the pair
         */
        public Y getFirst() {
            return first;
        }

        /**
         Sets the first element to store in the pair

         @param first is the first element to store in the pair
         */
        public void setFirst(Y first) {
            this.first = first;
        }

        /**
         Gets the second element stored in the pair

         @return the second element stored in the pair
         */
        public Z getSecond() {
            return second;
        }

        /**
         Sets the second element to store in the pair

         @param second is the second element to store in the pair
         */
        public void setSecond(Z second) {
            this.second = second;
        }
    }

}
