package shurmanov.tostring;

import java.lang.reflect.Field;

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
                    e.printStackTrace();
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