package ch.jalu.wordeval.propertytransformers;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluation.export.ExportParams;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Filter that saves words whose letters are alphabetical from beginning to end,
 * forwards or backwards. For example, in German "einst", each following letter
 * comes later in the alphabet.
 */
public class AlphabeticalOrder implements PropertyFinder<Integer> {

  private static final int FORWARDS = -1; // TODO: Use predicates instead
  private static final int BACKWARDS = 1;

  @Override
  public List<Integer> findProperties(Word word) {
    String text = word.noAccentsWordCharsOnly(); // TODO #15: Make locale-aware instead
    int length = checkIsOrdered(text, FORWARDS);
    if (length > 1) {
      return singletonList(length);
    }
    length = checkIsOrdered(text, BACKWARDS);
    if (length > 1) {
      return singletonList(length);
    }
    return emptyList();
  }

  // TODO .
  public ExportParams buildExportParams() {
    return ExportParams.builder()
        .topEntryMinimum(Optional.of(4.0))
        .maxTopEntrySize(Optional.of(10))
        .build();
  }

  private static int checkIsOrdered(String word, int searchDirection) {
    String previousChar = word.substring(0, 1);
    for (int i = 1; i < word.length(); ++i) {
      String currentChar = word.substring(i, i + 1);
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
