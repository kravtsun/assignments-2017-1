package ru.spbau.mit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class StringSetImplTest {
    private static final int MAX_STRING_SIZE = 100;
    private static final int TESTS_COUNT = 10000;
    private static final Random RANDOMIZER;
    private static final String RANDOM_SYMBOLS;
    private ArrayList<StringSetImpl> stringSets;
    private boolean testSerializationBetweenStates;

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

    @Before
    public void setUp() throws Exception {
        stringSets = new ArrayList<>();
        stringSets.add(new StringSetImpl());
        testSerializationBetweenStates = true;
    }

    @Test
    public void testSimple() {
        StringSetImpl stringSet = new StringSetImpl();
        assertAdd(true, ("abc"));
        assertContains(true, ("abc"));
        assertSize(1);
        assertHowManyStartsWithPrefix("abc", 1);
    }

    @Test
    public void myTest() {
        StringSetImpl stringSet = new StringSetImpl();
        ArrayList<String> alreadyAdded = new ArrayList<>();
        testSerializationBetweenStates = false;
        for (int i = 0; i < TESTS_COUNT; i++) {
            String newString = randomString();
            assertSize(alreadyAdded.size());
            if (alreadyAdded.contains(newString)) {
                assertAdd(false, (newString));
                assertSize(alreadyAdded.size());
            } else {
                assertAdd(true, (newString));
                alreadyAdded.add(newString);
            }
        }

        for (String s : alreadyAdded) {
            assertAdd(false, (s));
            assertContains(true, (s));
        }

        for (int i = 0; i < TESTS_COUNT; i++) {
            String s = randomString();
            assertContains(alreadyAdded.contains(s), s);
        }
        testSerializationBetweenStates = true;
    }

    @Test
    public void stringSetSpecificCases() {
        StringSetImpl stringSet = new StringSetImpl();
        String newString = randomString();
        assertSize(0);
        assertAdd(true, (newString));
        assertSize(1);
        assertAdd(false, (newString));
        assertSize(1);
        String prefix = newString.substring(0, newString.length() - 1);
        assertAdd(true, (prefix));
        assertRemove(true, (newString));
        assertContains(true, (prefix));
        assertContains(false, (newString));
    }

    @Test
    public void typicalCase1() {
        StringSetImpl stringSet = new StringSetImpl();
        String newString = randomString();
        assertAdd(true, (newString));
        assertContains(true, (newString));
        assertSize(1);
        assertAdd(false, (newString));
        assertSize(1);
        String suffix = newString.substring(1);
        String prefix = newString.substring(0, newString.length() - 1);
        assertContains(false, (suffix));
        assertContains(false, (prefix));
        assertSize(1);
        assertRemove(false, (suffix));
        assertRemove(false, (prefix));
        assertRemove(true, (newString));
        assertSize(0);
        assertContains(false, (suffix));
        assertContains(false, (prefix));
        assertContains(false, (newString));
        assertRemove(false, (newString));
        assertContains(false, (suffix));
        assertContains(false, (prefix));
        assertContains(false, (newString));
    }

    @Test
    public void typicalCase2() {
        StringSetImpl stringSet = new StringSetImpl();
        String newString = randomString();
        assertAdd(true, (newString));
        assertAdd(false, (newString));

        String suffix = newString.substring(1);
        String prefix = newString.substring(0, newString.length() - 1);

        final int prefixTestCount = 100;
        for (int i = 0; i < prefixTestCount; ++i) {
            String checkPrefix = prefix.substring(0, RANDOMIZER.nextInt(prefix.length()));
            assertHowManyStartsWithPrefix((checkPrefix), 1);
        }

        assertSize(1);
        assertRemove(false, (suffix));
        assertRemove(false, (prefix));
        assertRemove(true, (newString));
        assertSize(0);
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

    private StringSetImpl testSerialization(StringSetImpl olds) {
        StringSetImpl s = new StringSetImpl();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        olds.serialize(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        s.deserialize(in);
        return s;
    }

    private StringSetImpl lastStringSet() {
        int size = stringSets.size();
        assert(size > 0);
        return stringSets.get(size - 1);
    }

    private void assertAdd(boolean answer, String s) {
        if (testSerializationBetweenStates) {
            stringSets.add(testSerialization(lastStringSet()));
        }
        for (StringSetImpl stringSet : stringSets) {
            assertEquals(answer, stringSet.add(s));
        }
    }

    private void assertContains(boolean answer, String s) {
        for (StringSetImpl stringSet : stringSets) {
            assertEquals(answer, stringSet.contains(s));
        }
    }

    private void assertRemove(boolean answer, String s) {
        if (testSerializationBetweenStates) {
            stringSets.add(testSerialization(lastStringSet()));
        }
        for (StringSetImpl stringSet : stringSets) {
            assertEquals(answer, stringSet.remove(s));
        }
    }

    private void assertSize(int answer) {
        for (StringSetImpl stringSet : stringSets) {
            assertEquals(answer, stringSet.size());
        }
    }

    private void assertHowManyStartsWithPrefix(String prefix, int answer) {
        for (StringSetImpl stringSet : stringSets) {
            assertEquals(answer, stringSet.howManyStartsWithPrefix(prefix));
        }
    }
}
