package ch.ljacqu.wordeval.evaluation;

public class LongWords extends Evaluator<Integer, String> {

  /** Ignore any words whose length is less than the minimum length. */
  public static final int DEFAULT_MIN_LENGTH = 6;

  private int minLength = DEFAULT_MIN_LENGTH;

  public LongWords() {
  }

  public LongWords(int minLength) {
    this.minLength = minLength;
  }

  @Override
  public void processWord(String word) {
    if (word.length() >= minLength) {
      addEntry(word.length(), word);
    }
  }

}
