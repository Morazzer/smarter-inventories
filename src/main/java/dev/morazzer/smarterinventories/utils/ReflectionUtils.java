package dev.morazzer.smarterinventories.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

public class ReflectionUtils {

    public static Class<?> getCraftClass(String className) {
        if (className.startsWith("org.bukkit.craftbukkit")) {
            return getClass("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className.split("org.bukkit.craftbukkit")[1]);
        }

        return getClass("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getValue(Class<?> clazz, Object object, String fieldName, Class<T> fieldType) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return fieldType.cast(field.get(object));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getValue(Class<?> clazz, Object object, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setValue(Class<?> clazz, Object object, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T invoke(Class<?> clazz, Object object, String methodName, Class<T> returnType, Object... parameters) {
        try {
            Method declaredMethod = clazz.getDeclaredMethod(methodName, Arrays.stream(parameters).map(Object::getClass).toArray(Class<?>[]::new));
            return returnType.cast(declaredMethod.invoke(object, parameters));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invoke(Class<?> clazz, Object object, String methodName, Object... parameters) {
        try {
            Method declaredMethod = clazz.getDeclaredMethod(methodName, Arrays.stream(parameters).map(Object::getClass).toArray(Class<?>[]::new));
            return declaredMethod.invoke(object, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invoke(Class<?> clazz, Object object, String methodName, Class<?>[] classes, Object... parameters) {
        try {
            Method declaredMethod = clazz.getDeclaredMethod(methodName, classes);
            return declaredMethod.invoke(object, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T createInstance(Class<T> clazz, Object... objects) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(Arrays.stream(objects).map(Object::getClass).toArray(Class<?>[]::new));
            return constructor.newInstance(objects);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static <T> T createInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... objects) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            return constructor.newInstance(objects);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
