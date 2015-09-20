package ch.ljacqu.wordeval.evaluation;

import java.util.Arrays;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.PartWordReducer;

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
    ExportParams params = new ExportParamsBuilder()
      .setDescending(true)
      .setMaxTopEntrySize(2)
      .build();
    return PartWordExport.create("anagrams", getResults(), params, new PartWordReducer.BySize());
  }

}
