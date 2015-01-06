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
package com.fuzzylite.factory;

import com.fuzzylite.lang.Cloneable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloningFactory<T extends Cloneable> implements Cloneable {

    private String name;
    private Map<String, T> objects;

    public CloningFactory() {
        this("");
    }

    public CloningFactory(String name) {
        this.name = name;
        this.objects = new HashMap<String, T>();
    }

    public void registerObject(String key, T object) {
        this.objects.put(key, object);
    }

    public void deregisterObject(String key) {
        if (this.objects.containsKey(key)) {
            this.objects.remove(key);
        }
    }

    public boolean hasObject(String key) {
        return this.objects.containsKey(key);
    }

    public T getObject(String key) {
        if (this.objects.containsKey(key)) {
            return this.objects.get(key);
        }
        return null;
    }

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
        throw new RuntimeException("[cloning error] " + this.name
                + " object by name <" + key + "> not registered");
    }

    public List<String> available() {
        List<String> result = new ArrayList<String>();
        for (String key : this.objects.keySet()) {
            result.add(key);
        }
        return result;
    }

    @Override
    public CloningFactory<T> clone() throws CloneNotSupportedException {
        return (CloningFactory<T>) super.clone();
    }

}
