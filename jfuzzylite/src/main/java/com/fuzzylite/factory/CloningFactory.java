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
 The CloningFactory class is the base class for a factory whose objects are
 created from a registered object by calling the `clone()` method.

 @param <T> is the class of the object to be cloned

 @author Juan Rada-Vilela, Ph.D.
 @see FactoryManager
 @since 5.0
 */
public class CloningFactory<T extends Op.Cloneable> implements Op.Cloneable {

    private Map<String, T> objects;

    public CloningFactory() {
        this.objects = new HashMap<String, T>();
    }

    /**
     Registers the object in the factory and assumes its ownership

     @param key is the unique name by which objects are registered
     @param object is the object to be cloned via a `clone` method
     */
    public void registerObject(String key, T object) {
        this.objects.put(key, object);
    }

    /**
     Removes from the factory the given object and deletes it

     @param key is the unique name by which objects are registered
     */
    public void deregisterObject(String key) {
        if (this.objects.containsKey(key)) {
            this.objects.remove(key);
        }
    }

    /**
     Checks whether the factory has the given object registered

     @param key is the unique name by which objects are registered
     @return whether the factory has the given object registered
     */
    public boolean hasObject(String key) {
        return this.objects.containsKey(key);
    }

    /**
     Gets the object registered by the given key, not a clone of the object

     @param key is the unique name by which objects are registered
     @return the object registered by the given key
     */
    public T getObject(String key) {
        if (this.objects.containsKey(key)) {
            return this.objects.get(key);
        }
        return null;
    }

    /**
     Creates a cloned object by executing the clone method on the registered
     object

     @param key is the unique name by which objects are registered
     @return a cloned object by executing the clone method on the registered
     object
     */
    @SuppressWarnings("unchecked")
    public T cloneObject(String key) {
        if (this.objects.containsKey(key)) {
            T object = this.objects.get(key);
            if (object != null) {
                try {
                    return (T) object.clone();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            return null;
        }
        throw new RuntimeException("[cloning error] object by name <" + key
                + "> not registered in " + getClass().getSimpleName());
    }

    /**
     Returns a set of the available objects

     @return a set of the available objects
     */
    public Set<String> available() {
        return new HashSet<String>(this.objects.keySet());
    }

    /**
     Gets the map of registered keys and objects

     @return the map of registered keys and objects
     */
    public Map<String, T> getObjects() {
        return objects;
    }

    /**
     Sets the map of registered keys and objects

     @param objects is the map of registered keys and objects
     */
    public void setObjects(Map<String, T> objects) {
        this.objects = objects;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CloningFactory<T> clone() throws CloneNotSupportedException {
        CloningFactory<T> result = (CloningFactory<T>) super.clone();
        result.objects = new HashMap<String, T>(this.objects);
        return result;
    }

}
