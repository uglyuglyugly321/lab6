package shurmanov.invoke;

import java.lang.reflect.Method;

public class InvokeHandler {

    public static void runInvokedMethods(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Invoke.class)) {
                try {
                    method.invoke(obj);
                } catch (Exception e) {
                    System.out.println("Ошибка при вызове метода " + method.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public static void test() {
        MyClass myObj = new MyClass();
        runInvokedMethods(myObj);
    }
}