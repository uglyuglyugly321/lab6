# Лабораторная работа по Java №6 — Аннотации и рефлексия

**Автор**

*Студент: Шурманов Демид Андреевич*

*Группа: ИТ-9*

---

# В рамках лабораторной работы были реализованы:

- разработка собственных аннотаций с различными целевыми элементами
- использование аннотаций с разными видами свойств (обязательные, необязательные)
- обработка аннотаций через Reflection API в рантайме
- написание обработчиков (Handler) для каждой аннотации
- JUnit тесты для проверки корректности аннотаций

Все задания реализованы **в одном проекте**, но **в разных пакетах**, по одному на задание.

Работа программы демонстрируется через **интерактивное меню** в классе `Main`.

---

# Структура проекта

```
src
└ shurmanov
  ├ invoke/
  │ ├ Invoke.java            (Аннотация для вызова методов)
  │ ├ InvokeHandler.java     (Обработчик для @Invoke)
  │ └ MyClass.java           (Класс с помеченными методами)
  │
  ├ defaultann/
  │ ├ Default.java           (Аннотация значения по умолчанию)
  │ ├ DefaultHandler.java    (Обработчик для @Default)
  │ └ MyDefaultClass.java    (Класс с @Default)
  │
  ├ tostring/
  │ ├ ToString.java          (Аннотация для toString)
  │ ├ Mode.java              (Enum YES/NO)
  │ ├ ToStringHandler.java   (Обработчик для @ToString)
  │ └ Person.java            (Класс с @ToString)
  │
  ├ validate/
  │ ├ Validate.java          (Аннотация для валидации)
  │ ├ ValidateHandler.java   (Обработчик для @Validate)
  │ └ MyValidatedClass.java  (Класс с @Validate)
  │
  ├ two/
  │ ├ Two.java               (Аннотация с двумя свойствами)
  │ ├ TwoHandler.java        (Обработчик для @Two)
  │ └ ExampleClass.java      (Класс с @Two)
  │
  ├ cache/
  │ ├ Cache.java             (Аннотация для кэша)
  │ ├ CacheHandler.java      (Обработчик для @Cache)
  │ └ CacheClass.java        (Класс с @Cache)
  │
  ├ test/
  │ └ DefaultTest.java       (JUnit тесты для @Default)
  │
  └ main/
    └ Main.java              (Главный класс программы)
```

### Описание пакетов

| Пакет | Описание |
|-------|---------|
| invoke | Аннотация для автоматического вызова методов |
| defaultann | Аннотация для указания типа по умолчанию |
| tostring | Аннотация для кастомного строкового представления |
| validate | Аннотация для указания классов для валидации |
| two | Аннотация с двумя обязательными свойствами |
| cache | Аннотация для указания кешируемых областей |
| test | JUnit тесты |
| main | Главный класс программы |

---

# Меню программы

При запуске программы пользователю предлагается выбрать задание:

```text
=== Меню ===
1 — Задание 1 (Тест @Invoke)
2 — Задание 2 (Тест @Default)
3 — Задание 3 (Тест @ToString)
4 — Задание 4 (Тест @Validate)
5 — Задание 5 (Тест @Two)
6 — Задание 6 (Тест @Cache)

0 — Выход
```

*Каждый пункт меню запускает тест соответствующего задания.*

---


# Задание 1 — Аннотация @Invoke

## Условие

Разработайте аннотацию **@Invoke** со следующими характеристиками:

- Целью может быть только **МЕТОД**
- Доступна во время исполнения программы
- Не имеет свойств

Создайте класс, содержащий несколько методов, и проаннотируйте хотя бы один из них аннотацией **@Invoke**. Реализуйте обработчик (через Reflection API), который находит методы, отмеченные аннотацией **@Invoke**, и вызывает их автоматически.

---

## Алгоритм решения

1. Создается аннотация `@Invoke` с `@Target(ElementType.METHOD)` и `@Retention(RetentionPolicy.RUNTIME)`
2. Создается класс `MyClass` с несколькими методами, часть из них помечена `@Invoke`
3. Создается обработчик `InvokeHandler`, который:
   - Получает `Class<?>` объекта через `obj.getClass()`
   - Проходит по всем методам через `clazz.getDeclaredMethods()`
   - Проверяет наличие аннотации через `method.isAnnotationPresent(Invoke.class)`
   - Вызывает найденные методы через `method.invoke(obj)`
   - Обрабатывает исключения

