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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.term.Discrete.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 The Discrete class is a basic Term that represents a discrete membership
 function. The pairs of values in any Discrete term **must** be sorted
 ascendently because the membership function is computed using binary search to
 find the lower and upper bounds of @f$x@f$.

 @image html discrete.svg

 @author Juan Rada-Vilela, Ph.D.
 @see Term
 @see Variable
 @since 4.0
 */
public class Discrete extends Term implements List<Pair> {

    /**
     The Pair class represents a pair of floating-point values to represent a
     discrete pair
     */
    public static class Pair implements Op.Cloneable {

        public double x;
        public double y;

        public Pair() {
            this(Double.NaN, Double.NaN);
        }

        public Pair(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         Gets the x value

         @return the x value
         */
        public double getX() {
            return x;
        }

        /**
         Sets the x value

         @param x is the x value
         */
        public void setX(double x) {
            this.x = x;
        }

        /**
         Gets the y value

         @return the y value
         */
        public double getY() {
            return y;
        }

        /**
         Sets the y value

         @param y is the y value
         */
        public void setY(double y) {
            this.y = y;
        }

        @Override
        public Pair clone() throws CloneNotSupportedException {
            return (Pair) super.clone();
        }

        @Override
        public String toString() {
            return "(" + Op.str(x) + "," + Op.str(y) + ")";
        }

    }

    private List<Pair> xy;

    public Discrete() {
        this("");
    }

    public Discrete(String name) {
        this(name, new ArrayList<Pair>());
    }

    public Discrete(String name, List<Pair> xy) {
        this(name, xy, 1.0);
    }

    public Discrete(String name, List<Pair> xy, double height) {
        super(name, height);
        this.xy = xy;
    }

    /**
     Returns the parameters of the term as `x1 y1 xn yn [height]`

     @return `x1 y1 xn yn [height]`
     */
    @Override
    public String parameters() {
        StringBuilder result = new StringBuilder();
        Iterator<Pair> it = xy.iterator();
        while (it.hasNext()) {
            Pair pair = it.next();
            result.append(String.format("%s %s",
                    Op.str(pair.getX()), Op.str(pair.getY())));
            if (it.hasNext()) {
                result.append(" ");
            }
        }
        if (!Op.isEq(height, 1.0)) {
            result.append(String.format(" %s", Op.str(height)));
        }
        return result.toString();
    }

    /**
     Configures the term with the parameters given as `x1 y1 xn yn [height]`

     @param parameters as `x1 y1 xn yn [height]`
     */
    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        LinkedList<Double> values = new LinkedList<Double>();

        List<String> strValues = Op.split(parameters, " ");
        for (String strValue : strValues) {
            values.add(Op.toDouble(strValue));
        }

        if (values.size() % 2 == 0) {
            setHeight(1.0);
        } else {
            setHeight(values.removeLast());
        }
        setXY(toPairs(values));
    }

    /**
     Creates a Discrete term from a variadic set of values.

     @param name is the name of the resulting term
     @param xy are the values @f$x_0, y_0, ..., x_i, y_i, ..., x_n, y_n@f$
     @return a new Discrete term with the given parameters
     */
    public static Discrete create(String name, double... xy) {
        final int mod2 = xy.length % 2;
        List<Pair> xyValues = new ArrayList<Pair>(xy.length / 2);
        for (int i = 0; i < xy.length - mod2; i += 2) {
            xyValues.add(new Pair(xy[i], xy[i + 1]));
        }
        Discrete result = new Discrete(name, xyValues);
        if (mod2 != 0) {
            result.setHeight(xy[xy.length - 1]);
        }
        return new Discrete(name, xyValues);
    }

    /**
     Discretizes the given term at the default integral resolution ensuring that
     @f$\mu(x)\in[0.0,1.0]@f$.

     @param term is the term to discretize
     @param start is the value from which discretization starts
     @param end is the value at which discretization ends
     @return a Discrete term that approximates the given term
     */
    public static Discrete discretize(Term term, double start, double end) {
        return discretize(term, start, end, IntegralDefuzzifier.getDefaultResolution());
    }

