# Лабораторная работа по Java — Аннотации и рефлексия (lab6)

**Автор**

*Студент: Шурманов Демид Андреевич*  
*Группа: ИТ-9*

---

# Описание проекта

Проект демонстрирует практическое применение пользовательских аннотаций и рефлексии в Java. В нескольких самостоятельных модулях показаны распространённые задачи: автоматический вызов помеченных методов, подстановка значений по умолчанию, создание настраиваемого строкового представления объектов, простая валидация полей и примитивное кэширование результатов. Все демонстрации запускаются через текстовое меню в `shurmanov.main.Main`.

Цель работы — научиться: создавать аннотации (`@interface`), задавать правила их хранения (`@Retention`, `@Target`), и в рантайме находить и обрабатывать помеченные элементы с помощью рефлексии (`Class`, `Method`, `Field`).

---

# Структура проекта

```
src/shurmanov/
  cache/         # Кэширование результатов вызовов
  defaultann/    # Подстановка значений по умолчанию
  invoke/        # Вызов методов, помеченных @Invoke
  tostring/      # Кастомный toString через аннотации
  two/           # Учебный пример (Two)
  validate/      # Валидация полей по аннотациям
  main/          # Меню и демонстрация
  test/          # Тесты (локально)
```

Каждый модуль содержит: аннотацию, примерный класс с пометками (test class) и обработчик (`*Handler`) с логикой на рефлексии.

---

# Меню программы

При запуске `shurmanov.main.Main` показывается меню с пунктами для каждой темы:

```text
=== Меню ===
1 — Invoke (вызов помеченных методов)
2 — Default (значения по умолчанию)
3 — ToString (кастомное строковое представление)
4 — Validate (валидация полей)
5 — Cache (кэширование)
6 — Two (доп. пример)
0 — Выход
```

Выберите пункт — программа создаст демонстрационный объект и вызовет соответствующий Handler.

---

# Как запускать

Собрать и запустить из терминала (macOS / Linux):

```bash
# из корня проекта lab6
javac -d out $(find src -name "*.java")
java -cp out shurmanov.main.Main
```

Или запустите класс `shurmanov.main.Main` из IDE (IntelliJ, Eclipse).

---

# Детали по модулям

Ниже — для каждого модуля: условие задачи, краткий алгоритм решения и важные фрагменты кода (не весь код).

---

## 1) invoke — автоматический вызов методов

Условие
- Найти и выполнить все методы экземпляра, помеченные аннотацией `@Invoke`.

Алгоритм решения
1. Получить `Class<?> clazz = instance.getClass()`.
2. Пройти по `clazz.getDeclaredMethods()`.
3. Для каждого метода проверить `method.isAnnotationPresent(Invoke.class)`.
4. Включить доступ `method.setAccessible(true)` и вызвать `method.invoke(instance, ...)`.
5. Обработать исключения: `InvocationTargetException`, `IllegalAccessException`.

Код (ключевые фрагменты)

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Invoke { }

// в обработчике
for (Method m : clazz.getDeclaredMethods()) {
    if (m.isAnnotationPresent(Invoke.class)) {
        m.setAccessible(true);
        m.invoke(instance);
    }
}
```

Ключевые особенности
- Работает и с приватными методами (через setAccessible).
- При вызове методов учитывайте сигнатуры (аргументы) и возможные исключения из целевого метода.

---

## 2) defaultann — подстановка значений по умолчанию

Условие
- Если поле помечено `@Default("...")` и имеет текущее значение `null`, обработчик должен записать значение из аннотации.

Алгоритм решения
1. Получить `clazz.getDeclaredFields()`.
2. Для каждого поля проверить `isAnnotationPresent(Default.class)`.
3. Преобразовать значение аннотации из строки в тип поля (parse) и установить его, если поле равно `null`.

Код (ключевые фрагменты)

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default { String value() default ""; }

// в DefaultHandler
for (Field f : clazz.getDeclaredFields()) {
    if (f.isAnnotationPresent(Default.class)) {
        f.setAccessible(true);
        Object cur = f.get(instance);
        if (cur == null) {
            Default ann = f.getAnnotation(Default.class);
            Object def = parse(ann.value(), f.getType());
            f.set(instance, def);
        }
    }
}
```

Ключевые особенности
- Нужно различать примитивы и объектные типы при установке значений.
- Если аннотация содержит сложные типы — реализовать парсер значений.

---

## 3) tostring — кастомное строковое представление

Условие
- Сформировать строку для объекта в соответствии с правилами аннотаций (включать/исключать поля, режимы форматирования).

Алгоритм решения
1. Пройти по `clazz.getDeclaredFields()`.
2. Решить, какие поля включать (по аннотации или настройке класса).
3. Получить значения полей и форматировать в единую строку.

Код (ключевые фрагменты)