---

## Код

### Invoke.java

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Invoke {
}
```

### InvokeHandler.java

```java
public class InvokeHandler {

    public static void runInvokedMethods(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Invoke.class)) {
                try {
                    method.invoke(obj);
                } catch (Exception e) {
                    System.out.println("Ошибка при вызове метода " + method.getName() + 
                                     ": " + e.getMessage());
                }
            }
        }
    }

    public static void test() {
        MyClass myObj = new MyClass();
        runInvokedMethods(myObj);
    }
}
```

### MyClass.java

```java
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
}
```

### Ключевые особенности:
```
 **Целевой элемент** — @Target(ElementType.METHOD) ограничивает использование только на методы
 **Рефлексия** — используется getDeclaredMethods() для получения всех методов
 **Обработка исключений** — правильная обработка InvocationTargetException и IllegalAccessException
 **Безусловный вызов** — все методы с @Invoke вызываются без аргументов
```

---

# Задание 2 — Аннотация @Default

## Условие

Разработайте аннотацию **@Default** со следующими характеристиками:

- Целью может быть **ТИП или ПОЛЕ**
- Доступна во время исполнения программы
- Имеет **обязательное свойство** `value` типа `Class`

Проаннотируйте какой-либо класс данной аннотацией, указав тип по умолчанию. Напишите обработчик, который выводит имя указанного класса по умолчанию.

---

## Алгоритм решения

1. Создается аннотация `@Default` с `@Target({ElementType.TYPE, ElementType.FIELD})` и обязательным свойством `value` типа `Class<?>`
2. Класс `MyDefaultClass` аннотируется с `@Default(value = String.class)`, поля — с разными типами
3. Обработчик `DefaultHandler` проверяет наличие аннотации на классе и полях
4. Для каждой найденной аннотации читается свойство `value()` и выводится `annotation.value().getName()`

---

## Код

### Default.java

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
    Class<?> value();  // обязательное свойство
}
```

### DefaultHandler.java

```java
public class DefaultHandler {

    public static void printClassDefault(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Default.class)) {
            Default annotation = clazz.getAnnotation(Default.class);
            System.out.println("Класс по умолчанию: " + annotation.value().getName());
        }
    }

    public static void printFieldDefaults(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Default.class)) {
                Default annotation = field.getAnnotation(Default.class);
                System.out.println("Поле " + field.getName() + " по умолчанию: " + 
                                 annotation.value().getName());
            }
        }
    }

    public static void test() {
        printClassDefault(MyDefaultClass.class);
        printFieldDefaults(MyDefaultClass.class);
    }
}
```

### MyDefaultClass.java

```java
@Default(value = String.class)
public class MyDefaultClass {

    @Default(value = Integer.class)
    private int number;

    public MyDefaultClass() {
        this.number = 42;
    }
}
```

### Ключевые особенности:
```
 **Обязательное свойство** — свойство value не имеет значения по умолчанию
 **Множественные цели** — аннотация работает на уровне класса и поля
 **Получение значения** — через getAnnotation() и вызов метода value()
 **Отражение типов** — использование Class.getName() для получения имени класса
```

---

# Задание 3 — Аннотация @ToString

## Условие

Разработайте аннотацию **@ToString** со следующими характеристиками:

- Целью может быть **ТИП или ПОЛЕ**
- Доступна во время исполнения программы
- Имеет необязательное свойство `value` с двумя вариантами значений: `YES` или `NO`
- Значение свойства по умолчанию: `YES`

Проаннотируйте класс аннотацией **@ToString**, а одно из полей — с `@ToString(Mode.NO)`. Создайте метод, который формирует строковое представление объекта, учитывая только те поля, где **@ToString** имеет значение `YES`.

---

## Алгоритм решения

1. Создается enum `Mode` с значениями `YES` и `NO`
2. Создается аннотация `@ToString` с необязательным свойством `value()` типа `Mode` со значением по умолчанию `Mode.YES`
3. Класс `Person` аннотируется `@ToString`, одно из полей помечено `@ToString(Mode.NO)`
4. Обработчик `ToStringHandler` проходит по полям, проверяет значение аннотации и включает в строку только поля с `YES`

