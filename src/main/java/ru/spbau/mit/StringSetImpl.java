package ru.spbau.mit;

public class StringSetImpl implements StringSet {
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

    private static class Vertex {
        private static final int CHAR_POWER = 2 * 26;
        private final Vertex[] next;
        private boolean isTerminal;
        private final Vertex parent;
        private int subTreeSize;

        Vertex(Vertex parent) {
            isTerminal = false;
            next = new Vertex[CHAR_POWER];
            this.parent = parent;
            subTreeSize = 0;
        }

        public static int stepCharIndex(char stepChar) {
            if (Character.isLowerCase(stepChar)) {
                return (int) stepChar - 'a';
            } else {
                return (CHAR_POWER / 2) + (int) (stepChar - 'A');
            }
        }
    }
}
