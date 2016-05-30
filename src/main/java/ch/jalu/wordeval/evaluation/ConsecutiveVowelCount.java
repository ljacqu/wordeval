package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LanguageService;
import ch.jalu.wordeval.language.LetterType;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice ("oi", "ée").
 */
public class ConsecutiveVowelCount extends WordStatEvaluator {

  private List<String> lettersToConsider;
  @Getter
  private LetterType letterType;

  /**
   * Creates a new VowelCount evaluator instance.
   *
   * @param type the letter type to consider
   * @param language the language of the words to evaluate
   */
  public ConsecutiveVowelCount(LetterType type, Language language) {
    lettersToConsider = LanguageService.getLetters(type, language);
    letterType = type;
  }

  @Override
  public void processWord(String word, String rawWord) {
    int count = 0;
    for (int i = 0; i <= word.length(); ++i) {
      if (i == word.length() || !lettersToConsider.contains(word.substring(i, i + 1))) {
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
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  @Override
  public ExportObject toExportObject() {
    String identifier = "ConsecutiveVowelCount_" + letterType.getName();
    return toExportObject(identifier, ExportParams.builder()
        .topEntryMinimum(Optional.of(3.0))
        .topKeys(4)
        .build());
  }

}
