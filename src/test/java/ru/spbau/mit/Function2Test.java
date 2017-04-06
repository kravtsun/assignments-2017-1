package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function2Test {

    private static final Integer FIVE_INTEGER = 5;
    private static final Integer FOUR_INTEGER = 4;
    private static final Integer THREE_INTEGER = 3;
    private static final Integer ONE_INTEGER = 1;
    private static final Integer ZERO_INTEGER = 0;

    private final Function1<Integer, Integer> sqr = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer arg) {
            return arg * arg;
        }
    };

    private final Function2<Integer, Integer, Integer> minus = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply2(Integer arg1, Integer arg2) {
            return arg1 - arg2;
        }
    };

    private final Function1<Object, Object> id = new Function1<Object, Object>() {
        @Override
        public Object apply(Object arg) {
            return arg;
        }
    };

    @Test
    public void curry() throws Exception {
        Function1<Integer, Integer> fiveMinus = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer arg) {
                return FIVE_INTEGER - arg;
            }
        };

        assertEquals(ONE_INTEGER, minus.curry().apply(FIVE_INTEGER).apply(FOUR_INTEGER));
        assertEquals(ONE_INTEGER, fiveMinus.apply(FOUR_INTEGER));
//        assertEquals(ONE_INTEGER, minus.curry(fiveDouble).apply(FOUR_INTEGER)); // Compilation error.
//        assertEquals(ONE_INTEGER, minus.curry(ONE_INTEGER).apply(fiveDouble)); // Compilation error.
    }

    @Test
    public void apply2() throws Exception {
        assertEquals(FOUR_INTEGER, minus.apply2(FIVE_INTEGER, ONE_INTEGER));
//        assertEquals(ONE_INTEGER, minus.apply2(FIVE_INTEGER, (Double) FOUR_INTEGER)); // Compilation error.
    }

    @Test
    public void compose() throws Exception {
        assertEquals(ONE_INTEGER, minus.compose(id).apply2(FIVE_INTEGER, FOUR_INTEGER));
        assertEquals(ONE_INTEGER, minus.compose(sqr).apply2(FOUR_INTEGER, THREE_INTEGER));
    }

    @Test
    public void bind1() throws Exception {
        assertEquals(FOUR_INTEGER, minus.bind1(FIVE_INTEGER).apply(ONE_INTEGER));
    }

    @Test
    public void bind2() throws Exception {
        assertEquals(ZERO_INTEGER, minus.bind2(ONE_INTEGER).apply(ONE_INTEGER));
    }
}
