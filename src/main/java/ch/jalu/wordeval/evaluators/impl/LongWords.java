package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordWithScore;

/**
 * Filters the words by length, with the intention to get the longest words of
 * the dictionary.
 */
public class LongWords implements WordEvaluator<WordWithScore> {

  /** Ignore any words whose length is less than the minimum length. */
  private static final int MIN_LENGTH = 6;

  @Override
  public void evaluate(Word word, ResultStore<WordWithScore> resultStore) {
    int length = word.getLowercase().length();
    if (length >= MIN_LENGTH) {
      resultStore.addResult(new WordWithScore(word, length));
    }
  }
}
