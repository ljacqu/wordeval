package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

/**
 * Filters the words by length, with the intention to get the longest words of
 * the dictionary.
 */
public class LongWords implements WordEvaluator {

  /** Ignore any words whose length is less than the minimum length. */
  private static final int MIN_LENGTH = 6;

  @Override
  public void evaluate(Word word, ResultStore resultStore) {
    if (word.getLowercase().length() >= MIN_LENGTH) {
      resultStore.addResult(word, new EvaluationResult(word.getLowercase().length(), null));
    }
  }
}
