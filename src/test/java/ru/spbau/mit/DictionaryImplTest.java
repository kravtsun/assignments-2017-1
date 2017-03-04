package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DictionaryImplTest {
    private static final int NTESTS = 1000;
    private static final int MAX_STRING_SIZE = 100;
    private static final int MAX_CHAR = 256;
    private static final Random RANDOMIZER;
    private static final int RANDOMIZER_SEED = 1092423045;
    private static final String RANDOM_SYMBOLS;

    static {
        RANDOMIZER = new Random(RANDOMIZER_SEED);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_CHAR; ++i) {
            sb.append((char) i);
        }

        RANDOM_SYMBOLS = sb.toString();
    }

    private static String randomString() {
        int size = 1 + RANDOMIZER.nextInt(MAX_STRING_SIZE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            int nextCharPosition = RANDOMIZER.nextInt(RANDOM_SYMBOLS.length());
            char nextSymbol = RANDOM_SYMBOLS.charAt(nextCharPosition);
            sb.append(nextSymbol);
        }
        return sb.toString();
    }

    @Test
    public void size() throws Exception {
        DictionaryImpl d = new DictionaryImpl();
        int size = 0;
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < NTESTS; i++) {
            assertTrue(d.size() == size);
            String key = randomString();
            if (!keys.contains(key)) {
                keys.add(key);
                size++;
                d.put(key, randomString());
                assertTrue(d.size() == size);
            }
        }
        assertTrue(d.size() == size);
    }

    @Test
    public void contains() throws Exception {
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        DictionaryImpl d = new DictionaryImpl();

        // section "put".
        for (int i = 0; i < NTESTS; ++i) {
            String key = randomString();
            String value = randomString();
            if (keys.contains(key)) {
                int index = keys.indexOf(key);
                assertEquals(d.put(key, value), values.get(index));
                values.set(index, value);
            } else {
                keys.add(key);
                values.add(value);
                assertEquals(d.put(key, value), null);
            }
            assertTrue(d.contains(key));
            assertEquals(value, d.get(key));
        }

        // section "contains".
        for (int i = 0; i < NTESTS; ++i) {
            int index = RANDOMIZER.nextInt(keys.size());
            assertTrue(d.contains(keys.get(index)));
            assertEquals(d.get(keys.get(index)), values.get(index));
        }

        // section "remove".
        boolean[] used = new boolean[keys.size()];
        Arrays.fill(used, false);
        for (int i = 0; i < NTESTS; ++i) {
            int index = RANDOMIZER.nextInt(keys.size());
            String key = keys.get(index), value = values.get(index);
            if (used[index]) {
                assertEquals(d.remove(key), null);
            } else {
                assertEquals(d.remove(key), value);
                assertEquals(d.remove(key), null);
                assertEquals(d.remove(key), null);
                used[index] = true;
            }
        }
    }

    private static boolean tossCoin(int truePercentage) {
        return RANDOMIZER.nextInt(100) < truePercentage;
    }
    @Test
    public void clear() throws Exception {
        DictionaryImpl d = new DictionaryImpl();
        assertEquals(d.size(), 0);
        d.clear();
        assertEquals(d.size(), 0);

        ArrayList<String> keys = new ArrayList<>();
        final int clearFrequency = 10;
        for (int i = 0; i < NTESTS; ++i) {
            String key = randomString();
            String value = randomString();
            if (keys.contains(key)) {
                continue;
            }
            d.put(key, value);
            if (tossCoin(clearFrequency)) {
                d.clear();
                for (String k : keys) {
                    assertTrue(!d.contains(k));
                }
                keys.clear();
            }

        }
    }

    @Test
    public void testEmpty() throws Exception {
        DictionaryImpl d = new DictionaryImpl();
        String emptyValue = null;
        ArrayList<String> keys = new ArrayList<>();

        final int putEmptyFrequency = 25;
        final int removeEmptyFrequency = 50;
        final int removeAnyFrequency = 30;
        for (int i = 0; i < NTESTS; ++i) {
            if (tossCoin(putEmptyFrequency)) {
                String newEmptyValue = randomString();
                assertEquals(d.put("", newEmptyValue), emptyValue);
                keys.add("");
                emptyValue = newEmptyValue;
            }
            if (tossCoin(removeEmptyFrequency)) {
                assertEquals(d.remove(""), emptyValue);
                keys.remove("");
                emptyValue = null;
            }

            if (tossCoin(removeAnyFrequency) && !keys.isEmpty()) {
                String key = keys.get(RANDOMIZER.nextInt(keys.size()));
                if (!key.isEmpty()) {
                    d.remove(key);
                    keys.remove(key);
                    assertEquals(d.size(), keys.size());
                }
            }
        }
    }

}