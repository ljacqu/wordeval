package ch.ljacqu.wordeval.evaluation;

/**
 * Filter that saves words whose letters are alphabetical from beginning to end,
 * forwards or backwards. For example, in German "einst", each following letter
 * comes later in the alphabet.
 */
public class AlphabeticalOrder extends Evaluator<Integer> {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void processWord(String word, String rawWord) {
    int length = checkIsOrdered(word, FORWARDS);
    if (length > 0) {
      addEntry(length, rawWord);
    }
    length = checkIsOrdered(word, BACKWARDS);
    if (length > 0) {
      addEntry(length, rawWord);
    }
  }

  private int checkIsOrdered(String word, int searchDirection) {
    String previousChar = String.valueOf(word.charAt(0));
    for (int i = 1; i < word.length(); ++i) {
      String currentChar = String.valueOf(word.charAt(i));
      int comparison = strcmp(previousChar, currentChar);
      if (comparison == 0 || comparison == searchDirection) {
        previousChar = currentChar;
      } else {
        // The comparison is not what we were looking for, so stop
        return 0;
      }
    }
    return word.length();
  }

  private int strcmp(String a, String b) {
    int comparison = a.compareToIgnoreCase(b);
    return comparison > 0 ? 1 : (comparison < 0 ? -1 : 0);
  }

}
