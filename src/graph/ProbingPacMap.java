package graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A map with keys of type 'K' and values of type `V`, implemented using a hash table with linear
 * probing.
 */
public class ProbingPacMap<K, V> implements PacMap<K, V> {
    /**
     * Represents an association of a key `key` (of type `K`) with a value `value` (of type `V`).
     */
    private record Entry<K, V>(K key, V value) {
    }

    /**
     * Represents a tombstone. If an entry at index `i` is removed, element `i` will be replaced
     * by a reference to this object. Tombstones count toward the load factor, and are cleared when
     * the hash table is resized.
     */
    @SuppressWarnings("rawtypes")
    private static final Entry TOMBSTONE = new Entry<>(null, null);

    /**
     * The initial capacity of the hash table for new instances of `ProbingPacMap`.
     */
    private static final int INITIAL_CAPACITY = 16;

    /**
     * The maximum load factor (inclusive) that is allowed in the `entries` hash table. If the load
     * factor ever exceeds this maximum, then the hash table length must be immediately doubled to
     * reduce the load factor. Must have `0 < maxLoadFactor < 1`.
     */
    public static final double MAX_LOAD_FACTOR = 0.5;

    /**
     * The probing hash table backing this map. Indices (i.e., buckets) that don't currently store an
     * entry (possibly a TOMBSTONE) are `null`. If this map contains an entry with a key whose hash
     * code maps to index `i`, then the (unique) entry containing that key is reachable via linear
     * search starting at index `i` (wrapping around the array if necessary) without encountering
     * `null`.
     */
    private Entry<K, V>[] entries;

    /**
     * The number of actual key, value pairs within 'entries'.
     * This must be greater than or equal to 0 and less than or equal to the number of non-null buckets.
     */
    private int size;

    /**
     * The number of non-null buckets within 'entries'
     * This must be greater than or equal to 0 and less than or equal to the number of non-null buckets.
     */
    private int storedItems;


    /**
     * Create a new empty `ProbingPacMap`.
     */
    @SuppressWarnings("unchecked")
    public ProbingPacMap() {
        entries = new Entry[INITIAL_CAPACITY];
        storedItems = 0;
        size = 0;
    }

    /**
     * Returns the number of keys currently associated with values in this map. Runs in O(1) time.
     */
    @Override
    public int size() {
        return size;

    }

    /**
     * Returns the current load factor of the hash table backing this map. Runs in O(1) time.
     */
    private double loadFactor() {
        return (double) storedItems / entries.length;
    }


    /**
     * If `key` is a key in this map, return the index in `entries` for this key. Otherwise, returns
     * the first index of a `null` or tombstone entry in the table at or after the index
     * corresponding to the key's hash code (wrapping around).
     */
    private int findEntry(K key) {
        int capacity = entries.length;
        int index = Math.floorMod(key.hashCode(), capacity);
        int tombstone = -1;

        for (int i = 0; i < capacity; i++) {
            int probe = Math.floorMod(index + i, capacity);
            Entry<K, V> entry = entries[probe];

            if (entry == null) {
                return (tombstone != -1) ? tombstone : probe;
            } else if (entry == TOMBSTONE) {
                if (tombstone == -1) {
                    tombstone = probe;
                }
            } else if (entry.key().equals(key)) {
                return probe;
            }
        }
        return tombstone;

    }

    @Override
    public boolean containsKey(K key) {
        int index = findEntry(key);
        Entry<K, V> entry = entries[index];
        return entry != null && entry != TOMBSTONE;
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            throw new NoSuchElementException();
        }
        int index = findEntry(key);
        Entry<K, V> entry = entries[index];
        return entry.value();

    }

    /**
     * Helper method: Doubles the table capacity and re-hashes all existing
     * entries. This method is called by put() when the load factor is exceeded.
     * Tombstones are cleared during this process.
     */
    private void resize() {
        int oldCapacity = entries.length;
        Entry<K, V>[] oldEntries = entries;

        int newCapacity = oldCapacity * 2;
        entries = new Entry[newCapacity];
        size = 0;
        storedItems = 0;

        for (Entry<K, V> entry : oldEntries) {
            if (entry != null && entry != TOMBSTONE) {
                int newIndex = findEntry(entry.key);
                entries[newIndex] = entry;
                size++;
                storedItems++;
            }
        }
    }

    @Override
    public void put(K key, V value) {
        int index = findEntry(key);
        Entry<K, V> entry = entries[index];

        if (entry != null && entry != TOMBSTONE && entry.key().equals(key)) {
            entries[index] = new Entry<>(key, value);
            return;
        }

        entries[index] = new Entry<>(key, value);
        size++;
        storedItems++;

        if (loadFactor() > MAX_LOAD_FACTOR) {
            resize();
        }

    }


    @Override
    @SuppressWarnings("unchecked")
    public V remove(K key) {

        if (!(containsKey(key))) {
            throw new NoSuchElementException();
        }

        int index = findEntry(key);
        Entry<K, V> entry = entries[index];
        V oldValue = entry.value;
        entries[index] = TOMBSTONE;
        size--;
        return oldValue;

    }

    @Override
    public Iterator<K> iterator() {
        return new ProbingPacMapIterator();
    }

    /**
     * An iterator over the keys in this hash table. This map must not be structurally
     * modified while any such iterators are alive.
     */
    private class ProbingPacMapIterator implements Iterator<K> {

        /**
         * The index of the entry in `entries` containing the next value to yield, or
         * `entries.length` if all values have been yielded.
         */
        private int iNext;

        /**
         * Create a new iterator over this dictionary's keys.
         */
        ProbingPacMapIterator() {
            iNext = 0;
            findNext();
        }

        /**
         * Set `iNext` to the first index `i` not less than the current value of `iNext` such that
         * `entries[i] != null` and 'entries[i] != TOMBSTONE', or set it to `entries.length` if
         * there are no remaining non-null and non-tombstone entries.  Note that if `iNext` is
         * already the index of a non-null and non-tombstone entry, then it will not be changed.
         */
        private void findNext() {
            while (iNext < entries.length && (entries[iNext] == null || entries[iNext] == TOMBSTONE)) {
                iNext += 1;
            }
        }

        @Override
        public boolean hasNext() {
            return iNext < entries.length;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            K ans = entries[iNext].key;
            iNext += 1;
            findNext();
            return ans;
        }
    }
}