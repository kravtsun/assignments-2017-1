package ru.spbau.mit;



public class DictionaryImpl implements Dictionary {

    // how many elements at most can be stored in a bucket with one hash
    private static final int MAX_BUCKET_FILL_SIZE = 4;
    private static final int INITIAL_BUCKETS_NUMBER = 4;

    private Bucket[] buckets;
    private int size;

    public DictionaryImpl() {
        buckets = new Bucket[INITIAL_BUCKETS_NUMBER];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }

        size = 0;
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

    private int hashCodeBucketIndex(String key) {
        return bucketIndex(key, buckets.length);
    }

    public int size() {
        return size;
    }

    public boolean contains(String key) {
        String value = get(key);
        return value != null;
    }

    public String get(String key) {
        int hashIndex = hashCodeBucketIndex(key);
        return buckets[hashIndex].get(key);
    }

    public String put(String key, String value) {
        int hashIndex = hashCodeBucketIndex(key);
        Bucket bucket = buckets[hashIndex];

        try {
            String oldValue = bucket.put(key, value);
            if (oldValue == null) {
                size++;
            }
            return oldValue;
        } catch (Bucket.BucketOverflowException e) {
            resize();
            return put(key, value);
        }
    }

    public String remove(String key) {
        int hashIndex = hashCodeBucketIndex(key);
        Bucket bucket = buckets[hashIndex];
        String oldValue = bucket.remove(key);
        if (oldValue != null) {
            size--;
        }
        return oldValue;
    }

    public void clear() {
        size = 0;
        buckets = emptyBuckets(buckets.length);
    }

    private Bucket[] emptyBuckets(int newBucketsNumber) {
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
                try {
                    newBucket.add(bucket.values[i]);
                } catch (Bucket.BucketOverflowException e) {
                    return null;
                }
            }
        }
        return newBuckets;
    }

    private void resize() {
        for (int newBucketsNumber = buckets.length * 2;; newBucketsNumber *= 2) {
            Bucket[] newBs = newBuckets(newBucketsNumber);
            if (newBs != null) {
                buckets = newBs;
                return;
            }
        }
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
        public class BucketOverflowException extends Exception {}
        private final Node[] values;

        Bucket() {
            values = new Node[MAX_BUCKET_FILL_SIZE];
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

        public String get(String key) {
            int i = index(key);
            if (i == -1) {
                return null;
            } else {
                return values[i].value;
            }
        }

        int size() {
            for (int i = 0; i < MAX_BUCKET_FILL_SIZE; ++i) {
                if (values[i] == null) {
                    return i;
                }
            }
            return MAX_BUCKET_FILL_SIZE;
        }

        private boolean isFull() {
            return size() == MAX_BUCKET_FILL_SIZE;
        }

        public String put(String key, String value) throws BucketOverflowException {
            int i = index(key);
            if (i != -1) {
                String oldValue = values[i].value;
                values[i].value = value;
                return oldValue;
            }

            add(new Node(key, value));

            return null;
        }

        public void add(Node node) throws BucketOverflowException {
            if (isFull()) {
                throw new BucketOverflowException();
            }

            values[size()] = node;
        }

        public String remove(String key) {
            String oldValue = get(key);

            if (oldValue == null) {
                return null;
            } else {
                int j = 0;
                int size = size();

                for (int i = 0; i < size; ++i) {
                    if (!values[i].key.equals(key)) {
                        values[j++] = values[i];
                    }
                }

                values[size-1] = null;
                return oldValue;
            }
        }
    }
}
