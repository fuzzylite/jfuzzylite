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
package com.fuzzylite.term;

import com.fuzzylite.Op;
import com.fuzzylite.term.Discrete.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Discrete extends Term implements List<Pair> {

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

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

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

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        LinkedList<Double> values = new LinkedList<Double>();

        List<String> strValues = Op.split(parameters, " ");
        Iterator<String> it = strValues.iterator();
        while (it.hasNext()) {
            values.add(Op.toDouble(it.next()));
        }

        if (values.size() % 2 == 0) {
            setHeight(1.0);
        } else {
            setHeight(values.removeLast());
        }
        setXY(toPairs(values));
    }

    public static Discrete create(String name, double... xy) {
        if (xy.length < 2 || xy.length % 2 != 0) {
            throw new RuntimeException(String.format("[discrete term] "
                    + "expected an even number of parameters "
                    + "matching (x,y)+, but passed <%d>", xy.length));
        }
        List<Pair> xyValues = new ArrayList<Pair>(xy.length / 2);
        for (int i = 0; i < xy.length; i += 2) {
            xyValues.add(new Pair(xy[i], xy[i + 1]));
        }
        return new Discrete(name, xyValues);
    }

    @Override
    public double membership(double _x_) {
        if (Double.isNaN(_x_)) {
            return Double.NaN;
        }
        if (xy.isEmpty()) {
            return height * 0.0;
        }

        /*                ______________________
         *               /                      \
         *              /                        \
         * ____________/                          \____________
         *            x[0]                      x[n-1]
         */
        Pair first = xy.get(0);
        Pair last = xy.get(xy.size() - 1);
        if (Op.isLE(_x_, first.getX())) {
            return height * first.getY();
        }
        if (Op.isGE(_x_, last.getX())) {
            return height * last.getY();
        }

        int lower = -1, upper = -1;
        for (int i = 0; i < xy.size(); ++i) {
            if (Op.isEq(xy.get(i).getX(), _x_)) {
                return height * xy.get(i).getY();
            }
            //approximate on the left
            if (Op.isLt(xy.get(i).getX(), _x_)) {
                lower = i;
            }
            if (Op.isGt(xy.get(i).getX(), _x_)) {
                upper = i;
                break;
            }
        }

        if (upper < 0) {
            upper = xy.size() - 1;
        }
        if (lower < 0) {
            lower = 0;
        }
        return height * Op.scale(_x_, xy.get(lower).getX(), xy.get(upper).getX(),
                xy.get(lower).getY(), xy.get(upper).getY());
    }

    public List<Pair> getXY() {
        return xy;
    }

    public void setXY(List<Pair> xy) {
        this.xy = xy;
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

    public static List<Double> toList(List<Pair> xyValues) {
        List<Double> result = new ArrayList<Double>(xyValues.size() * 2);
        Iterator<Pair> it = xyValues.iterator();
        while (it.hasNext()) {
            Pair pair = it.next();
            result.add(pair.getX());
            result.add(pair.getY());
        }
        return result;
    }

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

    public static List<Pair> toPairs(List<Double> xyValues, double missingValue) {
        List<Pair> result = new ArrayList<Pair>((xyValues.size() + 1) / 2);
        Iterator<Double> it = xyValues.iterator();
        while (it.hasNext()) {
            result.add(new Pair(it.next(), it.hasNext() ? it.next() : missingValue));
        }
        return result;
    }

    public static String formatXY(List<Discrete.Pair> xy) {
        return formatXY(xy, "(", ",", ")", " ");
    }

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

    /**
     * List implementation
     */
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
