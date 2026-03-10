package shurmanov.defaultann;

@Default(value = String.class)
public class MyDefaultClass {

    @Default(value = Integer.class)
    private int number;

    public MyDefaultClass() {
        this.number = 42;
    }

}
