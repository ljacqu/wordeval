package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.language.WordForm;

/**
 * Evaluator base class. An evaluator checks words for a given property and adds
 * it to its collection if it was deemed as relevant.
 * @param <K> The key the evaluator uses to store special words (typically: the
 *        sequences the evaluator identifies, or the word length)
 */
public abstract class Evaluator<K> {

  /** Collection of relevant words. */
  protected NavigableMap<K, List<String>> results = new TreeMap<K, List<String>>();

  /**
   * Processes a word and add it to results if it is relevant.
   * @param word The word to check (sanitized: trimmed, all lowercase)
   * @param rawWord The raw form of the word in the dictionary
   */
  public abstract void processWord(String word, String rawWord);

  /**
   * Converts the evaluator's results to an export object.
   * @return The converted ExportObject instance
   */
  public ExportObject toExportObject() {
    return toExportObject(this.getClass().getSimpleName(), null);
  }

  /**
   * Converts the evaluator's results to an export object.
   * @param identifier The identifier of the export object
   * @param topEntries The number of entries to keep non-aggregated
   * @return The converted ExportObject instance
   */
  protected abstract ExportObject toExportObject(String identifier,
      Integer topEntries);

  /**
   * Gets the results of the evaluator.
   * @return A map with the results (key = property, entry = list of values)
   */
  public NavigableMap<K, List<String>> getResults() {
    return Collections.unmodifiableNavigableMap(results);
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
   * @param word The word to add to the key's list
   */
  protected void addEntry(K key, String word) {
    if (results.get(key) == null) {
      results.put(key, new ArrayList<String>());
    }
    results.get(key).add(word);
  }

  /**
   * Outputs the evaluator's results for debug / quick viewing purposes.
   */
  public void outputAggregatedResult() {
    for (Entry<K, List<String>> entry : results.entrySet()) {
      outputEntry(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Outputs an entry of the results map.
   * @param key The key in the results map
   * @param entry The list of entries to output
   */
  protected void outputEntry(K key, List<String> entry) {
    if (entry.size() > 50) {
      System.out.println(key + ": " + entry.size());
    } else {
      System.out.println(key + ": " + entry);
    }
  }

}
