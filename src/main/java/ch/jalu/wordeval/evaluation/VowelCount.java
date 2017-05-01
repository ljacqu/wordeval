package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluator which collects all words by count of 
 * separate vowels or consonants for further processing.
 */
public class VowelCount extends PartWordEvaluator {

  private final List<String> letters;
  @Getter
  private final LetterType letterType;

  /**
   * Creates a new VowelCount evaluator.
   *
   * @param letterType the letter type (consonant, vowel) to consider
   * @param language the language of the words to process
   */
  public VowelCount(LetterType letterType, Language language) {
    this.letters = letterType.getLetters(language);
    this.letterType = letterType;
  }

  @Override
  public void processWord(String word, String rawWord) {
    // TODO #64: Iterate over the letters of the word instead
    String letterProfile = letters.stream()
      .filter(word::contains)
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
