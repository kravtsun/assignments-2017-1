package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CollectionsTest {
    private static final Integer MAGIC_1 = 1;
    private static final Integer MAGIC_2 = -4;
    private static final Integer MAGIC_3 = 3;
    private static final Integer MAGIC_4 = 10;
    private static final Integer MAGIC_5 = -20;

    private static final ArrayList<Integer> INT_ARR = new ArrayList<Integer>() { {
        add(MAGIC_1);
        add(MAGIC_2);
        add(MAGIC_3);
        add(MAGIC_4);
        add(MAGIC_5);
    } };

    private static final ArrayList<Integer> INT_ARR_NEGATED = new ArrayList<Integer>() { {
        add(-MAGIC_1);
        add(-MAGIC_2);
        add(-MAGIC_3);
        add(-MAGIC_4);
        add(-MAGIC_5);
    } };

    private static final ArrayList<String> INT_ARR_STRINGED = new ArrayList<String>() { {
        add(MAGIC_1.toString());
        add(MAGIC_2.toString());
        add(MAGIC_3.toString());
        add(MAGIC_4.toString());
        add(MAGIC_5.toString());
    } };

    private static final Predicate<Integer> IS_POSITIVE_INTEGER = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            return arg > 0;
        }
    };

    private static final List<Object> EMPTY_ARR = new ArrayList<>();

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
    }

    @Test
    public void takeWhile() throws Exception {
        Predicate<CharSequence> u2 = new Predicate<CharSequence>() {
            @Override
            public Boolean apply(CharSequence charSequence) {
                return charSequence.length() < 2;
            }
        };

        ArrayList<String> firstSmall = new ArrayList<String>() { {
            add("1");
        } };

        assertEquals(firstSmall, Collections.takeWhile(u2, INT_ARR_STRINGED));
        assertEquals(EMPTY_ARR, Collections.takeWhile(IS_POSITIVE_INTEGER, INT_ARR_NEGATED));
        assertEquals(INT_ARR_STRINGED, Collections.takeWhile(Predicate.ALWAYS_TRUE, INT_ARR_STRINGED));
    }

    @Test
    public void takeUnless() throws Exception {
        ArrayList<Integer> firstNegative = new ArrayList<Integer>() { {
            add(-1);
        } };

        assertEquals(EMPTY_ARR, Collections.takeUnless(IS_POSITIVE_INTEGER.not(), INT_ARR_NEGATED));
        assertEquals(firstNegative, Collections.takeUnless(IS_POSITIVE_INTEGER, INT_ARR_NEGATED));
        assertEquals(INT_ARR, Collections.takeUnless(Predicate.ALWAYS_FALSE, INT_ARR));
    }


    @Test
    public void foldl() throws Exception {
        final Integer result = 10;
        assertEquals(result, Collections.foldl(MINUS, INT_ARR, (Integer) 0));

        final Function2<String, Integer, String> accumulateString = new Function2<String, Integer, String>() {
            @Override
            public String apply2(String arg1, Integer arg2) {
                return arg1 + arg2.toString();
            }
        };

        final String initString = "Hello, ";
        final String resultString = "Hello, 1-4310-20";
        assertEquals(resultString, Collections.foldl(accumulateString, INT_ARR, initString));
        assertEquals(0, Collections.foldl(FAIL_FUNCTION, EMPTY_ARR, 0));
    }

    @Test
    public void foldr() throws Exception {
        final Function2<Integer, String, String> accumulateString = new Function2<Integer, String, String>() {
            @Override
            public String apply2(Integer arg1, String arg2) {
                return arg1.toString() + arg2;
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
