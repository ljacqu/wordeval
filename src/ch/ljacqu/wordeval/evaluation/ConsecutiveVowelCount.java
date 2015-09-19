package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LanguageService;
import ch.ljacqu.wordeval.language.LetterType;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice ("oi", "ée").
 */
public class ConsecutiveVowelCount extends WordStatEvaluator {

  private List<String> lettersToConsider;
  private LetterType letterType;

  /**
   * Creates a new VowelCount evaluator instance.
   * @param type The letter type to consider
   */
  public ConsecutiveVowelCount(LetterType type, Language language) {
    lettersToConsider = LanguageService.getLetters(type, language);
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
      if (i == word.length()
          || !lettersToConsider.contains(word.substring(i, i + 1))) {
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
    return toExportObject(identifier, new ExportParamsBuilder().setMinimum(3.0)
        .setTopKeys(4).build());
  }

}