---

## Код

### Mode.java

```java
public enum Mode {
    YES,
    NO
}
```

### ToString.java

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToString {
    Mode value() default Mode.YES;  // необязательное свойство с значением по умолчанию
}
```

### ToStringHandler.java

```java
public class ToStringHandler {

    public static String buildString(Object obj) {
        Class<?> clazz = obj.getClass();

        if (!clazz.isAnnotationPresent(ToString.class)) {
            return obj.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append(" [");

        Field[] fields = clazz.getDeclaredFields();
        boolean first = true;

        for (Field field : fields) {
            Mode mode = Mode.YES;

            if (field.isAnnotationPresent(ToString.class)) {
                ToString annotation = field.getAnnotation(ToString.class);
                mode = annotation.value();
            }

            if (mode == Mode.YES) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (!first) sb.append(", ");
                    sb.append(field.getName()).append("=").append(value);
                    first = false;
                } catch (IllegalAccessException e) {
                    // логирование или обработка
                }
            }
        }

        sb.append("]");
        return sb.toString();
    }

    public static void test() {
        Person p = new Person("Вася", 30, "Москва");
        String result = buildString(p);
        System.out.println(result);
    }
}
```

### Person.java

```java
@ToString  // класс аннотирован
public class Person {

    private String name;

    @ToString(Mode.NO)  // это поле не участвует в toString
    private int age;

    private String city;

    public Person(String name, int age, String city) {
        this.name = name;
        this.age = age;
        this.city = city;
    }
}
```

### Ключевые особенности:
```
 **Значение по умолчанию** — поля наследуют Mode.YES, если не указано иное
 **Условная сборка строки** — только поля с Mode.YES включаются в результат
 **setAccessible()** — нужен для доступа к приватным полям
 **StringBuilder** — эффективное формирование строки
```

---

# Задание 4 — Аннотация @Validate

## Условие

Разработайте аннотацию **@Validate** со следующими характеристиками:

- Целью может быть **ТИП или АННОТАЦИЯ**
- Доступна во время исполнения программы
- Имеет **обязательное свойство** `value` типа `Class[]`

Проаннотируйте класс аннотацией **@Validate**, передав список типов для проверки. Реализуйте обработчик, который выводит, какие классы указаны в аннотации.

---

## Алгоритм решения

1. Создается аннотация `@Validate` с `@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})`
2. Обязательное свойство `value` типа `Class<?>[]` (массив классов)
3. Класс `MyValidatedClass` помечается `@Validate({String.class, Integer.class, Double.class})`
4. Обработчик проверяет наличие аннотации и выводит все элементы массива

---

## Код

### Validate.java

```java
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    Class<?>[] value();  // обязательное свойство — массив классов
}
```

### ValidateHandler.java

```java
public class ValidateHandler {

    public static void printValidatedClasses(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Validate.class)) {
            Validate annotation = clazz.getAnnotation(Validate.class);
            System.out.println("Классы для валидации: " + Arrays.toString(annotation.value()));
        }
    }

    public static void test() {
        printValidatedClasses(MyValidatedClass.class);
    }
}
```

### MyValidatedClass.java

```java
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
```

### Ключевые особенности:
```
 **Массив типов** — свойство value содержит несколько классов для валидации
 **Arrays.toString()** — удобный вывод массива классов
 **Гибкость** — можно передать любое количество типов
```

---

# Задание 5 — Аннотация @Two

## Условие

Разработайте аннотацию **@Two** со следующими характеристиками:

- Целью может быть **ТИП**
- Доступна во время исполнения программы
- Имеет два **обязательных свойства**: 
  - `first` типа `String`
  - `second` типа `int`

Проаннотируйте какой-либо класс аннотацией **@Two**, передав строковое и числовое значения. Реализуйте обработчик, который считывает и выводит значения этих свойств.

---

## Алгоритм решения

1. Создается аннотация `@Two` с `@Target(ElementType.TYPE)`
2. Два обязательных свойства: `String first()` и `int second()`
3. Класс `ExampleClass` помечается `@Two(first = "Hello", second = 42)`
4. Обработчик получает аннотацию и выводит оба свойства

---

## Код

### Two.java

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Two {
    String first();   // обязательное свойство
    int second();     // обязательное свойство
}
```

### TwoHandler.java

