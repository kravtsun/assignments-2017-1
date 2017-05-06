package ru.spbau.mit;

public class DictionaryImpl implements Dictionary {

    // how many elements at most can be stored in a bucket with one hash
    private static final int MAX_BUCKET_FILL_SIZE = 4;
    private static final int INITIAL_BUCKETS_NUMBER = 4;

    private Bucket[] buckets;
    private int size;

    public DictionaryImpl() {
        buckets = emptyBuckets(INITIAL_BUCKETS_NUMBER);
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean contains(String key) {
        if (key == null) {
            return false;
        }
        int hashIndex = bucketIndex(key);
        return buckets[hashIndex].contains(key);
    }

    public String get(String key) {
        if (key == null) {
            return null;
        }
        int hashIndex = bucketIndex(key);
        return buckets[hashIndex].get(key);
    }

    public String put(String key, String value) {
        if (key == null) {
            return null;
        }
        int hashIndex = bucketIndex(key);
        Bucket bucket = buckets[hashIndex];

        if (bucket.canPut(key)) {
            boolean containedBefore = bucket.contains(key);
            String oldValue = bucket.put(key, value);
            if (!containedBefore) {
                size++;
            }
            return oldValue;
        } else {
            resize();
            return put(key, value);
        }
    }

    public String remove(String key) {
        if (key == null) {
            return null;
        }
        int hashIndex = bucketIndex(key);
        Bucket bucket = buckets[hashIndex];
        boolean containedBefore = bucket.contains(key);
        String oldValue = bucket.remove(key);
        if (containedBefore) {
            size--;
        }
        return oldValue;
    }

    public void clear() {
        size = 0;
        buckets = emptyBuckets(buckets.length);
    }

    private static int bucketIndex(String key, int bucketsNumber) {
        int initialHash = key.hashCode();
        final int bitsInInteger = 32;
        final int minIntegerAbs = 1 << (bitsInInteger - 1);
        if (initialHash < 0) {
            initialHash += minIntegerAbs;
        }

        return initialHash % bucketsNumber;
    }

    private int bucketIndex(String key) {
        return bucketIndex(key, buckets.length);
    }

    private static Bucket[] emptyBuckets(int newBucketsNumber) {
        Bucket[] newBuckets = new Bucket[newBucketsNumber];
        for (int i = 0; i < newBucketsNumber; ++i) {
            newBuckets[i] = new Bucket();
        }
        return newBuckets;
    }

    private Bucket[] newBuckets(int newBucketsNumber) {
        Bucket[] newBuckets = emptyBuckets(newBucketsNumber);
        for (Bucket bucket : buckets) {
            for (int i = 0; i < bucket.size(); ++i) {
                int newBucketIndex = bucketIndex(bucket.values[i].key, newBucketsNumber);
                Bucket newBucket = newBuckets[newBucketIndex];

                if (!newBucket.canPut(bucket.values[i].key)) {
                    return null;
                }
                newBucket.add(bucket.values[i]);
            }
        }
        return newBuckets;
    }

    private void resize() {
        Bucket[] newBs = null;
        for (int newBucketsNumber = buckets.length * 2; newBs == null; newBucketsNumber *= 2) {
            newBs = newBuckets(newBucketsNumber);
        }
        buckets = newBs;
    }

    private static class Node {
        private final String key;
        private String value;

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private static class Bucket {
        private final Node[] values;

        Bucket() {
            values = new Node[MAX_BUCKET_FILL_SIZE];
        }

        public boolean contains(String key) {
            return index(key) != -1;
        }

        public String get(String key) {
            int i = index(key);
            return i == -1 ? null : values[i].value;
        }

        public int size() {
            for (int i = 0; i < MAX_BUCKET_FILL_SIZE; ++i) {
                if (values[i] == null) {
                    return i;
                }
            }
            return MAX_BUCKET_FILL_SIZE;
        }

        public boolean canPut(String key) {
            return index(key) != -1 || !isFull();
        }

        public String put(String key, String value) {
            int i = index(key);
            assert (canPut(key));
            if (i != -1) {
                String oldValue = values[i].value;
                values[i].value = value;
                return oldValue;
            }

            add(new Node(key, value));
            return null;
        }

        public void add(Node node) {
            values[size()] = node;
        }

        public String remove(String key) {
            String oldValue = null;
            int i = index(key);
            if (i != -1) {
                oldValue = values[i].value;
                int size = size();
                values[i] = values[size - 1];
                values[size - 1] = null;
                assert (size() == size - 1);
            }
            return oldValue;
        }

        private int index(String key) {
            int size = size();
            for (int i = 0; i < size; i++) {
                if (values[i].key.equals(key)) {
                    return i;
                }
            }
            return -1;
        }

        private boolean isFull() {
            return size() == MAX_BUCKET_FILL_SIZE;
        }
    }
}
