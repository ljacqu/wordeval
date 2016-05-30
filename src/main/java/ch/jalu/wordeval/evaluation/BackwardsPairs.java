package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.PartWordExport;
import ch.jalu.wordeval.evaluation.export.PartWordReducer;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Finds pairs of words that are equal to each other when reversed, e.g. German
 * "Lager" and "Regal".
 */
public class BackwardsPairs extends PostEvaluator<String, WordCollector> {

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
    return toExportObject(this.getClass().getSimpleName(), ExportParams.builder()
        .topEntryMinimum(Optional.of(3.0))
        .build());
  }

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return PartWordExport.create(identifier, getResults(), params, new PartWordReducer.ByLength());
  }

  @Override public Class<WordCollector> getType() { return WordCollector.class; }

}
