package ch.ljacqu.wordeval.anagrams;

import java.util.Arrays;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.PartWordReducer;

public class AnagramCollector extends PartWordEvaluator {

  public AnagramCollector() {
  }

  @Override
  public void processWord(String word, String rawWord) {
    char[] r = word.toCharArray();
    Arrays.sort(r);
    addEntry(new String(r), rawWord.toLowerCase());
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }
  
  @Override
  public ExportObject toExportObject() {
    ExportParams params = new ExportParamsBuilder().setDescending(true).build();
    return PartWordExport.create("anagrams", getResults(), params, new PartWordReducer.BySize());
  }

}
