package ch.jalu.wordeval.propertytransformers;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluation.export.ExportParams;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Checks for groups of letters in a word that create an
 * alphabetical sequence, such as "rstu" in German "Erstuntersuchung."
 */
public class AlphabeticalSequence implements PropertyFinder<String> {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public List<String> findProperties(Word word) {
    // TODO #15: Make locale-aware instead
    String text = word.noAccentsWordCharsOnly();

    List<String> results = new ArrayList<>();
    results.addAll(checkForSequence(text, FORWARDS));
    results.addAll(checkForSequence(text, BACKWARDS));
    return results;
  }


  // TODO .
  public ExportParams buildExportParams() {
    return ExportParams.builder()
        .maxPartWordListSize(Optional.of(10))
        .maxTopEntrySize(Optional.of(20))
        .topKeys(4)
        .build();
  }

  /**
   * Checks a word for consecutive alphabetical sequences.
   * @param word The lowercase word
   * @param searchDirection 1 to look for forward alphabetical sequences, -1 for backwards
   */
  private List<String> checkForSequence(String word, int searchDirection) {
    int alphabeticalStreak = 1;
    List<String> results = new LinkedList<>();

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
          String alphabeticalSequence = word.substring(i - alphabeticalStreak, i);
          results.add(alphabeticalSequence);
        }
        alphabeticalStreak = 1;
      }
    }
    return results;
  }
}
