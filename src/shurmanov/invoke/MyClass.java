package shurmanov.invoke;

public class MyClass {

    @Invoke
    public void method1() {
        System.out.println("Метод 1 вызван");
    }

    public void method2() {
        System.out.println("Метод 2 вызван (без аннотации)");
    }

    @Invoke
    public void method3() {
        System.out.println("Метод 3 вызван");
    }

    public void method4(String name){
        System.out.println("Метод 4 вызван с аргументом: " + name);
    }
}
