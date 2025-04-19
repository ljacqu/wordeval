package ch.jalu.wordeval.appdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores objects by a key. Throws an exception if an object tries to be stored with
 * the same key twice, or if an object is retrieved for a key that does not have an
 * associated object.
 */
abstract class ObjectStore<K, V> {

  protected final Map<K, V> entries = new HashMap<>();

  /**
   * Adds all given objects to the store.
   *
   * @param objects the objects to add
   */
  @SafeVarargs
  public final void addAll(V... objects) {
    for (V object : objects) {
      add(object);
    }
  }

  /**
   * Adds the given object to the store.
   *
   * @param object the object to store
   */
  private void add(V object) {
    K key = getKey(object);
    if (entries.containsKey(key)) {
      throw new IllegalStateException("An object for key '" + key + "' has already been stored");
    }
    entries.put(key, object);
  }

  /**
   * Returns the object for the given key or throws an exception if there is no such entry.
   *
   * @param key the key whose object should be retrieved
   * @return the object
   */
  public V get(K key) {
    V value = entries.get(key);
    if (value == null) {
      throw new IllegalStateException(
          "No entry has been stored for key '" + key + "' (" + getClass().getSimpleName() + ")");
    }
    return value;
  }

  /**
   * Returns all keys that are in the store.
   *
   * @return the key set
   */
  public Set<K> keySet() {
    return entries.keySet();
  }

  /**
   * Returns the key based on the object.
   *
   * @param object the object to process
   * @return the key
   */
  protected abstract K getKey(V object);

}
