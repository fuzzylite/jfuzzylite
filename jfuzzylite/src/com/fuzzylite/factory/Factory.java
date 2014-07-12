/*
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
 */
package com.fuzzylite.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Factory<T> {

    protected Map<String, Class<? extends T>> map;

    public Factory() {
        this.map = new HashMap<>();
    }

    public void register(Class<? extends T> clazz) {
        this.register(clazz.getSimpleName(), clazz);
    }

    public void register(String simpleName, Class<? extends T> clazz) {
        this.map.put(simpleName, clazz);
    }

    public void deregister(String simpleName) {
        this.map.remove(simpleName);
    }

    public boolean isRegistered(String simpleName) {
        return this.map.containsKey(simpleName);
    }

    public Set<String> available() {
        return this.map.keySet();
    }

    public T createInstance(String simpleName) {
        if (simpleName == null || simpleName.isEmpty()) {
            return null;
        }
        try {
            return this.map.get(simpleName).newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
