package ru.spbau.mit;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

// TODO smart tabulation.
public class SimpleImplementor implements Implementor {
    private static final String OPENING_BRACE = "{";
    private static final String CLOSING_BRACE = "}";
    private static final String OPENING_PARENTHESIS = "(";
    private static final String CLOSING_PARENTHESIS = ")";
    private static final String NEW_LINE = "\n";
    private static final String SPACE = " ";
    private static final String COLON = ";";
    private final String outputDirectory;

    public SimpleImplementor(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String implementFromDirectory(String directoryPath, String className) throws ImplementorException {
        try {
            String implClassName = getImplementationName(className);
            Class interfaceClazz = getClazz(directoryPath, className);
            work(implClassName, interfaceClazz);
            return implClassName;
        } catch (Throwable t) {
            throw dealWithThrownException(t);
        }
    }

    @Override
    public String implementFromStandardLibrary(String className) throws ImplementorException {
        try {
            final String packageName = getPackageName(className);
            String standardLibraryPackagePrefix = "java.";
            int prefixLength = standardLibraryPackagePrefix.length();
            if (!packageName.isEmpty()
                    && (packageName.length() < prefixLength
                    || !packageName.substring(0, prefixLength).equals(standardLibraryPackagePrefix))) {
                String errorMessage = "Wrong package for class from standard library: " + packageName;
                throw new IllegalArgumentException(errorMessage);
            }
            final String fullClassName = packageName.isEmpty() ? "java.lang." + className : className;
            final Class interfaceClazz = getClazz(fullClassName);

            // Cannot put implementation at standard library's package.
            final String implClassName = getImplementationName(getSimpleClassName(className));
            work(implClassName, interfaceClazz);
            return implClassName;
        } catch (Throwable t) {
            throw dealWithThrownException(t);
        }
    }

    private Class getClazz(String dir, String className) throws MalformedURLException, ClassNotFoundException {
        File outputDirectoryFile = new File(outputDirectory);
        File testDirectoryFile = new File(dir);
        URL[] urls = new URL[]{outputDirectoryFile.toURI().toURL(),
                testDirectoryFile.toURI().toURL()};
        ClassLoader classLoader = new URLClassLoader(urls);
        return Class.forName(className, false, classLoader);
    }

    @NotNull
    private static Class getClazz(String className) throws ClassNotFoundException {
        ClassLoader classLoader = getCurrentClassLoader();
        return Class.forName(className, false, classLoader);
    }

    private static String getImplementationName(String baseName) {
        return baseName + "Impl";
    }

    private static String getSimpleClassName(String className) {
        final int lastPointPosition = className.lastIndexOf('.');
        if (lastPointPosition == -1) {
            return className;
        }
//        else if (lastPointPosition + 1 >= className.length()) {
//            throw new IllegalArgumentException("Wrong className: " + className);
//        }
        return className.substring(lastPointPosition + 1);
    }

    private static String getSimpleClassName(Class clazz) {
        return getSimpleClassName(clazz.getCanonicalName());
    }

    private static String getFullClassName(Class clazz) {
        return clazz.getCanonicalName();
    }

    private static String getPackageName(String className) {
        final int lastPointPosition = className.lastIndexOf('.');
        if (lastPointPosition == 0 || lastPointPosition == className.length() - 1) {
            throw new IllegalArgumentException("Wrong className: " + className);
        }
        return lastPointPosition == -1 ? "" : className.substring(0, lastPointPosition);
    }

    private static Writer getImplWriter(String outputDirectory, String className) throws ImplementorException {
        String packageName = getPackageName(className);
        String packageSubPath = packageName.replace('.', '/');
        try {
            // TODO simplify dealing with parent directory.
            Path enclosingDirectoryPath = Paths.get(outputDirectory, packageSubPath);
            Path dir = Files.createDirectories(enclosingDirectoryPath);
            String implSimpleName = getSimpleClassName(className);
            Path implPath = Paths.get(dir.toUri().getRawPath(), implSimpleName + ".java");
            return Files.newBufferedWriter(implPath, StandardOpenOption.CREATE_NEW);
        } catch (Throwable t) {
            throw new ImplementorException("Error while initializing writer", t);
        }
    }

    private static ClassLoader getCurrentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private ImplementorException dealWithThrownException(Throwable t) {
        return t instanceof ImplementorException
                ? (ImplementorException) t
                : new ImplementorException("ImplementorException: ", t);
    }

    private void writeImports(Writer writer, String className) throws IOException {
        final String currentPackageName = getPackageName(className);
        if (!currentPackageName.isEmpty()) {
            writer.append("package ")
                    .append(currentPackageName)
                    .append(";")
                    .append(NEW_LINE);
        }
    }

    // TODO variable parameters.
    private String parametersString(Parameter[] parameters) {
        // TODO ensure ordering.
        return Arrays.stream(parameters)
                .map((p) -> getFullClassName(p.getType()) + SPACE + p.getName())
                .sequential()
                .collect(Collectors.joining(", "));
    }

    private String thrownExceptionsString(Class[] exceptionTypes) {
        String exceptionsString = Arrays.stream(exceptionTypes)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", "));
        return exceptionsString.isEmpty() ? "" : "throws " + exceptionsString + SPACE;
    }

    private void writeConstructors(Writer writer, Class baseClazz, String className) throws IOException {
        String simpleName = getSimpleClassName(className);
        // assuming there is no private constructors as we won't be able to use it anyway.
        for (Constructor constructor : baseClazz.getConstructors()) {
            Parameter[] parameters = constructor.getParameters();
            writer.append(simpleName)
                    .append(OPENING_PARENTHESIS)
                    .append(parametersString(parameters))
                    .append(CLOSING_PARENTHESIS)
                    .append(SPACE)
                    .append(thrownExceptionsString(constructor.getExceptionTypes()));
            // constructor implementation.
            writer.append(OPENING_BRACE).append(NEW_LINE);
            String args = Arrays.stream(parameters)
                    .map(Parameter::getName)
                    .collect(Collectors.joining(", "));
            writer.append("super")
                    .append(OPENING_PARENTHESIS)
                    .append(args)
                    .append(CLOSING_PARENTHESIS)
                    .append(COLON)
                    .append(NEW_LINE);
            writer.append(CLOSING_BRACE).append(NEW_LINE);
        }

    }

    private void writeMethods(Writer writer, Class baseClazz) throws IOException {
        if ((baseClazz.getModifiers() & Modifier.ABSTRACT) != 0) {
            for (Method method : baseClazz.getMethods()) {
                final int mods = method.getModifiers();
                if (Modifier.isAbstract(mods)) {
                    Class methodReturnType = method.getReturnType();
                    writer.append("public ")
                            .append(getFullClassName(methodReturnType))
                            .append(SPACE)
                            .append(method.getName());

                    // signature without modifiers and name.

                    writer.append(OPENING_PARENTHESIS)
                            .append(parametersString(method.getParameters()))
                            .append(CLOSING_PARENTHESIS)
                            .append(thrownExceptionsString(method.getExceptionTypes()))
                            .append(SPACE);

                    // method implementation.
                    writer.append(OPENING_BRACE).append(NEW_LINE);

                    // TODO check for cast warnings.
                    if (!methodReturnType.isPrimitive() || methodReturnType != void.class) {
                        String returnValueString;
                        if (!methodReturnType.isPrimitive()) {
                            returnValueString = "null";
                        } else if (methodReturnType == byte.class
                                || methodReturnType == char.class
                                || methodReturnType == short.class
                                || methodReturnType == int.class
                                || methodReturnType == long.class) {
                            returnValueString = "0";
                        } else if (methodReturnType == boolean.class) {
                            returnValueString = "false";
                        } else if (methodReturnType == float.class || methodReturnType == double.class) {
                            returnValueString = "0.0";
                        } else {
                            throw new UnknownError();
                        }
                        writer.append("return ")
                                .append(returnValueString)
                                .append(COLON)
                                .append(NEW_LINE);
                    }
                    writer.append(CLOSING_BRACE).append(NEW_LINE);
                }
            }
        }
    }

    private void work(String className, Class baseClazz) throws IOException, ImplementorException {
        if (Modifier.isFinal(baseClazz.getModifiers())) {
            throw new IllegalArgumentException("Unable to instantiate from final class: " + className);
        }
        Writer writer = getImplWriter(outputDirectory, className);
        writeImports(writer, className);
        String simpleName = getSimpleClassName(className);
        assert !baseClazz.isArray();
        String inheritanceAnnotation = baseClazz.isInterface()
                ? " implements " + getFullClassName(baseClazz)
                : " extends " + getFullClassName(baseClazz);

        writer.append("public class ")
                .append(simpleName)
                .append(inheritanceAnnotation)
                .append(SPACE)
                .append(OPENING_BRACE)
                .append(NEW_LINE);

        writeConstructors(writer, baseClazz, className);
        writeMethods(writer, baseClazz);

        // * imports
        // * implementation class declaration
        // * constructors redirecting to super, if baseClazz is abstract (not interface) -
        // cannot declare a constructor abstract class!!
        // * methods.

        writer.append(CLOSING_BRACE + NEW_LINE);
        writer.append(NEW_LINE);
        writer.close();
    }
}
