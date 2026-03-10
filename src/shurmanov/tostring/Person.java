package shurmanov.tostring;

@ToString // класс аннотирован
public class Person {

    private String name;

    @ToString(Mode.NO) // поле не участвует в строковом представлении
    private int age;

    private String city;

    public Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }

    // Геттеры при желании
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCity() { return city; }
}