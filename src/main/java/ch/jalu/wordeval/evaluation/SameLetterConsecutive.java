package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Finds words wherein the same letter appears multiple times consecutively,
 * e.g. "lll" in German "Rollladen."
 */
public class SameLetterConsecutive extends PartWordEvaluator {

  @Override
  public void processWord(String word, String rawWord) {
    int counter = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++counter;
      } else {
        if (counter > 1) {
          addEntry(StringUtils.repeat(lastChar, counter), rawWord);
        }
        lastChar = i < word.length() ? word.charAt(i) : '\0';
        counter = 1;
      }
    }
  }

  @Override
  public ExportObject toExportObject() {
    return toExportObject(ExportParams.builder()
        .topKeys(5)
        .isDescending(false)
        .topEntryMinimum(Optional.of(3.0))
        .build());
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

}