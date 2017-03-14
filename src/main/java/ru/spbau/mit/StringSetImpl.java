package ru.spbau.mit;

public class StringSetImpl implements StringSet
{
    private Vertex root;

    public StringSetImpl() {
        root = null;
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
            if (current.next[c] == null) {
                if (addIfNotExists) {
                    current.next[c] = new Vertex(current);
                } else {
                    return null;
                }
            }
            current = current.next[c];
        }
        return current;
    }

    /**
     * Expected complexity: O(|element|)
     *
     * @param element
     * @return <tt>true</tt> if this set did not already contain the specified
     * element
     */
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

    /**
     * Expected complexity: O(|element|)
     *
     * @param element
     */
    public boolean contains(String element) {
        Vertex current = traverseWord(element, false);
        return current != null && current.isTerminal;
    }

    private void removeOnEmpty(Vertex current, char stepChar) {
        current.subTreeSize--;
        if (current.subTreeSize == 0 && current.parent != null) {
            current.parent.next[stepChar] = null;
        }
//        else {
//            assert false;
//        }
    }

    /**
     * Expected complexity: O(|element|)
     *
     * @param element
     * @return <tt>true</tt> if this set contained the specified element
     */
    public boolean remove(String element) {
        Vertex current = traverseWord(element, false);

        if (current == null || !current.isTerminal) {
            return false;
        }

        current.isTerminal = false;
        for (int i = element.length()-1; i >= 0; --i) {
            removeOnEmpty(current, element.charAt(i));
            current = current.parent;
        }

//        assert(current == root);
        root.subTreeSize--;
        if (root.subTreeSize == 0) {
            root = null;
        }

        return true;
    }

    /**
     * Expected complexity: O(1)
     */
    public int size() {
        return root == null? 0 : root.subTreeSize;
    }

    /**
     * Expected complexity: O(|prefix|)
     *
     * @param prefix
     */
    public int howManyStartsWithPrefix(String prefix) {
        Vertex current = traverseWord(prefix, false);
        return current == null? 0 : current.subTreeSize;
    }

    private class Vertex {
        private final int CHAR_POWER = 2*256;
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
    }
}
