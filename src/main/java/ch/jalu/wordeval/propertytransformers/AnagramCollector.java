package ch.jalu.wordeval.propertytransformers;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluation.export.ExportParams;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

/**
 * Collects anagram groups (e.g. "acre", "care", "race").
 */
public class AnagramCollector implements PropertyFinder<String> {

  @Override
  public List<String> findProperties(Word word) {
    String text = word.noAccentsWordCharsOnly();
    char[] chars = text.toCharArray();
    Arrays.sort(chars);
    return singletonList(new String(chars));
  }

  // TODO .
  public ExportParams buildExportParams() {
    return ExportParams.builder()
        .isDescending(true)
        .maxTopEntrySize(Optional.of(3))
        .maxTopEntrySize(Optional.of(10))
        .maxPartWordListSize(Optional.of(10))
        .generalMinimum(Optional.of(2.0))
        .numberOfDetailedAggregation(Optional.of(0))
        .build();
  }
}
