package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import org.apache.commons.lang3.StringUtils;

/**
 * Finds words wherein the same letter appears multiple times consecutively,
 * e.g. "lll" in German "Rollladen."
 */
public class SameLetterConsecutive implements WordEvaluator<WordWithKey> {

  @Override
  public void evaluate(Word wordObject, ResultStore<WordWithKey> resultStore) {
    String word = wordObject.getWithoutAccents();
    int counter = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++counter;
      } else {
        if (counter > 1) {
          resultStore.addResult(new WordWithKey(wordObject, StringUtils.repeat(lastChar, counter)));
        }
        lastChar = i < word.length() ? word.charAt(i) : '\0';
        counter = 1;
      }
    }
  }
}
