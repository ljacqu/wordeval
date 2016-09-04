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
 * Finds emordnilaps, words that produce another word when reversed,
 * such as German "Lager" and "Regal".
 */
public class Emordnilap extends PostEvaluator<String, WordCollector> {

  @Override
  public void evaluateWith(WordCollector collector) {
    Set<String> words = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    words.addAll(collector.returnSortedWords());

    for (String word : words) {
      String wordToLower = word.toLowerCase();
      String reversed = StringUtils.reverse(wordToLower);
      if (wordToLower.compareTo(reversed) < 0 && words.contains(reversed)) {
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
