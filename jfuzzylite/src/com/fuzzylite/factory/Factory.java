/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jcrada
 */
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
    
    public Set<String> available(){
        return this.map.keySet();
    }

    public T createInstance(String simpleName) {
        if (simpleName == null || simpleName.isEmpty()) return null;
        try {
            return this.map.get(simpleName).newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
