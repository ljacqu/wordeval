package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.LetterType;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;
import ch.ljacqu.wordeval.language.WordForm;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice as 2 ("oi", "ée").
 */
public class ConsecutiveVowelCount extends WordStatEvaluator {

  private List<Character> lettersToConsider;
  private LetterType letterType;

  /**
   * Creates a new VowelCount evaluator instance.
   * @param type The letter type to consider
   */
  public ConsecutiveVowelCount(LetterType type) {
    lettersToConsider = LetterService.getLetters(type);
    letterType = type;
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  @Override
  public void processWord(String word, String rawWord) {
    int count = 0;
    for (int i = 0; i <= word.length(); ++i) {
      if (i == word.length() || !lettersToConsider.contains(word.charAt(i))) {
        if (count > 1) {
          addEntry(count, rawWord);
        }
        count = 0;
      } else {
        ++count;
      }
    }
  }

  @Override
  public ExportObject toExportObject() {
    String identifier = "ConsecutiveVowelCount_" + letterType.getName();
    return toExportObject(identifier, new ExportParamsBuilder().setMinimum(3)
        .build());
  }

}
