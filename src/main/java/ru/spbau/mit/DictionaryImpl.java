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
        final int MIN_INTEGER_ABS = 1 << 31;
        if (initialHash < 0) {
            initialHash += MIN_INTEGER_ABS;
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
        Bucket bucket = buckets[hashIndex]; // reference or copied value?

        try {
            return bucket.put(key, value);
        } catch (Bucket.BucketOverflowException e) {
            resize();
            return put(key, value);
        }
    }

    private Bucket[] newBuckets(int newBucketsNumber) {
        Bucket[] newBuckets = new Bucket[newBucketsNumber];
        for (int i = 0; i < newBucketsNumber; ++i) {
            newBuckets[i] = new Bucket();
        }

        size = 0;
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
        for (int newBucketsNumber = buckets.length * 2;; newBucketsNumber *= 2) {
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
        size = 0;
        buckets = newBuckets(buckets.length);
    }

    private static class Node {
        private final String trueKey;
        private String value;

        Node(String trueKey, String value) {
            this.trueKey = trueKey;
            this.value = value;
        }
    }

    private class Bucket {
        public class BucketOverflowException extends Exception {}
        private final Node[] values;
        private int trueSize;

        Bucket() {
            values = new Node[MAX_BUCKET_FILL_SIZE];
            trueSize = 0;
        }

        private int index(String key) {
            for (int i = 0; i < trueSize; i++) {
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

        private boolean isFull() {
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
            size++;
        }

        public String remove(String key) {
            String oldValue = get(key);

            if (oldValue == null) {
                return null;
            } else {
                int j = 0;
                for (int i = 0; i < trueSize; ++i) {
                    if (values[i].trueKey.equals(key)) {
                        continue;
                    }
                    values[j] = values[i]; // copy???
                    j++;
                }
                size--;
                trueSize--;
                values[trueSize] = null;
                return oldValue;
            }
        }
    }
}
