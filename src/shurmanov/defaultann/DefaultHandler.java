package shurmanov.defaultann;

import java.lang.reflect.Field;

public class DefaultHandler {

    public static void printClassDefault(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Default.class)) {
            Default annotation = clazz.getAnnotation(Default.class);
            System.out.println("Класс по умолчанию: " + annotation.value().getName());
        } else {
            System.out.println("Аннотация @Default отсутствует у класса " + clazz.getName());
        }
    }


    public static void printFieldDefaults(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Default.class)) {
                Default annotation = field.getAnnotation(Default.class);
                System.out.println("Поле " + field.getName() + " по умолчанию: " + annotation.value().getName());
            }
        }
    }

    public static void test() {
        printClassDefault(MyDefaultClass.class);
        printFieldDefaults(MyDefaultClass.class);
    }
}