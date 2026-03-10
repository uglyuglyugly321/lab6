package shurmanov.cache;

@Cache({"users", "products", "orders"})
public class CacheClass {

    public void doSomething() {
        System.out.println("Работа класса CacheClass....");
    }
}
