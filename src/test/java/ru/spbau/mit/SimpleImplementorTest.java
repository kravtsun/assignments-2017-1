package ru.spbau.mit;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleImplementorTest {
    private static class PrivateNonInheritable {}

    public static final class FinalNonInheritable {}

    static private Class<SimpleImplementor> clazz = SimpleImplementor.class;

    @Rule
    public TemporaryFolder outputDirectory = new TemporaryFolder();

    static private Implementor implementor = null;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // Failing guidelines //
    /*   1) Невозможно создать наследника класса.
     *   2) Входной класс не найден.
     *   3) Невозможно записать сгенерированный класс.
     */

    @Before
    public void setUp() {
//        outputDirectory.create();
        implementor = new SimpleImplementor(outputDirectory.getRoot().getAbsolutePath());
        // It is accumulated throw test cases manipulating with expectedException, isn't it?
        expectedException = ExpectedException.none();
    }

    public final class PublicNonInheritable {}
    private interface PrivateInheritable {}

    private static String projectDirectory() {
        return System.getProperty("user.dir");
    }

    @Test
    public void impossibleToCreateSuccessor() {
        try {
            implementor.implementFromStandardLibrary("System");
            fail("Exception expected");
        }
        catch (ImplementorException e) {
            assertTrue(IllegalArgumentException.class.isInstance(e.getCause()));
        }
    }

    @Test(expected = ImplementorException.class)
    public void alreadyExists() throws IOException, ImplementorException {
        outputDirectory.newFile("StringImpl.class");
        expectedException.expectCause(isA(FileAlreadyExistsException.class));
        implementor.implementFromStandardLibrary("String");
    }

    @Test
    public void getClazz() throws ReflectiveOperationException {
        Method method = clazz.getDeclaredMethod("getClazz", String.class);
        startWorkingWithPrivateMethod(method);
        Class clazz = null;
        clazz = (Class) method.invoke(null, "java.lang.String");
        assertEquals("java.lang.String", clazz.getName());
        clazz = (Class) method.invoke(null, "ru.spbau.mit.SimpleImplementorTest");
        assertEquals("ru.spbau.mit.SimpleImplementorTest", clazz.getCanonicalName());
        stopWorkingWithPrivateMethod(method);
    }

    private static void startWorkingWithPrivateMethod(Method method) {
        assertFalse(method.isAccessible());
        method.setAccessible(true);
    }

    private static void stopWorkingWithPrivateMethod(Method method) {
        method.setAccessible(false);
    }

    @Test
    public void getImplementationName() throws ReflectiveOperationException {
        Method method = clazz.getDeclaredMethod("getImplementationName", String.class);
        startWorkingWithPrivateMethod(method);
        assertEquals("ComparableImpl", method.invoke(null, "Comparable"));
        assertEquals("ru.spbau.mit.AnInterfaceImpl", method.invoke(null, "ru.spbau.mit.AnInterface"));
        stopWorkingWithPrivateMethod(method);
    }

    @Test
    public void getSimpleClassName() throws ReflectiveOperationException {
        Method method = clazz.getDeclaredMethod("getSimpleClassName", String.class);
        startWorkingWithPrivateMethod(method);
        assertEquals("c", method.invoke(null, "a.b.c"));
        assertEquals("String", method.invoke(null, "String"));
        stopWorkingWithPrivateMethod(method);
    }

}