    /**
     Discretizes the given term at the given resolution ensuring that
     @f$\mu(x)\in[0.0,1.0]@f$.


     @param term is the term to discretize
     @param start is the value from which discretization starts
     @param end is the value at which discretization ends
     @param resolution is the number of equally-distributed samples to perform
     between start and end
     @return a Discrete term that approximates the given term
     */
    public static Discrete discretize(Term term, double start, double end, int resolution) {
        return discretize(term, start, end, resolution, true);
    }

    /**
     Discretizes the given term

     @param term is the term to discretize
     @param start is the value from which discretization starts
     @param end is the value at which discretization ends
     @param resolution is the number of equally-distributed samples to perform
     between start and end
     @param boundedMembershipFunction indicates whether to ensure that
     @f$\mu(x)\in[0.0,1.0]@f$
     @return a Discrete term that approximates the given term
     */
    public static Discrete discretize(Term term, double start, double end, int resolution,
            boolean boundedMembershipFunction) {
        Discrete result = new Discrete(term.getName());
        double dx = (end - start) / resolution;
        double x, y;
        for (int i = 0; i <= resolution; ++i) {
            x = start + i * dx;
            y = term.membership(x);
            if (boundedMembershipFunction) {
                y = Op.bound(y, 0.0, 1.0);
            }
            result.add(new Discrete.Pair(x, y));
        }
        return result;
    }

    /**
     The Ascendantly class is a comparator to sort collections of discrete pairs
     ascendantly by the x-coordinate
     */
    public static class Ascendantly implements Comparator<Discrete.Pair> {

        @Override
        public int compare(Pair o1, Pair o2) {
            if (o1.x < o2.x) {
                return -1;
            }
            if (o1.x > o2.x) {
                return 1;
            }
            return 0;
        }
    }

    public static final Ascendantly ASCENDANTLY = new Ascendantly();

    /**
     Ascendantly sorts the pairs of values in this Discrete term by the

     @f$x@f$-coordinate
     */
    public void sort() {
        Collections.sort(this, ASCENDANTLY);
    }

    /**
     Ascendantly sorts the given pairs of values by the @f$x@f$-value, as it is
     required by the Discrete term.

     @param pairs is a vector of pairs of values in the form @f$(x,y)@f$
     */
    public static void sort(List<Discrete.Pair> pairs) {
        Collections.sort(pairs, ASCENDANTLY);
    }

    /**
     Computes the membership function evaluated at @f$x@f$ by using binary
     search to find the lower and upper bounds of @f$x@f$ and then linearly
     interpolating the membership function between the bounds.

     @param x
     @return @f$ \dfrac{h (y_{\max} - y_{\min})}{(x_{\max}- x_{\min})} (x -
     x_{\min}) + y_{\min}@f$

     where @f$h@f$ is the height of the Term,
     @f$x_{\min}@f$ and @f$x_{\max}@f$is are the lower and upper limits of
     @f$x@f$ in `xy` (respectively),
     @f$y_{\min}@f$ and @f$y_{\max}@f$is are the membership functions of
     @f$\mu(x_{\min})@f$ and @f$\mu(x_{\max})@f$ (respectively)
     */
    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        if (xy.isEmpty()) {
            throw new RuntimeException("[discrete error] term is empty");
        }

        //                ______________________
        //               /                      \
        //              /                        \
        // ____________/                          \____________
        //            x[0]                      x[n-1]
        //
        Pair first = xy.get(0);
        Pair last = xy.get(xy.size() - 1);
        if (Op.isLE(x, first.getX())) {
            return height * first.getY();
        }
        if (Op.isGE(x, last.getX())) {
            return height * last.getY();
        }
        Discrete.Pair value = new Discrete.Pair(x, Double.NaN);
        //Binary search will find a number greater than or equal to x
        int upper = Collections.binarySearch(xy, value, ASCENDANTLY);
        //if the upper bound is equal to x
        if (upper >= 0) {
            return height * xy.get(upper).y;
        }
        //if the upper bound is not x, then binary search returns (-insertionPoint - 1)
        upper = Math.abs(upper + 1);
        //and the lower bound
        int lower = upper - 1;

