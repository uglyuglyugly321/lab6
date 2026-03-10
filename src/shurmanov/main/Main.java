package shurmanov.main;

import shurmanov.cache.CacheHandler;
import shurmanov.defaultann.DefaultHandler;
import shurmanov.invoke.InvokeHandler;
import shurmanov.tostring.ToStringHandler;
import shurmanov.two.TwoHandler;
import shurmanov.validate.MyValidatedClass;
import shurmanov.validate.ValidateHandler;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("=== Меню ===");
            System.out.println("1 — Задание 1 (Тест @Invoke)");
            System.out.println("2 — Задание 2 (Тест @Default)");
            System.out.println("3 — Задание 3 (Тест @ToString)");
            System.out.println("4 — Задание 4 (Тест @Validate)");
            System.out.println("5 — Задание 5 (Тест @Two)");
            System.out.println("6 — Задание 6 (Тест @Cache)");

            System.out.println("0 — Выход");
            System.out.print("Выберите пункт: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> InvokeHandler.test();
                case 2 -> DefaultHandler.test();
                case 3 -> ToStringHandler.test();
                case 4 -> ValidateHandler.test();
                case 5 -> TwoHandler.test();
                case 6 -> CacheHandler.test();
                case 0 -> System.out.println("Выход...");
                default -> System.out.println("Неверный ввод!");
            }

        } while (choice != 0);
    }
}