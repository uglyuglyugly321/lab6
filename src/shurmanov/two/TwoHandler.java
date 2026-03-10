package shurmanov.two;

public class TwoHandler {

    public static void printAnnotationValues(Class<?> clazz) {

        if (clazz.isAnnotationPresent(Two.class)) {

            Two annotation = clazz.getAnnotation(Two.class);

            String firstValue = annotation.first();
            int secondValue = annotation.second();

            System.out.println("first = " + firstValue);
            System.out.println("second = " + secondValue);

        } else {
            System.out.println("Аннотация @Two отсутствует");
        }
    }

    public static void test() {
        printAnnotationValues(ExampleClass.class);
    }
}