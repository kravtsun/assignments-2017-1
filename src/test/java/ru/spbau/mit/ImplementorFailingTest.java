package ru.spbau.mit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ImplementorFailingTest {
    private static final Class<ClassNotFoundException> CLASS_NOT_FOUND = ClassNotFoundException.class;
    private static final Class<IllegalArgumentException> ILLEGAL_ARGUMENT = IllegalArgumentException.class;
    private String className;
    private Class<Throwable> expectedCauseClass;

    @Rule
    public TemporaryFolder outputDirectory = new TemporaryFolder();

    @Parameters
    public static List<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "a.b.c", ILLEGAL_ARGUMENT },
                { "BadString", CLASS_NOT_FOUND },
                { "Object.class", ILLEGAL_ARGUMENT },
                { ".SimpleImplementorTest", ILLEGAL_ARGUMENT },
                { "SimpleImplementorTest.", ILLEGAL_ARGUMENT },
                { "java.lang.Object", null },
                { "Object", null },
                { "java.lang.Object0", CLASS_NOT_FOUND }
        });
    }

    public ImplementorFailingTest(String className, Class<Throwable> expectedCause) {
        this.className = className;
        this.expectedCauseClass = expectedCause;
    }

    @Test
    public void test() {
        Implementor implementor = new SimpleImplementor(outputDirectory.getRoot().getPath());
        try {
            implementor.implementFromStandardLibrary(className);
            assertNull(expectedCauseClass);
        } catch (ImplementorException e) {
            assertThat(e.getCause(), isA(expectedCauseClass));
        }
    }
}