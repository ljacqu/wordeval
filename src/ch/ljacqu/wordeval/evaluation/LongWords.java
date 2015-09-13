package ch.ljacqu.wordeval.evaluation;

/**
 * Filters the words by length, with the intention to get the longest words of
 * the dictionary.
 */
public class LongWords extends WordStatEvaluator {

  /** Ignore any words whose length is less than the minimum length. */
  public static final int MIN_LENGTH = 6;

  @Override
  public void processWord(String word, String rawWord) {
    if (word.length() >= MIN_LENGTH) {
      addEntry(word.length(), rawWord);
    }
  }

}
