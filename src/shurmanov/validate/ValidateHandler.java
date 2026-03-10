package shurmanov.validate;

import java.util.Arrays;

public class ValidateHandler {

    public static void printValidatedClasses(Class<?> clazz){
        if (clazz.isAnnotationPresent(Validate.class)) {
            Validate annotation = clazz.getAnnotation(Validate.class);
            System.out.println("Классы для валидации: " + Arrays.toString(annotation.value()));
        } else {
            System.out.println("Аннотация @Validate отсутствует у класса " + clazz.getName());
        }
    }

    public static void test() {
        printValidatedClasses(MyValidatedClass.class);
    }
}