```java
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
```

### ExampleClass.java

```java
@Two(first = "Hello", second = 42)
public class ExampleClass {
    private String name = "Test";
}
```

### Ключевые особенности:
```
 **Разные типы свойств** — в одной аннотации String и int
 **Обязательные значения** — оба свойства должны быть указаны при применении
 **Простой обработчик** — прямой вызов методов для получения значений
```

---

# Задание 6 — Аннотация @Cache

## Условие

Разработайте аннотацию **@Cache** со следующими характеристиками:

- Целью может быть **ТИП**
- Доступна во время исполнения программы
- Имеет необязательное свойство `value` типа `String[]`
- Значение свойства по умолчанию: пустой массив

Проаннотируйте класс аннотацией **@Cache**, указав несколько кешируемых областей. Создайте обработчик, который выводит список всех кешируемых областей или сообщение, что список пуст.

---

## Алгоритм решения

1. Создается аннотация `@Cache` с `@Target(ElementType.TYPE)`
2. Необязательное свойство `value` типа `String[]` со значением по умолчанию `{}`
3. Класс `CacheClass` помечается `@Cache({"users", "products", "orders"})`
4. Обработчик получает массив и проверяет его длину, выводит содержимое или уведомление о пустоте

---

## Код

### Cache.java

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String[] value() default {};  // необязательное свойство, пустой массив по умолчанию
}
```

### CacheHandler.java

```java
public class CacheHandler {

    public static void printCacheAreas(Class<?> clazz) {

        if (clazz.isAnnotationPresent(Cache.class)) {

            Cache annotation = clazz.getAnnotation(Cache.class);
            String[] areas = annotation.value();

            if (areas.length == 0) {
                System.out.println("Список кешируемых областей пуст.");
            } else {
                System.out.println("Кешируемые области:");
                for (String area : areas) {
                    System.out.println("- " + area);
                }
            }

        } else {
            System.out.println("Аннотация @Cache отсутствует у класса.");
        }
    }

    public static void test() {
        printCacheAreas(CacheClass.class);
    }
}
```

### CacheClass.java

```java
@Cache({"users", "products", "orders"})
public class CacheClass {

    public void doSomething() {
        System.out.println("Работа класса CacheClass....");
    }
}
```

### Ключевые особенности:
```
 **Массив строк** — хранит названия кешируемых областей
 **Значение по умолчанию** — пустой массив позволяет не указывать свойство
 **Проверка пустоты** — различаются случаи пустого и непустого массива
 **Итерация** — использование for-each для вывода элементов
```

---

# Примеры запуска и ожидаемый вывод

1) **@Invoke**
   ```
   Метод 1 вызван
   Метод 3 вызван
   ```

2) **@Default**
   ```
   Класс по умолчанию: java.lang.String
   Поле number по умолчанию: java.lang.Integer
   ```

3) **@ToString**
   ```
   Person [name=Вася, city=Москва]
   ```
   (Поле `age` не включено, т.к. помечено `@ToString(Mode.NO)`)

4) **@Validate**
   ```
   Классы для валидации: [class java.lang.String, class java.lang.Integer, class java.lang.Double]
   ```

5) **@Two**
   ```
   first = Hello
   second = 42
   ```

6) **@Cache**
   ```
   Кешируемые области:
   - users
   - products
   - orders
   ```

---

# Тестирование (JUnit)

Для задания @Default реализованы JUnit тесты:

```java
class DefaultAnnotationTest {

    @Test
    void testDefaultValueOnClass() {
        Default annotation = ClassWithStringDefault.class.getAnnotation(Default.class);
        assertNotNull(annotation);
        assertEquals(String.class, annotation.value());
    }

    @Test
    void testDefaultAnnotationOnField() throws NoSuchFieldException {
        Field field = ClassWithFieldDefault.class.getDeclaredField("number");
        Default annotation = field.getAnnotation(Default.class);
        assertNotNull(annotation);
        assertEquals(Double.class, annotation.value());
    }

    @ParameterizedTest
    @MethodSource("classProvider")
    void parameterizedTestForDefaultAnnotation(Class<?> clazz, Class<?> expectedType) {
        Default annotation = clazz.getAnnotation(Default.class);
        assertNotNull(annotation);
        assertEquals(expectedType, annotation.value());
    }
}
```



---

