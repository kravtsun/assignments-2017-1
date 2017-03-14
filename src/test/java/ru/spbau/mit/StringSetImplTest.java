package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class StringSetImplTest {
    private static final int MAX_STRING_SIZE = 100;
    private static final int TESTS_COUNT = 10000;

    private static final Random RANDOMIZER;
    private static final String RANDOM_SYMBOLS;

    static {
        final int randomizerSeed = 1123834521;
        RANDOMIZER = new Random(randomizerSeed);

        StringBuilder sb = new StringBuilder();
        for (char c = 'a'; c <= 'z'; ++c) {
            sb.append(c);
        }

        for (char c = 'A'; c <= 'Z'; ++c) {
            sb.append(c);
        }

        RANDOM_SYMBOLS = sb.toString();
    }

    private static String randomString() {
        int size = 1 + RANDOMIZER.nextInt(MAX_STRING_SIZE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int nextCharPosition = RANDOMIZER.nextInt(RANDOM_SYMBOLS.length());
            char nextSymbol = RANDOM_SYMBOLS.charAt(nextCharPosition);
            sb.append(nextSymbol);
        }
        return sb.toString();
    }

    @Test
    public void testSimple() {
        StringSetImpl stringSet = new StringSetImpl();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(1, stringSet.size());
        assertEquals(1, stringSet.howManyStartsWithPrefix("abc"));
    }

    @Test
    public void myTest() {
        StringSetImpl stringSet = new StringSetImpl();

        ArrayList<String> alreadyAdded = new ArrayList<>();
        for (int i = 0; i < TESTS_COUNT; i++) {
            String newString = randomString();
            assertEquals(stringSet.size(), alreadyAdded.size());
            if (alreadyAdded.contains(newString)) {
                assertFalse(stringSet.add(newString));
                assertEquals(stringSet.size(), alreadyAdded.size());
            } else {
                assertTrue(stringSet.add(newString));
                alreadyAdded.add(newString);
            }
        }

        for (String s : alreadyAdded) {
            assertFalse(stringSet.add(s));
            assertTrue(stringSet.contains(s));
        }

        for (int i = 0; i < TESTS_COUNT; i++) {
            String s = randomString();
            assertEquals(alreadyAdded.contains(s), stringSet.contains(s));
        }
    }

    @Test
    public void stringSetSpecificCases() {
        StringSetImpl stringSet = new StringSetImpl();
        String newString = randomString();
        assertEquals(stringSet.size(), 0);
        assertTrue(stringSet.add(newString));
        assertEquals(stringSet.size(), 1);
        assertFalse(stringSet.add(newString));
        assertEquals(stringSet.size(), 1);
        String prefix = newString.substring(0, newString.length() - 1);
        assertTrue(stringSet.add(prefix));
        assertTrue(stringSet.remove(newString));
        assertTrue(stringSet.contains(prefix) && !stringSet.contains(newString));
    }

    @Test
    public void typicalCase1() {
        StringSetImpl stringSet = new StringSetImpl();
        String newString = randomString();
        assertTrue(stringSet.add(newString));
        assertTrue(stringSet.contains(newString));
        assertEquals(stringSet.size(), 1);
        assertFalse(stringSet.add(newString));
        assertEquals(stringSet.size(), 1);
        String suffix = newString.substring(1);
        String prefix = newString.substring(0, newString.length() - 1);
        assertFalse(stringSet.contains(suffix));
        assertFalse(stringSet.contains(prefix));
        assertEquals(stringSet.size(), 1);
        assertFalse(stringSet.remove(suffix));
        assertFalse(stringSet.remove(prefix));
        assertTrue(stringSet.remove(newString));
        assertEquals(stringSet.size(), 0);
        assertFalse(stringSet.contains(suffix));
        assertFalse(stringSet.contains(prefix));
        assertFalse(stringSet.contains(newString));
        assertFalse(stringSet.remove(newString));
        assertFalse(stringSet.contains(suffix));
        assertFalse(stringSet.contains(prefix));
        assertFalse(stringSet.contains(newString));
    }

    @Test
    public void typicalCase2() {
        StringSetImpl stringSet = new StringSetImpl();
        String newString = randomString();
        assertTrue(stringSet.add(newString));
        assertFalse(stringSet.add(newString));

        String suffix = newString.substring(1);
        String prefix = newString.substring(0, newString.length() - 1);

        final int prefixTestCountMax = 10;
        final int prefixTestCount = RANDOMIZER.nextInt(prefixTestCountMax);
        for (int i = 0; i < prefixTestCount; ++i) {
            String checkPrefix = prefix.substring(0, RANDOMIZER.nextInt(prefix.length()));
            assertEquals(stringSet.howManyStartsWithPrefix(checkPrefix), 1);
        }

        assertEquals(stringSet.size(), 1);
        assertFalse(stringSet.remove(suffix));
        assertFalse(stringSet.remove(prefix));
        assertTrue(stringSet.remove(newString));
        assertEquals(stringSet.size(), 0);
    }
}
