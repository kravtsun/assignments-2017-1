package ru.spbau.mit;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class StreamSerializableTest {
    @Test(expected = SerializationException.class)
    public void testFailEmpty() {
        testDeserialization(new ByteArrayOutputStream());
    }

    @Test(expected = SerializationException.class)
    public void testFailDummyHeader() {
        final int dummyHeader = 0xBABEFAFA;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeInteger(dummyHeader, out);
        testDeserialization(out);
    }

    @Test
    public void testEmpty() {
        StringSetImpl s = new StringSetImpl();
        assertEmptyStringSetImpl(s); // just for sureness.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        s.serialize(out);
        StringSetImpl s1 = testDeserialization(out);
        assertEmptyStringSetImpl(s1);

        String[] arr = {"abc", "cde", ""};
        for (String str : arr) {
            assertTrue(s1.add(str));
        }

        for (String str : arr) {
            assertTrue(s1.remove(str));
        }

        out.reset();
        s1.serialize(out);

        StringSetImpl s2 = testDeserialization(out);
        assertEmptyStringSetImpl(s2);
    }

    private static void writeInteger(int num, OutputStream out) {
        final int bytesInInteger = 4;
        final int bitsInByte = 8;
        try {
            for (int i = 0; i < bytesInInteger; ++i) {
                final int lowestByte = num & ((1 << bitsInByte) - 1);
                num >>= bitsInByte;
                out.write(lowestByte);
            }
        } catch (IOException e) {
            fail();
        }
    }

    private static StringSetImpl testDeserialization(ByteArrayOutputStream out) throws SerializationException {
        StringSetImpl stringSet = new StringSetImpl();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ((StreamSerializable) stringSet).deserialize(in);
        return stringSet;
    }

    private static void assertEmptyStringSetImpl(StringSetImpl s) {
        assertNotNull(s);
        assertEquals(0, s.size());
        assertEquals(0, s.howManyStartsWithPrefix(""));
    }
}
