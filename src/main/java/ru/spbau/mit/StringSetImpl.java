package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StringSetImpl implements StringSet, StreamSerializable {
    private static final int BYTES_IN_INT = 4;
    private static final int BITS_IN_BYTE = 8;
    private Vertex root;

    public StringSetImpl() {
        root = null;
    }

    public boolean add(String element) {
        Vertex current = traverseWord(element, true);
        if (current.isTerminal) {
            return false;
        } else {
            current.isTerminal = true;
            while (current != null) {
                current.subTreeSize++;
                current = current.parent;
            }
            return true;
        }
    }

    public boolean contains(String element) {
        Vertex current = traverseWord(element, false);
        return current != null && current.isTerminal;
    }

    public boolean remove(String element) {
        Vertex current = traverseWord(element, false);

        if (current == null || !current.isTerminal) {
            return false;
        }

        current.isTerminal = false;
        for (int i = element.length() - 1; i >= 0; --i) {
            removeOnEmpty(current, element.charAt(i));
            current = current.parent;
        }

        root.subTreeSize--;
        if (root.subTreeSize == 0) {
            root = null;
        }

        return true;
    }

    public int size() {
        return root == null ? 0 : root.subTreeSize;
    }

    public int howManyStartsWithPrefix(String prefix) {
        Vertex current = traverseWord(prefix, false);
        return current == null ? 0 : current.subTreeSize;
    }

    public void serialize(OutputStream out) throws SerializationException {
        Vertex.serializeVertex(root, out);
    }

    public void deserialize(InputStream in) throws SerializationException {
        root = Vertex.deserializeVertex(in, null);
    }

    private Vertex traverseWord(String element, boolean addIfNotExists) {
        if (root == null) {
            if (addIfNotExists) {
                root = new Vertex(null); // root only.
            } else {
                return null;
            }
        }

        Vertex current = root;

        for (int i = 0; i < element.length(); ++i) {
            char c = element.charAt(i);
            int stepCharIndex = Vertex.stepCharIndex(c);
            if (current.next[stepCharIndex] == null) {
                if (addIfNotExists) {
                    current.next[stepCharIndex] = new Vertex(current);
                } else {
                    return null;
                }
            }
            current = current.next[stepCharIndex];
        }
        return current;
    }

    private void removeOnEmpty(Vertex current, char stepChar) {
        current.subTreeSize--;
        if (current.subTreeSize == 0 && current.parent != null) {
            int stepCharIndex = Vertex.stepCharIndex(stepChar);
            current.parent.next[stepCharIndex] = null;
        }
    }

    private static void booleanSerialize(boolean b, OutputStream out) throws SerializationException {
        try {
            if (b) {
                out.write(1);
            } else {
                out.write(0);
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private static void intSerialize(int num, OutputStream out) throws SerializationException {
        final int byteMask = 0xFF;
        try {
            for (int i = 0; i < BYTES_IN_INT; ++i) {
                out.write(num & byteMask);
                num >>= BITS_IN_BYTE;
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private static boolean booleanDeserialize(InputStream in) throws SerializationException {
        try {
            int i = in.read();
            if (i != 1 && i != 0) {
                throw new SerializationException();
            }
            return i == 1;
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private static int intDeserialize(InputStream in) throws SerializationException {
        try {
            int num = 0;
            for (int i = 0; i < BYTES_IN_INT; ++i) {
                final int readNum = in.read();
                num |= readNum << (i * BITS_IN_BYTE);
            }
            return num;
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    private static class Vertex implements StreamSerializable {
        private static final int CHAR_POWER = 2 * 26;
        private static final int VERTEX_MAGIC = 0xAABBCCDD;
        private static final int EMPTY_VERTEX_MAGIC = 0xDDCCBBAA;
        private final Vertex[] next;
        private boolean isTerminal;
        private Vertex parent;
        private int subTreeSize;

        Vertex(Vertex parent) {
            isTerminal = false;
            next = new Vertex[CHAR_POWER];
            this.parent = parent;
            subTreeSize = 0;
        }

        public static void serializeVertex(Vertex v, OutputStream out) throws SerializationException {
            if (v == null) {
                intSerialize(EMPTY_VERTEX_MAGIC, out);
            } else {
                v.serialize(out);
            }
        }

        // Question: constructor would be better?
        public static Vertex deserializeVertex(InputStream in, Vertex parent) throws SerializationException {
            final int magic = intDeserialize(in);
            if (magic == EMPTY_VERTEX_MAGIC) {
                return null;
            } else if (magic == VERTEX_MAGIC) {
                Vertex v = new Vertex(parent);
                v.deserialize(in);
                return v;
            } else {
                throw new SerializationException();
            }
        }

        public static int stepCharIndex(char stepChar) {
            if (Character.isLowerCase(stepChar)) {
                return (int) stepChar - 'a';
            } else {
                return (CHAR_POWER / 2) + (int) (stepChar - 'A');
            }
	    }

        public void serialize(OutputStream out) throws SerializationException {
            intSerialize(VERTEX_MAGIC, out);
            for (int i = 0; i < next.length; ++i) {
                if (next[i] == null) {
                    intSerialize(EMPTY_VERTEX_MAGIC, out);
                } else {
                    next[i].serialize(out);
                }
            }
            booleanSerialize(isTerminal, out);
            intSerialize(subTreeSize, out);
        }

        public void deserialize(InputStream in) throws SerializationException {
//            final int magic = intDeserialize(in);
//            if (magic != VERTEX_MAGIC) {
//                throw new SerializationException();
//            }

            for (int i = 0; i < next.length; ++i) {
                next[i] = deserializeVertex(in, this);
            }

            isTerminal = booleanDeserialize(in);
            subTreeSize = intDeserialize(in);
        }
    }
}
