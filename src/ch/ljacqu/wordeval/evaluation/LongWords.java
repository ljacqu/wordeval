package ch.ljacqu.wordeval.evaluation;

/**
 * Filters the words by length, with the intention to get the longest words of
 * the dictionary.
 */
public class LongWords extends Evaluator<Integer, String> {

  /** Ignore any words whose length is less than the minimum length. */
  public static final int DEFAULT_MIN_LENGTH = 6;

  private final int minLength;

  /**
   * Creates a new LongWords evaluator with the default minimum length.
   */
  public LongWords() {
    this(DEFAULT_MIN_LENGTH);
  }

  /**
   * Creates a new LongWords evaluator with a given minimum length; any words
   * whose length is lower than minLength will not be saved.
   * @param minLength The minimum length to consider
   */
  public LongWords(int minLength) {
    this.minLength = minLength;
  }

  @Override
  public void processWord(String word, String rawWord) {
    if (word.length() >= minLength) {
      addEntry(word.length(), rawWord);
    }
  }

}
