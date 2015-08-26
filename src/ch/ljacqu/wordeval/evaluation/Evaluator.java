package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ch.ljacqu.wordeval.language.WordForm;

/**
 * Evaluator base class. An evaluator checks words for a given property and adds
 * it to its collection if it was deemed as relevant.
 * @param <K> The key the evaluator uses
 * @param <V> The value the evaluator uses
 */
public abstract class Evaluator<K, V> {

  /** Collection of relevant words. */
  protected Map<K, List<V>> results = new HashMap<K, List<V>>();

  /**
   * Processes a word and add it to results if it is relevant.
   * @param word The word to check (sanitized: trimmed, all lowercase)
   * @param rawWord The raw form of the word in the dictionary
   */
  public abstract void processWord(String word, String rawWord);

  /**
   * Gets the results of the evaluator.
   * @return A map with the results (key = property, entry = list of values)
   */
  public Map<K, List<V>> getResults() {
    return results;
  }

  /**
   * Returns the desired form of the word to process.
   * @return The word form
   */
  public WordForm getWordForm() {
    return WordForm.LOWERCASE;
  }

  /**
   * Adds an entry to the results map.
   * @param key The key for the new entry
   * @param entry The entry to add for the key
   */
  protected void addEntry(K key, V entry) {
    if (results.get(key) == null) {
      results.put(key, new ArrayList<V>());
    }
    results.get(key).add(entry);
  }

  /**
   * Outputs the evaluator's results.
   */
  public void outputAggregatedResult() {
    for (Entry<K, List<V>> entry : results.entrySet()) {
      outputEntry(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Outputs an entry of the results map.
   * @param key The key in the results map
   * @param entry The list of entries to output
   */
  protected void outputEntry(K key, List<V> entry) {
    if (entry.size() > 50) {
      System.out.println(key + ": " + entry.size());
    } else {
      System.out.println(key + ": " + entry);
    }
  }

}
