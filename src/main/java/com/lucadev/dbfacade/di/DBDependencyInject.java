package com.lucadev.dbfacade.di;

import java.util.HashMap;

/**
 * Helper class to manage dependency injection
 * @author Luca Camphuisen < Luca.Camphuisen@hva.nl >
 */
public class DBDependencyInject {

    private static HashMap<String,DBSerializer> classMappings = new HashMap<>();

    public static void register(Class clazz, DBSerializer serializer) {
        classMappings.put(clazz.getName(), serializer);
    }

    public static void unregister(Class clazz) {
        classMappings.remove(clazz.getName());
    }

    public static DBSerializer find(Class clazz) {
        return classMappings.get(clazz);
    }
}
