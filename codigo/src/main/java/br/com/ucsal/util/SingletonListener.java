package br.com.ucsal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import br.com.ucsal.annotation.Singleton;

public class SingletonListener {

    private static final Map<Class<?>, Object> instances = new HashMap<>();

    static {
        Reflections reflections = new Reflections("br.com.ucsal");
        Set<Class<?>> singletonClasses = reflections.getTypesAnnotatedWith(Singleton.class);

        for (Class<?> clazz : singletonClasses) {
            try {
            	if(!instances.containsKey(clazz)) {
            		var constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Object instance = constructor.newInstance();
                    instances.put(clazz, instance);
            	}
            } catch (Exception e) {
                throw new RuntimeException("Erro ao inicializar singleton para a classe: " + clazz.getName(), e);
            }
        }
    }

    private SingletonListener() {}

    @SuppressWarnings("unchecked")
    public static <T> T getSingleton(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Singleton.class)) {
            throw new IllegalArgumentException("A classe " + clazz.getName() + " é um @Singleton");
        }

        return (T) instances.get(clazz);
    }
}
