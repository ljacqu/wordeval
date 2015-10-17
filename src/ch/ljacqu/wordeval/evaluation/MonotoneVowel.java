package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LanguageService;
import ch.ljacqu.wordeval.language.LetterType;
import lombok.Getter;

/**
 * Filters long words which only use one different vowel or consonant, like
 * "abracadabra".
 */
public class MonotoneVowel extends WordStatEvaluator {

  private List<String> letters;
  @Getter
  private LetterType letterType;

  /**
   * Creates a new MonotoneVowel evaluator.
   * @param letterType The letter type (consonant, vowel) to consider
   * @param language The language of the words to process
   */
  public MonotoneVowel(LetterType letterType, Language language) {
    letters = LanguageService.getLetters(letterType, language);
    this.letterType = letterType;
  }

  @Override
  public void processWord(String word, String rawWord) {
    boolean foundLetter = false;
    for (String letter : letters) {
      if (word.indexOf(letter) != -1) {
        if (foundLetter) {
          // found another letter of that category, so stop
          return;
        } else {
          foundLetter = true;
        }
      }
    }
    if (foundLetter) {
      addEntry(word.length(), rawWord);
    }
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
  }

  @Override
  public ExportObject toExportObject() {
    String identifier = "Monotone_" + letterType.getName();
    return WordStatExport.create(identifier, getNavigableResults());
  }

}
