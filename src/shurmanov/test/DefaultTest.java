package shurmanov.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import shurmanov.defaultann.Default;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DefaultAnnotationTest {

    @Default(String.class)
    static class ClassWithStringDefault {}

    @Default(Integer.class)
    static class ClassWithIntegerDefault {}

    static class ClassWithFieldDefault {
        @Default(Double.class)
        private Double number;
    }


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


    static Stream<Object[]> classProvider() {
        return Stream.of(
                new Object[]{ClassWithStringDefault.class, String.class},
                new Object[]{ClassWithIntegerDefault.class, Integer.class}
        );
    }

    @ParameterizedTest
    @MethodSource("classProvider")
    void parameterizedTestForDefaultAnnotation(Class<?> clazz, Class<?> expectedType) {

        Default annotation = clazz.getAnnotation(Default.class);

        assertNotNull(annotation);
        assertEquals(expectedType, annotation.value());
    }
}