```java
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToString { Mode mode() default Mode.SHOW_ALL; }

// В ToStringHandler
StringBuilder sb = new StringBuilder();
for (Field f : clazz.getDeclaredFields()) {
    if (shouldInclude(f)) {
        f.setAccessible(true);
        sb.append(f.getName()).append("=").append(String.valueOf(f.get(instance))).append("; ");
    }
}
return sb.toString();
```

Ключевые особенности
- Позволяет централизованно управлять представлением объекта (вместо переопределения toString во множестве классов).

---

## 4) validate — простая валидация полей

Условие
- Проверять поля по правилам: notNull, диапазон (min/max), длина строки и т.д., заданным в `@Validate`.

Алгоритм решения
1. Проверка полей через `getDeclaredFields()`.
2. Для каждого поля читать параметры аннотации (`notNull`, `min`, `max`, `maxLength` и т.п.).
3. Формировать список ошибок и вернуть/вывести его.

Код (ключевые фрагменты)

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    boolean notNull() default false;
    long min() default Long.MIN_VALUE;
    long max() default Long.MAX_VALUE;
}

// В ValidateHandler
List<String> errors = new ArrayList<>();
for (Field f : clazz.getDeclaredFields()) {
    if (f.isAnnotationPresent(Validate.class)) {
        f.setAccessible(true);
        Object val = f.get(instance);
        Validate v = f.getAnnotation(Validate.class);
        if (v.notNull() && val == null) errors.add(f.getName() + " must not be null");
        // check numeric range и т.д.
    }
}
```

Ключевые особенности
- Валидация может возвращать подробные сообщения об ошибках для UI/логов.
- Можно расширять набор правил (регулярные выражения, кастомные валидаторы).

---

## 5) cache — простое кэширование

Условие
- Сохранять результаты метода в Map по ключу (имя метода + аргументы) и при повторных вызовах возвращать сохранённое значение.

Алгоритм решения
1. Создать `Map<String,Object> store = new HashMap<>()`.
2. На每 вызове формировать ключ из `method.getName()` + аргументы.
3. Если `store.containsKey(key)` — вернуть значение, иначе вызвать метод и сохранить результат.

Код (ключевые фрагменты)

```java
String key = method.getName() + Arrays.toString(args);
if (store.containsKey(key)) return store.get(key);
Object result = method.invoke(instance, args);
store.put(key, result);
return result;
```

Ключевые особенности
- Простейший пример: нет учёта времени жизни (TTL) и многопоточности.
- Для реального приложения нужно синхронизация и уборка кэша.

---

## 6) two — учебный пример

Условие
- Демонстрация альтернативной обработки аннотаций — логически похож на другие обработчики, но реализует свою учебную логику (см. `Two`, `TwoHandler`).

Ключевые моменты
- Полезен как шаблон для добавления новых обработчиков.

---

# Примеры запуска и ожидаемый вывод

1) Invoke
- В `MyClass` есть метод с `@Invoke`, который печатает "Invoked method".
- Ожидаемый вывод при запуске пункта меню: `Invoked method`.

2) Default
- В `MyDefaultClass` поле `name = null` и аннотация `@Default("Unknown")`.
- До: `name = null`; после обработки — `name = Unknown`.

3) ToString
- Объект `Person{firstName="Ivan", lastName="Ivanov", age=20}` → `Person{firstName=Ivan; lastName=Ivanov; age=20;}`.

4) Validate
- Если `age = -5` и аннотация `@Validate(min=0, max=150)`, то handler вернёт `Поле age вне диапазона [0,150]`.

5) Cache
- Первый вызов метода имитирует долгую операцию (sleep), второй возвращает значение мгновенно.

---

# Советы по отладке

- Если аннотация не видна в рантайме — обязательно проверьте `@Retention(RetentionPolicy.RUNTIME)`.
- Для приватных членов используйте `getDeclaredFields()` / `getDeclaredMethods()` и `setAccessible(true)`.
- Логируйте исключения: `InvocationTargetException.getCause()` даёт реальную причину ошибки в вызываемом методе.

---

# Что можно улучшить дальше

- Добавить JUnit тесты для каждого Handler (happy path + негатив).
- Ввести автосканирование классов по пакету (ClassPath scanner) вместо ручной регистрации.
- Расширить систему кэша (TTL, eviction, синхронизация).
- Вынести парсеры аннотаций и валидаторы в переиспользуемую библиотеку.

---

# Технологии

- Java 8/11+ (рекомендуется)
- Collections, Reflection, Annotations

---

Если хотите, могу дополнительно:
- вставить реальные примеры вывода, снятые с вашего проекта (запущу Main и добавлю выводы),
- добавить секцию с командами для запуска JUnit (если надо),
- адаптировать README под Gradle/Maven (создать `build.gradle` / `pom.xml`).

Напишите, как предпочитаете — настрою README под ваш стиль окончательно.
