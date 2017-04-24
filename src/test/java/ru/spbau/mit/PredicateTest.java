package ru.spbau.mit;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class PredicateTest {
    private static final Integer FIVE_INTEGER = 5;
    private static final Double ZERO_DOUBLE = 0.0;
    private static final Double ONE_DOUBLE = 1.0;
    private static final Integer ONE_INTEGER = 1;
    private static final Integer BIG_INTEGER = 123123124;
    private static final Integer FOUR_INTEGER = 4;

    // Does not work.
//    private static final Function2<Integer, Integer, Boolean> less = new Function2<Integer, Integer, Boolean>() {
//        @Override
//        public Boolean apply2(Integer arg1, Integer arg2) {
//            return arg1 < arg2;
//        }
//    };

    private static final Predicate<Integer> LESS_THAN_FIVE = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            return arg < FIVE_INTEGER;
        }
    };

    private static final Predicate<Integer> IS_POSITIVE_INTEGER = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            return arg > 0;
        }
    };

    private static final Predicate<Double> IS_POSITIVE_DOUBLE = new Predicate<Double>() {
        @Override
        public Boolean apply(Double arg) {
            return arg > 0;
        }
    };

    private static final Predicate<Integer> FAIL_INTEGER = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            assert false;
            return null;
        }
    };

    private static final Predicate<Double> FAIL_DOUBLE = new Predicate<Double>() {
        @Override
        public Boolean apply(Double arg) {
            assert false;
            return null;
        }
    };

    private static final Predicate<Double> EQUAL_TO_ONE = new Predicate<Double>() {
        @Override
        public Boolean apply(Double arg) {
            return Objects.equals(ONE_DOUBLE, arg);
        }
    };

    @Test
    public void and() throws Exception {
        assertFalse(EQUAL_TO_ONE.and(FAIL_DOUBLE).apply(ZERO_DOUBLE));
        assertFalse(IS_POSITIVE_INTEGER.and(LESS_THAN_FIVE).apply(BIG_INTEGER));
        assertTrue(LESS_THAN_FIVE.and(IS_POSITIVE_INTEGER).apply(FOUR_INTEGER));
        assertTrue(IS_POSITIVE_DOUBLE.and(EQUAL_TO_ONE).apply(ONE_DOUBLE));
//        assertFalse(Predicate.ALWAYS_FALSE.and(FAIL_DOUBLE).apply(null));
    }

    @Test
    public void or() throws Exception {
//        assertTrue(EQUAL_TO_ONE.apply(ONE_INTEGER)); // Compilation error.
        assertFalse(EQUAL_TO_ONE.or(IS_POSITIVE_DOUBLE).apply(ZERO_DOUBLE));
        assertTrue(LESS_THAN_FIVE.or(IS_POSITIVE_INTEGER).apply(FIVE_INTEGER));
        assertTrue(LESS_THAN_FIVE.or(FAIL_INTEGER).apply(ONE_INTEGER));
        assertTrue(IS_POSITIVE_DOUBLE.or(EQUAL_TO_ONE).apply(ONE_DOUBLE));
//        assertTrue(Predicate.ALWAYS_TRUE.or(FAIL_INTEGER).apply(null));
    }

    @Test
    public void not() throws Exception {
        assertFalse(LESS_THAN_FIVE.not().apply(FOUR_INTEGER));
        assertTrue(LESS_THAN_FIVE.not().apply(FIVE_INTEGER));
    }

    @Test
    public void apply() throws Exception {
        assertTrue(LESS_THAN_FIVE.apply(FOUR_INTEGER));
        assertFalse(LESS_THAN_FIVE.apply(FIVE_INTEGER));
        assertFalse(EQUAL_TO_ONE.apply(ZERO_DOUBLE));
    }

}
