
// Summary: Hash table implementation using chaining.
// Authors: Alec Henning, Alex Bae
// Date: 12/4/2021

import java.util.LinkedList;

public class HashTableChain<K, V> implements KWHashMap<K, V> {

    // nested class
    public static class Entry<K, V> {

        /** The key */
        private final K key;
        /** The value */
        private V value;

        /**
         * Creates a new key-value pair
         * 
         * @param key   The key
         * @param value The value
         */
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Retrieves the key
         * 
         * @return key;
         */
        public K getKey() {
            return key;
        }

        /**
         * Retrieves the value
         * 
         * @return The value
         */
        public V getValue() {
            return value;
        }

        /**
         * Sets the value
         * 
         * @param val The new value
         * @return The old value
         */
        public V setValue(V val) {
            V oldValue = value;
            value = val;
            return oldValue;
        }

    }

    private LinkedList<Entry<K, V>>[] table;
    private int numKeys;
    private int rehashCount = 0;
    private static final int CAPACITY = 101;
    private static final double LOAD_FACTOR = 15; // threshold was 3

    // Constructor
    @SuppressWarnings("unchecked")
    public HashTableChain() {
        table = new LinkedList[CAPACITY];
        numKeys = 0;
    }

    // user defined capacity Constructor
    @SuppressWarnings("unchecked")
    public HashTableChain(int C) {
        table = new LinkedList[C];
        numKeys = 0;
    }

    // @Override
    // public int hashCode() {
    // int hash = 5;
    // String temp = key;

    /**
     * Method get for class HashtableChain.
     * 
     * @param key The key being sought
     * @return The value associated with this key if found; otherwise, null
     */
    @Override
    public V get(Object key) {
        int index = key.hashCode() % table.length;
        if (index < 0)
            index += table.length;

        if (table[index] == null)
            return null;

        for (Entry<K, V> e : table[index]) {
            if (e.getKey().equals(key))
                return e.getValue();
        }
        // Assert : key is not in the table
        return null;
    }

    /**
     * Checks whether or not the table is empty
     * 
     * @return true if the table is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        if (numKeys == 0)
            return true;
        else
            return false;
    }

    /**
     * Method put for class HashtableChain.
     * 
     * @post This key value pair is inserted in the table and numKeys is
     *       incremented. If the key is already in the table, its value is changed
     *       to the argument value and numKeys is not changed.
     * 
     * @param key   The key of item being inserted
     * @param value The value for this key
     * @return The old value associated with this key if found; otherwise, null
     */
    @Override
    public V put(K key, V value) {
        int index = key.hashCode() % table.length;

        // if index is less than 0 add table length
        if (index < 0)
            index += table.length;

        // if table[index] is null create a new linked list at table[index]
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }

        for (Entry<K, V> e : table[index]) {
            // if the search is successful, replace the old value
            if (e.getKey().equals(key)) {
                // replace value for this key
                V oldVal = e.getValue();
                e.setValue(value);
                return oldVal;
            }
        }

        // Assert: the key is not in the LinkedList
        table[index].addFirst(new Entry<>(key, value));
        numKeys++;
        // System.out.println(numKeys + " " + (LOAD_FACTOR * table.length)); //
        // testing purposes
        if (numKeys > (LOAD_FACTOR * table.length)) // threshold
            rehash();
        return null;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        // System.out.println("Rehashing"); // testing purposes
        // save a reference to oldTable
        rehashCount++;
        LinkedList<Entry<K, V>>[] oldTable = table;

        int newSize = getNextPrime(2 * table.length);

        table = new LinkedList[newSize];

        numKeys = 0;
        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null) {
                for (Entry<K, V> e : oldTable[i]) {
                    put(e.getKey(), e.getValue());
                }
            }
        }

    }

    /**
     * Searchs the table for the Entry with the given key, removes the key if it
     * exists.
     * 
     * @param key the key to search for
     * @return the Value associated with the given key
     */
    @Override
    public V remove(Object key) {

        int index = key.hashCode() % table.length;

        if (index < 0) {
            index += table.length;
        }

        if (table[index] == null) {
            return null;
        }

        for (Entry<K, V> e : table[index]) {
            if (e.getKey().equals(key)) {
                V delVal = e.getValue();
                table[index].remove();
                numKeys--;
                if (table[index].size() == 0) {
                    table[index] = null;
                }
                return delVal;
            }
        }
        // Assert: the key is not in the table
        return null;
    }

    /**
     * Method for retrieving the size of the table
     * 
     * @return the size of the table
     */
    @Override
    public int size() {
        return numKeys;
    }

    /**
     * Method for retrieving the number of times the table has been rehashed.
     * 
     * @return the number of times the table has been rehashed
     */
    public int rehashCount() {
        return rehashCount;
    }

    /**
     * Checks if the number is a prime number
     * 
     * @param number The number to be checked
     * @return true if the number is prime, false otherwise
     */

    public static boolean isPrime(int number) {
        if (number % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loops through numbers starting at the number sent in and returns the closest
     * prime number
     * 
     * @param numberToCheck the number to start checking from
     * @return the closest prime number
     */
    public int getNextPrime(int numberToCheck) {
        for (int i = numberToCheck; true; i++) {
            if (isPrime(i)) {
                return i;
            }
        }
    }

}