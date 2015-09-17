package ch.ljacqu.wordeval.anagrams;

import java.util.Arrays;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;

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

}
