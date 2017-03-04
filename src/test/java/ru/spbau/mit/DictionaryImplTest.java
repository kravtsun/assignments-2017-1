package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by kravtsun on 04.03.17.
 */
public class DictionaryImplTest {
    private final static int NTESTS = 1000;
    private final static int MAX_STRING_SIZE = 100;
    private final static int MAX_CHAR = 256;
    private final static Random randomizer;
    private final static String random_symbols;

    static {
        randomizer = new Random(1092423045);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_CHAR; ++i) {
            sb.append((char)(i));
        }

        random_symbols = sb.toString();
    }

    private static String randomString() {
        int size = 1 + randomizer.nextInt(MAX_STRING_SIZE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            int nextCharPosition = randomizer.nextInt(random_symbols.length());
            char nextSymbol = random_symbols.charAt(nextCharPosition);
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
            String key = randomString(), value = randomString();
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
            int index = randomizer.nextInt(keys.size());
            assertTrue(d.contains(keys.get(index)));
            assertEquals(d.get(keys.get(index)), values.get(index));
        }

        // section "remove".
        boolean[] used = new boolean[keys.size()];
        Arrays.fill(used, false);
        for (int i = 0; i < NTESTS; ++i) {
            int index = randomizer.nextInt(keys.size());
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

    @Test
    public void clear() throws Exception {
        DictionaryImpl d = new DictionaryImpl();
        assertEquals(d.size(), 0);
        d.clear();
        assertEquals(d.size(), 0);

        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < NTESTS; ++i) {
            String key = randomString();
            String value = randomString();
            if (keys.contains(key)) {
                continue;
            }
            d.put(key, value);
            if (randomizer.nextInt(100) < 10) {
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
        for (int i = 0; i < NTESTS; ++i) {
            if (randomizer.nextInt(100) < 25) {
                String newEmptyValue = randomString();
                assertEquals(d.put("", newEmptyValue), emptyValue);
                keys.add("");
                emptyValue = newEmptyValue;
            }
            if (randomizer.nextInt(2) == 0) {
                assertEquals(d.remove(""), emptyValue);
                keys.remove("");
                emptyValue = null;
            }

            if (randomizer.nextInt(100) < 20 && !keys.isEmpty()) {
                String key = keys.get(randomizer.nextInt(keys.size()));
                if (!key.isEmpty()) {
                    d.remove(key);
                    keys.remove(key);
                    assertEquals(d.size(), keys.size());
                }
            }
        }
    }

}