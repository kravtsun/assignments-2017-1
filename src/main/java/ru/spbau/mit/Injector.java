package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public final class Injector {
    private static Map<String, Object> alreadyCreatedObjects = new HashMap<>();
    private static Set<String> enqueued = new HashSet<>();

    private Injector() {
    }

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        try {
            return initialize0(rootClassName, implementationClassNames);
        } finally {
            deinitializeStructures();
        }
    }

    private static void deinitializeStructures() {
        alreadyCreatedObjects.clear();
        enqueued.clear();
    }

    private static Object initialize0(String rootClassName, List<String> implementationClassNames)
            throws ImplementationNotFoundException,
            ClassNotFoundException,
            AmbiguousImplementationException,
            InjectionCycleException {
        if (alreadyCreatedObjects.containsKey(rootClassName)) {
            return alreadyCreatedObjects.get(rootClassName);
        }
        if (enqueued.contains(rootClassName)) {
            return new InjectionCycleException();
        }
        enqueued.add(rootClassName);

        Class rootClass = Class.forName(rootClassName);
        Constructor rootConstructor = rootClass.getConstructors()[0];
        List<Object> args = new ArrayList<>();
        for (Class<?> dependencyClass : rootConstructor.getParameterTypes()) {
            Predicate<String> predicate = (implName) -> {
                try {
                    Class implementationClass = Class.forName(implName);
                    return dependencyClass.isAssignableFrom(implementationClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException();
                }
            };
            List<String> allowedClasses = implementationClassNames
                    .stream()
                    .filter(predicate)
                    .collect(Collectors.toList());

            if (allowedClasses.size() == 1) {
                String allowedClass = allowedClasses.get(0);
                Object arg = initialize0(allowedClass, implementationClassNames);
                args.add(arg);
            } else if (allowedClasses.size() > 1) {
                throw new AmbiguousImplementationException();
            } else {
                if (enqueued.stream().anyMatch(predicate)) {
                    throw new InjectionCycleException();
                } else {
                    throw new ImplementationNotFoundException();
                }
            }
        }

        Object rootObject;
        try {
            rootObject = rootConstructor.newInstance(args.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException();
        }
        enqueued.remove(rootClassName);
        alreadyCreatedObjects.put(rootClassName, rootObject);
        return rootObject;
    }
}
