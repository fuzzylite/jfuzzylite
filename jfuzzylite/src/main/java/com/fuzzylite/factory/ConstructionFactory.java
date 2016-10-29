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
package com.fuzzylite.factory;

import com.fuzzylite.Op;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 The ConstructionFactory class is the base class for a factory whose objects are
 created by instantiating a class with no arguments.

 @author Juan Rada-Vilela, Ph.D.
 @see FactoryManager
 @since 5.0
 */
public class ConstructionFactory<T> implements Op.Cloneable {

    private Map<String, Class<? extends T>> constructors;

    public ConstructionFactory() {
        this.constructors = new HashMap<String, Class<? extends T>>();
    }

    /**
     Registers the class in the factory utilizing the `Class.getSimpleName()` as
     key

     @param clazz is the class of the object to construct
     */
    public void register(Class<? extends T> clazz) {
        this.register(clazz.getSimpleName(), clazz);
    }

    /**
     Registers the class in the factory utilizing the given key

     @param simpleName is the simple name of the class by which constructors are
     (generally) registered
     @param clazz is the class of the object to construct
     */
    public void register(String simpleName, Class<? extends T> clazz) {
        this.constructors.put(simpleName, clazz);
    }

    /**
     Removes from the factory the class associated to the given key

     @param simpleName is the key by which constructors are registered
     */
    public void deregister(String simpleName) {
        this.constructors.remove(simpleName);
    }

    /**
     Checks whether the factory has the given constructor registered

     @param simpleName is the unique name by which constructors are registered
     @return whether the factory has the given constructor registered
     */
    public boolean hasConstructor(String simpleName) {
        return this.constructors.containsKey(simpleName);
    }

    /**
     Gets the class registered by the given key

     @param simpleName is the key of the registered class
     @return the class registered under the given key, or `null` if the key is
     not registered
     */
    public Class<? extends T> getConstructor(String simpleName) {
        return this.constructors.get(simpleName);
    }

    /**
     Returns a set of keys for the constructors available

     @return a set of the constructors available
     */
    public Set<String> available() {
        return new HashSet<String>(this.constructors.keySet());
    }

    /**
     Creates an object by instantiating the registered class associated to the
     given key

     @param simpleName is the key of the class to instantiate
     @return an object by instantiating the registered class associated to the
     given key.
     @throws RuntimeException if the given key is not registered
     */
    public T constructObject(String simpleName) {
        if (simpleName == null) {
            return null;
        }

        if (this.constructors.containsKey(simpleName)) {
            try {
                Class<? extends T> clazz = this.constructors.get(simpleName);
                if (clazz != null) {
                    return clazz.newInstance();
                }
                return null;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        throw new RuntimeException("[construction error] constructor <" + simpleName
                + "> not registered in " + getClass().getSimpleName());
    }

    /**
     Gets the map of registered keys and constructors

     @return the map of registered keys and constructors
     */
    public Map<String, Class<? extends T>> getConstructors() {
        return constructors;
    }

    /**
     Sets the map of registered keys and constructors

     @param constructors is the map of registered keys and constructors
     */
    public void setConstructors(Map<String, Class<? extends T>> constructors) {
        this.constructors = constructors;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConstructionFactory<T> clone() throws CloneNotSupportedException {
        ConstructionFactory<T> result = (ConstructionFactory<T>) super.clone();
        result.constructors = new HashMap<String, Class<? extends T>>(this.constructors);
        return result;
    }

}
