package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Injector {

    private static ArrayList<String> mClassList;

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        mClassList = new ArrayList<>();
        return mInitialize(rootClassName, implementationClassNames);
    }

    private static Object mInitialize(String rootClassName,
                                       List<String> implementationClassNames)
            throws Exception {
        Class<?> rootClass = Class.forName(rootClassName);
        Constructor<?> rootConstructor = rootClass.getConstructors()[0];
        Class<?> parameterTypes[] = rootConstructor.getParameterTypes();
        String matchedParameterNames[] = new String[parameterTypes.length];

        if (mClassList.contains(rootClassName)) {
            throw new InjectionCycleException();
        }
        mClassList.add(rootClassName);

        for (String className : implementationClassNames) {
            Class<?> currentClass = Class.forName(className);
            for (int i = 0; i < parameterTypes.length; i++) {
                if (fits(currentClass, parameterTypes[i])) {
                    if (matchedParameterNames[i] != null) {
                        throw new AmbiguousImplementationException();
                    }
                    matchedParameterNames[i] = className;
                }
            }
        }

        for (String parameterName : matchedParameterNames) {
            if (parameterName == null)
                throw new ImplementationNotFoundException();
        }

        Object params[] = new Object[parameterTypes.length];
        for (int i = 0; i < matchedParameterNames.length; i++) {
            params[i] = mInitialize(matchedParameterNames[i], implementationClassNames);
        }

        mClassList.remove(rootClassName);

        return rootConstructor.newInstance(params);
    }

    private static boolean fits(Class<?> currentClass, Class<?> parameterType) {
        if (parameterType.isInterface() &&
                contains(currentClass.getInterfaces(), parameterType)) {
            return true;
        }

        return parameterType.isAssignableFrom(currentClass);
    }

    private static boolean contains(Class<?> interfaces[], Class<?> parameterType) {
        for (Class<?> clazz : interfaces) {
            if (clazz.equals(parameterType)) {
                return true;
            }
        }
        return false;
    }
}