package ch.ljacqu.wordeval.evaluation;

/**
 * Filter that saves words whose letters are alphabetical from beginning to end,
 * forwards or backwards. For example, in German "einst", each following letter
 * comes later in the alphabet.
 */
public class AlphabeticalOrder extends Evaluator<Integer, String> {

  public enum SortDirection {
    FORWARDS(-1), BACKWARDS(1);

    SortDirection(int compareValue) {
      this.shallCompareValue = compareValue;
    }

    int shallCompareValue;
  }

  private SortDirection sortDirection;

  public AlphabeticalOrder(SortDirection direction) {
    sortDirection = direction;
  }

  @Override
  public void processWord(String word) {
    String previousChar = String.valueOf(word.charAt(0));
    for (int i = 1; i < word.length(); ++i) {
      String currentChar = String.valueOf(word.charAt(i));
      int comparison = strcmp(previousChar, currentChar);
      if (comparison == 0 || comparison == sortDirection.shallCompareValue) {
        previousChar = currentChar;
      } else {
        // The comparison is not what we were looking for, so stop
        return;
      }
    }
    addEntry(word.length(), word);
  }

  private int strcmp(String a, String b) {
    int comparison = a.compareToIgnoreCase(b);
    return comparison > 0 ? 1 : (comparison < 0 ? -1 : 0);
  }

}
