package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Getter;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;

/**
 * Evaluator base class. An evaluator checks words for a given property and adds
 * it to its collection if it was deemed relevant.
 * @param <K> the key the evaluator uses to store special words (typically: the
 *        sequences the evaluator identifies, or the word length)
 */
public abstract class Evaluator<K> {

  /** Collection of relevant words. */
  @Getter
  private Map<K, Set<String>> results = new HashMap<K, Set<String>>();

  /**
   * Processes a word and add it to results if it is relevant.
   * @param word the word to check (sanitized: trimmed, all lowercase)
   * @param rawWord the raw form of the word in the dictionary
   */
  public abstract void processWord(String word, String rawWord);

  /**
   * Converts the evaluator's results to an export object.
   * @return the converted ExportObject instance
   */
  public ExportObject toExportObject() {
    return toExportObject(this.getClass().getSimpleName(), null);
  }

  /**
   * Converts the evaluator's results to an export object.
   * @param identifier the identifier of the export object
   * @param params the export params; can be null
   * @return the converted ExportObject instance, or null if it should not be exported
   */
  protected abstract ExportObject toExportObject(String identifier, ExportParams params);

  /**
   * Gets the results of the evaluator as a navigable map.
   * @return a map with the results (key = property, entry = list of words)
   */
  public NavigableMap<K, List<String>> getNavigableResults() {
    NavigableMap<K, List<String>> cleanResult = new TreeMap<>();
    for (Map.Entry<K, Set<String>> entry : results.entrySet()) {
      cleanResult.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    return cleanResult;
  }

  /**
   * Returns the desired form of the word to process.
   * @return the word form
   */
  public WordForm getWordForm() {
    return WordForm.LOWERCASE;
  }

  /**
   * Adds an entry to the results map.
   * @param key the key for the new entry
   * @param word the word to add to the key's list
   */
  protected void addEntry(K key, String word) {
    if (results.get(key) == null) {
      // It is massively faster to instantiate a TreeSet here rather than
      // using a HashSet and then converting it to TreeSet
      results.put(key, new TreeSet<String>(String.CASE_INSENSITIVE_ORDER));
    }
    if (word.toLowerCase().equals(word) && results.get(key).contains(word)) {
      results.get(key).remove(word);
    }
    results.get(key).add(word);
  }

}
