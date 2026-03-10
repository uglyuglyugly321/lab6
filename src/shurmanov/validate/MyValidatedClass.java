package shurmanov.validate;

@Validate({String.class, Integer.class, Double.class})
public class MyValidatedClass {
    private String name;
    private int age;
    private double salary;

    public MyValidatedClass() {
        this.name = "Вася";
        this.age = 30;
        this.salary = 1000.5;
    }
}
