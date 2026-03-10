package shurmanov.cache;

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