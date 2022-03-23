package com.engwili.arfe.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public enum ArfeTestContext {
    CONTEXT;

    private final ThreadLocal<Map<String, TreeSet<String>>> testContexts = ThreadLocal.withInitial(HashMap::new);

    public TreeSet<String> get(String name) {
        return testContexts.get().get(name);
    }

    public TreeSet<String> set(String name, TreeSet<String> object) {
        testContexts.get().put(name, object);
        return object;
    }
}
