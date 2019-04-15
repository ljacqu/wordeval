package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Evaluator base class. An evaluator checks words for a given property and adds
 * it to its collection if it was deemed relevant.
 * @param <K> the key the evaluator uses to store special words (typically: the
 *        sequences the evaluator identifies, or the word length)
 */
@Deprecated
public abstract class Evaluator<K extends Comparable> {

  /** Collection of relevant words. */
  @Getter
  private Multimap<K, String> results = TreeMultimap.create();

  /**
   * Converts the evaluator's results to an export object.
   *
   * @return the converted ExportObject instance, or null if it should not be exported
   */
  public ExportObject toExportObject() {
    return toExportObject(this.getClass().getSimpleName(), null);
  }

  /**
   * Converts the evaluator's results to an export object.
   *
   * @param identifier the identifier of the export object
   * @param params the export params; can be null
   * @return the converted ExportObject instance, or null if it should not be exported
   */
  protected abstract ExportObject toExportObject(String identifier, ExportParams params);

  /**
   * Gets the results of the evaluator as a navigable map.
   *
   * @return a map with the results (key = property, entry = list of words)
   */
  public NavigableMap<K, List<String>> getNavigableResults() {
    NavigableMap<K, List<String>> cleanResult = new TreeMap<>();
    for (Map.Entry<K, Collection<String>> entry : results.asMap().entrySet()) {
      cleanResult.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    return cleanResult;
  }

  /**
   * Adds an entry to the results map.
   *
   * @param key the key for the new entry
   * @param word the word to add to the key's list
   */
  protected void addEntry(K key, String word) {
    results.put(key, word);
  }

  /**
   * Post-processing step to remove words which are present multiple times in the evaluator's results.
   * This happens when the same word exists in the dictionary with different casing, e.g. "test" vs. "Test".
   *
   * @param locale The locale to use to make words lower-case
   */
  public void filterDuplicateWords(Locale locale) {
    for (Map.Entry<K, Collection<String>> entry : results.asMap().entrySet()) {
      Collection<String> resultList = entry.getValue();

      List<String> wordsToRemove = new ArrayList<>();
      Map<String, String> lowerCaseWords = new HashMap<>(resultList.size());
      for (String word : resultList) {
        String duplicateWord = checkWordForDuplicate(lowerCaseWords, locale, word);
        if (duplicateWord != null) {
          wordsToRemove.add(duplicateWord);
        }
      }

      for (String word : wordsToRemove) {
        resultList.remove(word);
      }
    }
  }

  /**
   * Checks the given Map if the word has already been passed. If so, returns the variant to remove from
   * the result by preferring all lower-case words. For instance, if "Test" is passed, it is added to the
   * map as "test" -> "Test", and then if "test" is passed, "Test" will be returned to be deleted from
   * the results.
   *
   * @param lowerCaseWords The list of traversed words, where the key is the lower-case form of the actual word
   * @param locale The locale to use to make words to lower-case
   * @param word The word to process
   * @return The word to delete if applicable, {@code null} otherwise
   */
  private static String checkWordForDuplicate(Map<String, String> lowerCaseWords, Locale locale, String word) {
    String lowerCase = word.toLowerCase(locale);
    if (lowerCaseWords.containsKey(lowerCase)) {
      if (lowerCase.equals(word)) {
        String oldWord = lowerCaseWords.get(lowerCase);
        lowerCaseWords.put(lowerCase, word);
        return oldWord;
      } else {
        return word;
      }
    } else {
      lowerCaseWords.put(lowerCase, word);
      return null;
    }
  }

}