        //FuzzyLite.logger().log(Level.INFO, "x={0}\t[{1} , {2}]", new Object[]{x, lower, upper});
        return height * Op.scale(x, xy.get(lower).getX(), xy.get(upper).getX(),
                xy.get(lower).getY(), xy.get(upper).getY());
    }

    /**
     Gets the list of pairs defining the discrete membership function

     @return the list of pairs defining the discrete membership function
     */
    public List<Pair> getXY() {
        return xy;
    }

    /**
     Sets the list of pairs defining the discrete membership function

     @param pairs is the list of pairs defining the discrete membership function
     */
    public void setXY(List<Pair> pairs) {
        this.xy = pairs;
    }

    /**
     Creates, fills and returns a list containing the @f$x@f$ values

     @return a list containing the @f$x@f$ values
     */
    public List<Double> x() {
        List<Double> result = new ArrayList<Double>(xy.size());
        for (Discrete.Pair pair : xy) {
            result.add(pair.x);
        }
        return result;
    }

    /**
     Gets the @f$x@f$ value at the given index

     @param index is the index
     @return the @f$x@f$ value at the given index
     */
    public double x(int index) {
        return xy.get(index).x;
    }

    /**
     Creates, fills and returns a vector containing the @f$y@f$ values

     @return a vector containing the @f$y@f$ values
     */
    public List<Double> y() {
        List<Double> result = new ArrayList<Double>(xy.size());
        for (Discrete.Pair pair : xy) {
            result.add(pair.y);
        }
        return result;
    }

    /**
     Gets the @f$y@f$ value at the given index

     @param index is the index
     @return the @f$y@f$ value at the given index
     */
    public double y(int index) {
        return xy.get(index).y;
    }

    @Override
    public Discrete clone() throws CloneNotSupportedException {
        Discrete result = (Discrete) super.clone();
        List<Pair> xyClone = new ArrayList<Pair>(this.xy.size());
        for (Pair p : this.xy) {
            xyClone.add(p.clone());
        }
        result.xy = xyClone;
        return result;
    }

    /**
     Creates a list of scalars from a list of Pair given in the form

     @f$\left(\{x_1,y_1\},...,\{x_n,y_n\}\right)@f$

     @param xyValues is the list of Pair
     @return a vector of scalars as @f$(x_1,y_1,...,x_n,y_n)@f$
     */
    public static List<Double> toList(List<Pair> xyValues) {
        List<Double> result = new ArrayList<Double>(xyValues.size() * 2);
        for (Pair pair : xyValues) {
            result.add(pair.getX());
            result.add(pair.getY());
        }
        return result;
    }

    /**
     Creates a list of Pair from a list of scalars given in the form

     @f$(x_1,y_1,...,x_n,y_n)@f$

     @param xyValues is a vector of fl::scalar given as
     @f$(x_1,y_1,...,x_n,y_n)@f$
     @return a list of Pair in the form
     @f$\left(\{x_1,y_1\},...,\{x_n,y_n\}\right)@f$
     @throws RuntimeException if a value is missing, that is, if the length of
     @f$xy@f$ is odd: @f$|xy|\mod 2 = 1@f$
     */
    public static List<Pair> toPairs(List<Double> xyValues) {
        if (xyValues.size() % 2 != 0) {
            throw new RuntimeException(String.format("[discrete error] "
                    + "missing value in set of pairs (|xy|=%d)", xyValues.size()));
        }
        List<Pair> result = new ArrayList<Pair>((xyValues.size() + 1) / 2);
        Iterator<Double> it = xyValues.iterator();
        while (it.hasNext()) {
            result.add(new Pair(it.next(), it.next()));
        }
        return result;
    }

    /**
     Creates a list of Pair from a list of scalars given in the form

     @f$(x_1,y_1,...,x_n,y_n)@f$

     @param xyValues is a list of scalars given as
     @f$(x_1,y_1,...,x_n,y_n)@f$ possibly missing a value
     @param missingValue is the replacement in the case a value is missing from
     @f$xy@f$
     @return a vector of Pair in the form
     @f$\left(\{x_1,y_1\},...,\{x_n,y_n\}\right)@f$
     */
    public static List<Pair> toPairs(List<Double> xyValues, double missingValue) {
        List<Pair> result = new ArrayList<Pair>((xyValues.size() + 1) / 2);
        Iterator<Double> it = xyValues.iterator();
        while (it.hasNext()) {
            result.add(new Pair(it.next(), it.hasNext() ? it.next() : missingValue));
        }
        return result;
    }

    /**
     Formats a vector of Pair into a string in the form @f$(x_1,y_1) ...
     (x_n,y_n)@f$

     @param xy is the vector of Pair
     @return a formatted string containing the pairs of @f$(x,y)@f$ values
     */
    public static String formatXY(List<Discrete.Pair> xy) {
        return formatXY(xy, "(", ",", ")", " ");
    }

    /**
     Formats a vector of Pair into a string in the form

     @f$(x_1,y_1) ... (x_n,y_n)@f$
     @param xy is the vector of Pair
     @param prefix indicates the prefix of a Pair, e.g., `(` results in
     @f$(x_i@f$
     @param innerSeparator indicates the separator between
     @f$x@f$ and @f$y@f$, e.g., `,` results in @f$x_i,y_i@f$
     @param suffix indicates the postfix of a Pair, e.g., `]` results in
     @f$y_i]@f$
     @param outerSeparator indicates the separator between Pair, e.g., `;`
     results in @f$(x_i,y_i);(x_j,y_j)@f$
     @return a formatted string containing the pairs of @f$(x,y)@f$ values
     */
    public static String formatXY(List<Discrete.Pair> xy,
            String prefix, String innerSeparator,
            String suffix, String outerSeparator) {
        StringBuilder result = new StringBuilder();
        Iterator<Pair> it = xy.iterator();
        while (it.hasNext()) {
            Pair pair = it.next();
            result.append(prefix).append(Op.str(pair.getX()))
                    .append(innerSeparator).append(Op.str(pair.getY()))
                    .append(suffix);
            if (it.hasNext()) {
                result.append(outerSeparator);
            }
        }
        return result.toString();
    }

    @Override
    public int size() {
        return this.xy.size();
    }

    @Override
    public boolean isEmpty() {
        return this.xy.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.xy.contains(o);
    }

    @Override
    public Iterator<Pair> iterator() {
        return this.xy.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.xy.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.xy.toArray(a);
    }

    @Override
    public boolean add(Pair e) {
        return this.xy.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.xy.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.xy.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Pair> c) {
        return this.xy.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Pair> c) {
        return this.xy.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.xy.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.xy.removeAll(c);
    }

    @Override
    public void clear() {
        this.xy.clear();
    }

    @Override
    public Pair get(int index) {
        return this.xy.get(index);
    }

    @Override
    public Pair set(int index, Pair element) {
        return this.xy.set(index, element);
    }

    @Override
    public void add(int index, Pair element) {
        this.xy.add(index, element);
    }

    @Override
    public Pair remove(int index) {
        return this.xy.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.xy.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.xy.lastIndexOf(o);
    }

    @Override
    public ListIterator<Pair> listIterator() {
        return this.xy.listIterator();
    }

    @Override
    public ListIterator<Pair> listIterator(int index) {
        return this.xy.listIterator(index);
    }

    @Override
    public List<Pair> subList(int fromIndex, int toIndex) {
        return this.xy.subList(fromIndex, toIndex);
    }

}
