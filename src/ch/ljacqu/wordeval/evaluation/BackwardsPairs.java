package ch.ljacqu.wordeval.evaluation;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

/**
 * Finds pairs of words that are equal to each other when reversed, e.g. German
 * "Lager" and "Regal".
 */
public class BackwardsPairs extends PartWordEvaluator {

  @Override
  public void processWord(String word, String rawWord) {
    // --
  }

  /**
   * Evaluate "backwards pairs" based on the collected words of a dictionary.
   * @param collector the word collector
   */
  @PostEvaluator
  public void postEvaluate(WordCollector collector) { 
    Set<String> words = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    words.addAll(collector.returnSortedWords());

    for (String word : words) {
      String wordToLower = word.toLowerCase();
      String reversed = StringUtils.reverse(wordToLower);
      if (wordToLower.compareTo(reversed) < 0
          && words.contains(reversed)) {
        addEntry(wordToLower, reversed);
      }
    }
  }

}
