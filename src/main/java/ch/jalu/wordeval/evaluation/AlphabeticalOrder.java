package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.ExportObject;

import java.util.Optional;

/**
 * Filter that saves words whose letters are alphabetical from beginning to end,
 * forwards or backwards. For example, in German "einst", each following letter
 * comes later in the alphabet.
 */
public class AlphabeticalOrder extends WordStatEvaluator {

  private static final int FORWARDS = -1;
  private static final int BACKWARDS = 1;

  @Override
  public void processWord(String word, String rawWord) {
    int length = checkIsOrdered(word, FORWARDS);
    if (length > 1) {
      addEntry(length, rawWord);
    }
    length = checkIsOrdered(word, BACKWARDS);
    if (length > 1) {
      addEntry(length, rawWord);
    }
  }

  @Override
  public WordForm getWordForm() {
    // TODO #15: Make locale-aware instead
    return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
  }

  @Override
  public ExportObject toExportObject() {
    return toExportObject(ExportParams.builder()
        .topEntryMinimum(Optional.of(4.0))
        .maxTopEntrySize(Optional.of(10))
        .build());
  }

  private static int checkIsOrdered(String word, int searchDirection) {
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

  private static int strcmp(String a, String b) {
    int comparison = a.compareToIgnoreCase(b);
    return comparison > 0 ? 1 : (comparison < 0 ? -1 : 0);
  }

}