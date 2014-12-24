package com.fuzzylite.misc;

/**
 *
 * @author jcrada
 */
public class Pair<Y, Z> {

    public Y first;
    public Z second;

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

}
