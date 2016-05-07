package ch.jalu.wordeval.evaluation;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import org.apache.commons.lang3.StringUtils;

/**
 * Finds pairs of words that are equal to each other when reversed, e.g. German
 * "Lager" and "Regal".
 */
public class BackwardsPairs extends PartWordEvaluator implements PostEvaluator<WordCollector> {

  @Override
  public void processWord(String word, String rawWord) {
    // --
  }

  /**
   * Evaluate "backwards pairs" based on the collected words of a dictionary.
   * @param collector the word collector
   */
  @Override
  public void evaluateWith(WordCollector collector) {
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

  @Override
  public ExportObject toExportObject() {
    return toExportObject(ExportParams.builder()
        .topEntryMinimum(Optional.of(3.0))
        .build());
  }

  @Override public Class<WordCollector> getType() { return WordCollector.class; }

}