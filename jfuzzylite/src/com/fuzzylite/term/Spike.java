package com.fuzzylite.term;

import com.fuzzylite.Op;
import java.util.List;

/**
 *
 * @author jcrada
 */
public class Spike extends Term {

    protected double center;
    protected double width;

    public Spike() {
        this("");
    }

    public Spike(String name) {
        this(name, Double.NaN, Double.NaN);
    }

    public Spike(String name, double center, double width) {
        this(name, center, width, 1.0);
    }

    public Spike(String name, double center, double width, double height) {
        super(name, height);
        this.center = center;
        this.width = width;
    }

    @Override
    public String parameters() {
        return Op.join(" ", center, width)
                + (!Op.isEq(height, 1.0) ? " " + Op.str(height) : "");
    }

    @Override
    public void configure(String parameters) {
        if (parameters.isEmpty()) {
            return;
        }
        List<String> values = Op.split(parameters, " ");
        int required = 2;
        if (values.size() < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setCenter(Op.toDouble(values.get(0)));
        setWidth(Op.toDouble(values.get(1)));
        if (values.size() > required) {
            setHeight(Op.toDouble(values.get(required)));
        }
    }

    @Override
    public double membership(double x) {
        if (Double.isNaN(x)) {
            return Double.NaN;
        }
        return height * Math.exp(-Math.abs(10.0 / width * (x - center)));
    }

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

}
