package ch.ljacqu.wordeval.evaluation;

import java.util.Optional;

import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;

/**
 * Filters that checks if there is a group of letters in a word that is an
 * alphabetical sequence, e.g. "rstu" in German "Erstuntersuchung."
 */
public class AlphabeticalSequence extends PartWordEvaluator {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void processWord(String word, String rawWord) {
    checkForSequence(word, rawWord, FORWARDS);
    checkForSequence(word, rawWord, BACKWARDS);
  }

  @Override
  public WordForm getWordForm() {
    // TODO #15: Make locale-aware instead
    return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
  }

  @Override
  public ExportObject toExportObject() {
    return toExportObject(ExportParams.builder()
        .maxPartWordListSize(Optional.of(50))
        .maxTopEntrySize(Optional.empty())
        .topKeys(4)
        .build());
  }

  /**
   * Checks a word for consecutive alphabetical sequences.
   * @param word The lowercase word
   * @param rawWord The word in its raw form
   * @param searchDirection 1 to look for forward alphabetical sequences, -1 for
   *        backwards
   */
  private void checkForSequence(String word, String rawWord, int searchDirection) {
    int alphabeticalStreak = 1;
    String previousChar = String.valueOf(word.charAt(0));
    for (int i = 1; i <= word.length(); ++i) {
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
