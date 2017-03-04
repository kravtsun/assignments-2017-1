package ru.spbau.mit;

import java.lang.Exception;

public class DictionaryImpl implements Dictionary {

    // how many elements at most can be stored in a bucket with one hash
    final private static int MAX_BUCKET_FILL_SIZE = 4;
    final private static int INITIAL_BUCKETS_NUMBER = 4;

    private static class Node {
        final private String trueKey;
        private String value;

        public static Node empty() {
            return new Node(null, null);
        }

        public Node(String trueKey, String value) {
            this.trueKey = trueKey;
            this.value = value;
        }
    }

    private class Bucket {
        public class BucketOverflowException extends Exception {}
        final private Node[] values;
        private int trueSize;

        public Bucket() {
            values = new Node[MAX_BUCKET_FILL_SIZE];
            for (int i = 0; i < values.length; ++i) {
                values[i] = Node.empty();
            }
            trueSize = 0;
        }

        public int index(String key) {
            for (int i = 0; i < trueSize; i++) {
//                if (values[i].trueKey == null) break;
                if (values[i].trueKey.equals(key)) {
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

        public boolean isFull() {
            return trueSize == MAX_BUCKET_FILL_SIZE;
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
            values[trueSize++] = node;
            fullSize++;
        }

        public String remove(String key) {
            String oldValue = get(key);

            if (oldValue == null) {
                return null;
            } else {
                int j = 0;
                for (int i = 0; i < trueSize; ++i) {
                    if (values[i].trueKey.equals(key)) continue;
                    values[j] = values[i]; // copy???
                    j++;
                }
                fullSize--;
                trueSize--;
                values[trueSize] = Node.empty();
                return oldValue;
            }
        }
    }

    private Bucket[] buckets;
    private int fullSize;

    public DictionaryImpl() {
        buckets = new Bucket[INITIAL_BUCKETS_NUMBER];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }

        fullSize = 0;
    }

    private int bucketIndex(String key, int bucketsNumber) {
        int initialHash = key.hashCode();
        if (initialHash < 0) {
            initialHash += 1 << 31;
        }

        return initialHash % bucketsNumber;
    }
    private int hashCodeBucketIndex(String key) {
        return bucketIndex(key, buckets.length);
    }

    public int size() {
        return fullSize;
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
//        String gotValue = get(key);
        int hashIndex = hashCodeBucketIndex(key);
        Bucket bucket = buckets[hashIndex]; // reference or copied value?

        try {
            String oldValue = bucket.put(key, value);
            if (oldValue != null) {
                return oldValue;
            }
        } catch (Bucket.BucketOverflowException e) {
            resize();
            return put(key, value);
        }
        return null;
    }

    private Bucket[] newBuckets(int newBucketsNumber) {
        Bucket[] newBuckets = new Bucket[newBucketsNumber];
        for (int i = 0; i < newBucketsNumber; ++i) {
            newBuckets[i] = new Bucket();
        }

        fullSize = 0;
        for (Bucket bucket : buckets) {
            for (int i = 0; i < bucket.trueSize; i++) {
                int newBucketIndex = bucketIndex(bucket.values[i].trueKey, newBucketsNumber);
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
        for (int newBucketsNumber = buckets.length * 2; ; newBucketsNumber *= 2) {
            Bucket[] newBs = newBuckets(newBucketsNumber);
            if (newBs != null) {
                buckets = newBs;
                return;
            }
        }
    }

    public String remove(String key) {
        int hashIndex = hashCodeBucketIndex(key);
        Bucket bucket = buckets[hashIndex];
        return bucket.remove(key);
    }

    public void clear() {
        fullSize = 0;
    }
}
