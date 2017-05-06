package ru.spbau.mit;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class Function1Test {
    private static final Function1<Integer[], Collection<Integer>> FROM_ARRAY_TO_COLLECTION =
            new Function1<Integer[], Collection<Integer>>() {
                @Override
                public Collection<Integer> apply(Integer[] arg) {
                    return Arrays.asList(arg);
                }
            };

    private final Function1<Integer, Integer> sqr = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer arg) {
            return arg * arg;
        }
    };

    private final Function1<Object, Object> id = new Function1<Object, Object>() {
        @Override
        public Object apply(Object arg) {
            return arg;
        }
    };

    @Test
    public void compose() throws Exception {
        Function1<Integer, Double> g = new Function1<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return integer.doubleValue();
            }
        };
        final Double fiveDouble = 5.0;
        final Integer fiveInteger = 5;
        assertEquals(fiveDouble, g.apply(fiveInteger));

        Function1<Number, String> f = new Function1<Number, String>() {
            @Override
            public String apply(Number number) {
                return number.toString();
            }
        };

        Function1<Integer, String> fg = g.compose(f);
        String fiveDoubleString = "5.0";
        assertEquals(fiveDoubleString, fg.apply(fiveInteger));

        Function1<Object, Integer> hash = new Function1<Object, Integer>() {
            @Override
            public Integer apply(Object arg) {
                return arg.hashCode();
            }
        };

        Integer i = 1;
        // must compile.
        final Integer someInteger = 123;
        assertEquals(0, hash.apply(someInteger) % 1);


        final int arraySize = 10;
        Integer[] integers = new Integer[arraySize];
        FROM_ARRAY_TO_COLLECTION.apply(integers); // compilation OK.

//        Double []doubles = new Double[arraySize];
//        FROM_ARRAY_TO_COLLECTION.apply(doubles); // compilation Failed.

//        Function1<Number, Object>
//        Function1<Object, Boolean> u1 = new Function1<Object, Boolean>() {
//            @Override
//            Boolean apply(Object o) {
//                return o.hashCode() % 2 == 0;
//            }
//        };
//
        final Integer twentyFiveInteger = 25;
        assertEquals(twentyFiveInteger, sqr.compose(id).apply(fiveInteger));
        assertEquals(fiveInteger, id.compose(id).apply(fiveInteger));
        String someString = "5";
        assertEquals(someString, id.compose(id).apply(someString));
    }

}
