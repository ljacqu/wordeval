package ch.ljacqu.wordeval.evaluation;

/**
 * Filters that checks if there is a group of letters in a word that
 * is an alphabetical sequence, e.g. "rstu" in German "Erstuntersuchung."
 */
public class AlphabeticSequence extends Evaluator<String, String> {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void processWord(String word) {
    checkForSequence(word, FORWARDS);
    checkForSequence(word, BACKWARDS);
  }

  public void checkForSequence(String word, int searchDirection) {
    int alphabeticalStreak = 1;
    String previousChar = String.valueOf(word.charAt(0)).toLowerCase();
    for (int i = 1; i < word.length(); ++i) {
      boolean isCharInSequence = false;
      if (i < word.length()) {
        String currentChar = String.valueOf(word.charAt(i)).toLowerCase();
        isCharInSequence = previousChar.compareTo(currentChar) == searchDirection;
        if (isCharInSequence) {
          ++alphabeticalStreak;
        }
        previousChar = currentChar;
      }
      if (!isCharInSequence || i == word.length()) {
        if (alphabeticalStreak > 2) {
          String alphabeticalSequence = word.substring(i - alphabeticalStreak,
              i);
          addEntry(alphabeticalSequence.toLowerCase(), word);
        }
        alphabeticalStreak = 1;
      }
    }
  }

}
