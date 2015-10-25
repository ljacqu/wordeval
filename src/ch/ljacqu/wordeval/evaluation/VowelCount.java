package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import java.util.stream.Collectors;

import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LanguageService;
import ch.ljacqu.wordeval.language.LetterType;
import lombok.Getter;

/**
 * Evaluator which collects all words by count of 
 * separate vowels or consonants for further processing.
 */
public class VowelCount extends PartWordEvaluator {

  private final List<String> letters;
  @Getter
  private final LetterType letterType;

  /**
   * Creates a new MonotoneVowel evaluator.
   * @param letterType the letter type (consonant, vowel) to consider
   * @param language the language of the words to process
   */
  public VowelCount(LetterType letterType, Language language) {
    letters = LanguageService.getLetters(letterType, language);
    this.letterType = letterType;
  }

  @Override
  public void processWord(String word, String rawWord) {
    String letterProfile = letters.stream()
      .filter(letter -> word.indexOf(letter) > -1)
      .collect(Collectors.joining());
    addEntry(letterProfile, rawWord);
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  @Override
  public ExportObject toExportObject() {
    return null;
  }

}
