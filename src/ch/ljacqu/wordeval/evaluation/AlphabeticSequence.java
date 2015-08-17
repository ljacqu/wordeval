package ch.ljacqu.wordeval.evaluation;

/**
 * Filters that checks if there is a group of letters in a word that is an
 * alphabetical sequence, e.g. "rstu" in German "Erstuntersuchung."
 */
public class AlphabeticSequence extends Evaluator<String, String> {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void processWord(String word, String rawWord) {
    checkForSequence(word, rawWord, FORWARDS);
    checkForSequence(word, rawWord, BACKWARDS);
  }

  public void checkForSequence(String word, String rawWord, int searchDirection) {
    int alphabeticalStreak = 1;
    String previousChar = String.valueOf(word.charAt(0));
    for (int i = 1; i < word.length(); ++i) {
      boolean isCharInSequence = false;
      if (i < word.length()) {
        String currentChar = String.valueOf(word.charAt(i));
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
          addEntry(alphabeticalSequence, rawWord);
        }
        alphabeticalStreak = 1;
      }
    }
  }

}
