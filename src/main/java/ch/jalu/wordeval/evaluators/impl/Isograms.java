package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds isograms (words with all different letters).
 */
public class Isograms implements WordEvaluator {

  @Override
  public void evaluate(Word wordObject, ResultStore resultStore) {
    String word = wordObject.getWithoutAccentsWordCharsOnly();
    Set<Character> charList = new HashSet<>();
    for (int i = 0; i < word.length(); ++i) {
      char currentChar = word.charAt(i);
      if (!charList.add(currentChar)) {
        // Char has already been encountered, so stop
        return;
      }
    }
    resultStore.addResult(wordObject, new EvaluationResult(word.length(), null));
  }
}
