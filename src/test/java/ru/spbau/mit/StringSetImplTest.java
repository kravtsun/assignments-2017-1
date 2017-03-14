package ru.spbau.mit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class StringSetImplTest {
    private final static int MAX_STRING_SIZE = 100;
    private final static int TESTS_COUNT = 10000;

    private final static Random randomizer;
    private final static String random_symbols;

    static {
        randomizer = new Random(1123834521);

        StringBuilder sb = new StringBuilder();
        for (char c = 'a'; c <= 'z'; ++c) {
            sb.append(c);
        }

        for (char c = 'A'; c <= 'Z'; ++c) {
            sb.append(c);
        }

        random_symbols = sb.toString();
    }

    private static String randomString() {
        int size = 1 + randomizer.nextInt(MAX_STRING_SIZE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int nextCharPosition = randomizer.nextInt(random_symbols.length());
            char nextSymbol = random_symbols.charAt(nextCharPosition);
            sb.append(nextSymbol);
        }
        return sb.toString();
    }

    @Test
    public void testSimple() {
        StringSetImpl StringSetImpl = new StringSetImpl();

        assertTrue(StringSetImpl.add("abc"));
        assertTrue(StringSetImpl.contains("abc"));
        assertEquals(1, StringSetImpl.size());
        assertEquals(1, StringSetImpl.howManyStartsWithPrefix("abc"));
    }
//
//    public static StringSetImpl instance() {
//        try {
//            return (StringSetImpl) Class.forName("StringSetImpl").newInstance();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        throw new IllegalStateException("Error while class loading");
//    }

    @Test
    public void myTest() {
        StringSetImpl StringSet = new StringSetImpl();

        ArrayList<String> alreadyAdded = new ArrayList<>();
        for (int i = 0; i < TESTS_COUNT; i++) {
            String newString = randomString();
            assertEquals(StringSet.size(), alreadyAdded.size());
            if (alreadyAdded.contains(newString)) {
                assertFalse(StringSet.add(newString));
                assertEquals(StringSet.size(), alreadyAdded.size());
            } else {
                assertTrue(StringSet.add(newString));
                alreadyAdded.add(newString);
            }
        }

        for (String s : alreadyAdded) {
            assertFalse(StringSet.add(s));
            assertTrue(StringSet.contains(s));
        }

        for (int i = 0; i < TESTS_COUNT; i++) {
            String s = randomString();
            assertEquals(alreadyAdded.contains(s), StringSet.contains(s));
        }
    }

    @Test
    public void StringSetSpecificCases() {
        StringSetImpl StringSet = new StringSetImpl();
        String newString = randomString();
        assertEquals(StringSet.size(), 0);
        assertTrue(StringSet.add(newString));
        assertEquals(StringSet.size(), 1);
        assertFalse(StringSet.add(newString));
        assertEquals(StringSet.size(),1);
        String prefix = newString.substring(0, newString.length()-1);
        assertTrue(StringSet.add(prefix));
        assertTrue(StringSet.remove(newString));
        assertTrue(StringSet.contains(prefix) && !StringSet.contains(newString));
    }

    @Test
    public void typicalCase1() {
        StringSetImpl StringSet = new StringSetImpl();
        String newString = randomString();
        assertTrue(StringSet.add(newString));
        assertTrue(StringSet.contains(newString));
        assertEquals(StringSet.size(), 1);
        assertFalse(StringSet.add(newString));
        assertEquals(StringSet.size(), 1);
        String suffix = newString.substring(1);
        String prefix = newString.substring(0, newString.length()-1);
        assertFalse(StringSet.contains(suffix));
        assertFalse(StringSet.contains(prefix));
        assertEquals(StringSet.size(), 1);
        assertFalse(StringSet.remove(suffix));
        assertFalse(StringSet.remove(prefix));
        assertTrue(StringSet.remove(newString));
        assertEquals(StringSet.size(), 0);
        assertFalse(StringSet.contains(suffix));
        assertFalse(StringSet.contains(prefix));
        assertFalse(StringSet.contains(newString));
        assertFalse(StringSet.remove(newString));
        assertFalse(StringSet.contains(suffix));
        assertFalse(StringSet.contains(prefix));
        assertFalse(StringSet.contains(newString));
    }

    @Test
    public void typicalCase2() {
        StringSetImpl StringSet = new StringSetImpl();
        String newString = randomString();
        assertTrue(StringSet.add(newString));
        assertFalse(StringSet.add(newString));

        String suffix = newString.substring(1);
        String prefix = newString.substring(0, newString.length()-1);

        int prefixTestCount = randomizer.nextInt(10);
        for (int i = 10; i < prefixTestCount; ++i) {
            String checkPrefix = prefix.substring(0, randomizer.nextInt(prefix.length()));
            assertEquals(StringSet.howManyStartsWithPrefix(checkPrefix), 1);
        }

        assertEquals(StringSet.size(), 1);
        assertFalse(StringSet.remove(suffix));
        assertFalse(StringSet.remove(prefix));
        assertTrue(StringSet.remove(newString));
        assertEquals(StringSet.size(), 0);
    }
}
