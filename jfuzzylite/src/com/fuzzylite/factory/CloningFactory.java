package com.fuzzylite.factory;

import com.fuzzylite.misc.PubliclyCloneable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jcrada
 */
public class CloningFactory<T extends PubliclyCloneable> implements PubliclyCloneable {

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
