package ch.jalu.wordeval.evaluation;

import java.util.Arrays;
import java.util.Optional;

import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.PartWordExport;
import ch.jalu.wordeval.evaluation.export.PartWordReducer;

/**
 * Collects anagram groups (e.g. "acre", "care", "race").
 */
public class Anagrams extends PartWordEvaluator {

  @Override
  public void processWord(String word, String rawWord) {
    char[] chars = word.toCharArray();
    Arrays.sort(chars);
    addEntry(new String(chars), rawWord.toLowerCase());
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
  }
  
  @Override
  public ExportObject toExportObject() {
    ExportParams params = ExportParams.builder()
        .isDescending(true)
        .maxTopEntrySize(Optional.of(3))
        .maxTopEntrySize(Optional.of(10))
        .maxPartWordListSize(Optional.of(10))
        .generalMinimum(Optional.of(2.0))
        .numberOfDetailedAggregation(Optional.of(0))
        .build();
    return PartWordExport.create("anagrams", getResults(), params, new PartWordReducer.BySize());
  }

}
