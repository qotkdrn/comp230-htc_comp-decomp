// Summary: Interface for a simple Hashtable.
// Authors: Alec Henning, Alex Bae
// Date: 12/4/2021

public interface KWHashMap<K, V> {

    V get(Object key);

    boolean isEmpty();

    V put(K key, V value);

    V remove(Object key);

    int size();

}