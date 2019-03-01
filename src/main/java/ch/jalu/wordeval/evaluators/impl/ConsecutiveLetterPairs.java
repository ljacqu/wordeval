package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

/**
 * Finds words with multiple consecutive letter groups following each other,
 * e.g. <code>voorraaddrakoste</code> in Afrikaans (oo + rr + aa + dd = 4).
 */
public class ConsecutiveLetterPairs implements WordEvaluator {

  @Override
  public void evaluate(Word wordObject, ResultStore resultStore) {
    String word = wordObject.getWithoutAccents();

    int letterCounter = 0;
    int pairCounter = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++letterCounter;
        continue;
      }
      if (letterCounter > 1) {
        ++pairCounter;
      }
      if (letterCounter <= 1 || i == word.length()) {
        if (pairCounter > 1) {
          resultStore.addResult(wordObject, new EvaluationResult(pairCounter, null));
        }
        pairCounter = 0;
      }
      lastChar = (i < word.length()) ? word.charAt(i) : '\0';
      letterCounter = 1;
    }
  }
}
