package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

public class CollectionsTest {
    private static final Integer MAGIC_1 = 1;
    private static final Integer MAGIC_2 = -4;
    private static final Integer MAGIC_3 = 3;
    private static final Integer MAGIC_4 = 10;
    private static final Integer MAGIC_5 = -20;

    private static final List<Integer> INT_ARR = Arrays.asList(
            MAGIC_1, MAGIC_2, MAGIC_3, MAGIC_4, MAGIC_5);

    private static final List<Integer> INT_ARR_NEGATED = Arrays.asList(
            -MAGIC_1, -MAGIC_2, -MAGIC_3, -MAGIC_4, -MAGIC_5);

    private static final List<Integer> ARR_WITH_NULL = Arrays.asList(
            null, 1, null, 4, null);

    private static final List<String> INT_ARR_STRINGED = Arrays.asList(
        MAGIC_1.toString(),
        MAGIC_2.toString(),
        MAGIC_3.toString(),
        MAGIC_4.toString(),
        MAGIC_5.toString()
    );

    private static final Predicate<Integer> IS_POSITIVE_INTEGER = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            return arg > 0;
        }
    };

    private static final Predicate<Object> IS_NULL = new Predicate<Object>() {
        @Override
        public Boolean apply(Object arg) {
            return arg == null;
        }
    };

    private static final Predicate<Object> NOT_NULL = IS_NULL.not();

    private static final List<Object> EMPTY_ARR = (List<Object>) emptyList();

    private static final Function2<Integer, Integer, Integer> MINUS = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply2(Integer arg1, Integer arg2) {
            return arg1 - arg2;
        }
    };

    private static final Function1<Object, Object> ID = new Function1<Object, Object>() {
        @Override
        public Object apply(Object arg) {
            return arg;
        }
    };

    private static final Function1<Object, String> TO_STRING = new Function1<Object, String>() {
        @Override
        public String apply(Object arg) {
            return arg.toString();
        }
    };

    private static final Function2<Object, Object, Object> FAIL_FUNCTION =
            new Function2<Object, Object, Object>() {
        @Override
        public Object apply2(Object arg1, Object arg2) {
            assert false;
            return null;
        }
    };

    @Test
    public void map() throws Exception {
        assertEquals(INT_ARR_NEGATED, Collections.map(MINUS.bind1(0), INT_ARR));
        assertEquals(EMPTY_ARR, Collections.map(ID, EMPTY_ARR));
        assertEquals(INT_ARR_STRINGED, Collections.map(TO_STRING, INT_ARR));
        assertEquals(INT_ARR, Collections.map(ID, INT_ARR));
    }

    @Test
    public void filter() throws Exception {
        final ArrayList<Integer> positiveOnly = new ArrayList<Integer>() { {
            add(-MAGIC_2);
            add(-MAGIC_5);
        } };
        assertEquals(positiveOnly, Collections.filter(IS_POSITIVE_INTEGER, INT_ARR_NEGATED));
        assertEquals(INT_ARR, Collections.filter(Predicate.ALWAYS_TRUE, INT_ARR));
        assertEquals(EMPTY_ARR, Collections.filter(Predicate.ALWAYS_FALSE, INT_ARR));
        assertEquals(EMPTY_ARR, Collections.filter(IS_NULL, INT_ARR));
        assertEquals(Arrays.asList(null, null, null), Collections.filter(IS_NULL, ARR_WITH_NULL));
        Predicate<Object> isTen = new Predicate<Object>() {
            @Override
            public Boolean apply(Object arg) {
                return (arg).toString().equals("10");
            }
        };
        final int ten = 10;
        assertEquals(Arrays.asList(ten), Collections.filter(isTen, INT_ARR));
    }

    @Test
    public void takeWhile() throws Exception {
        Predicate<CharSequence> u2 = new Predicate<CharSequence>() {
            @Override
            public Boolean apply(CharSequence charSequence) {
                return charSequence.length() < 2;
            }
        };
        assertEquals(Arrays.asList("1"), Collections.takeWhile(u2, INT_ARR_STRINGED));
        assertEquals(EMPTY_ARR, Collections.takeWhile(IS_POSITIVE_INTEGER, INT_ARR_NEGATED));
        assertEquals(INT_ARR_STRINGED, Collections.takeWhile(Predicate.ALWAYS_TRUE, INT_ARR_STRINGED));
        ArrayList<Integer> nullArray = new ArrayList<>();
        nullArray.add(null);
        assertEquals(nullArray, Collections.takeWhile(IS_NULL, ARR_WITH_NULL));
        assertEquals(EMPTY_ARR, Collections.takeWhile(IS_NULL, INT_ARR));
    }

    @Test
    public void takeUnless() throws Exception {
        assertEquals(EMPTY_ARR, Collections.takeUnless(IS_POSITIVE_INTEGER.not(), INT_ARR_NEGATED));
        assertEquals(Arrays.asList(-1), Collections.takeUnless(IS_POSITIVE_INTEGER, INT_ARR_NEGATED));
        assertEquals(INT_ARR, Collections.takeUnless(Predicate.ALWAYS_FALSE, INT_ARR));
        ArrayList<Objects> nullArr = new ArrayList<>();
        nullArr.add(null);
        assertEquals(nullArr, Collections.takeUnless(NOT_NULL, ARR_WITH_NULL));
        assertEquals(EMPTY_ARR, Collections.takeUnless(IS_NULL, ARR_WITH_NULL));
    }

    @Test
    public void foldl() throws Exception {
        final Integer result = 10;
        assertEquals(result, Collections.foldl(MINUS, INT_ARR, (Integer) 0));

        final Function2<String, Object, String> accumulateString = new Function2<String, Object, String>() {
            @Override
            public String apply2(String arg1, Object arg2) {
                return arg1 + Objects.toString(arg2);
            }
        };

        final String initString = "Hello, ";
        final String resultString = "Hello, 1-4310-20";
        assertEquals(resultString, Collections.foldl(accumulateString, INT_ARR, initString));
        assertEquals(0, Collections.foldl(FAIL_FUNCTION, EMPTY_ARR, 0));
        String expected = "null1null4null";
        assertEquals(expected, Collections.foldl(accumulateString, ARR_WITH_NULL, ""));
    }

    @Test
    public void foldr() throws Exception {

        final Function2<Object, String, String> accumulateString = new Function2<Object, String, String>() {
            @Override
            public String apply2(Object arg1, String arg2) {
                return Objects.toString(arg1) + arg2;
            }
        };

        final String initString = "123";
        final String resultString = "-14-3-1020123";
        assertEquals(resultString, Collections.foldr(accumulateString, INT_ARR_NEGATED, initString));

        final Integer init = -33;
        final Integer result = 11;
        assertEquals(result, Collections.foldr(MINUS, INT_ARR, init));
        assertEquals(0, Collections.foldr(FAIL_FUNCTION, EMPTY_ARR, 0));
    }
}
