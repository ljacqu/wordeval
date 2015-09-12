package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.WordStatExport;
import ch.ljacqu.wordeval.language.LetterService;
import ch.ljacqu.wordeval.language.LetterType;

/**
 * Filters long words which only use one different vowel or consonant, like
 * "abracadabra".
 */
public class MonotoneVowel extends WordStatEvaluator {

  private List<Character> letters;
  private LetterType letterType;

  /**
   * Creates a new MonotoneVowel evaluator.
   * @param letterType The letter type (consonant, vowel) to consider
   */
  public MonotoneVowel(LetterType letterType) {
    letters = LetterService.getLetters(letterType);
    this.letterType = letterType;
  }

  @Override
  public void processWord(String word, String rawWord) {
    boolean foundLetter = false;
    for (char letter : letters) {
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
    // TODO: No accents, or just lowercase?
    return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
  }

  @Override
  public ExportObject toExportObject() {
    String identifier = "Monotone_" + letterType.getName();
    return WordStatExport.create(identifier, getNavigableResults());
  }

}
