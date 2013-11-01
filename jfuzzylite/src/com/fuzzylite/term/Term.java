package com.fuzzylite.term;

/**
 *
 * @author jcrada
 */
public abstract class Term implements Cloneable {

    protected String name;

    public Term() {
        this("");
    }

    public Term(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract double membership(double x);

    @Override
    public abstract String toString();
}
