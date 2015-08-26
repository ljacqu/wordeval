package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.LetterType;
import ch.ljacqu.wordeval.language.WordForm;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice as 2 ("oi", "ée").
 */
public class VowelCount extends Evaluator<Integer> {

  private List<Character> recognizedLetters;

  /**
   * Creates a new VowelCount evaluator instance.
   * @param type The letter type to consider
   */
  public VowelCount(LetterType type) {
    recognizedLetters = LetterService.getLetters(type);
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  @Override
  public void processWord(String word, String rawWord) {
    int count = 0;
    for (int i = 0; i <= word.length(); ++i) {
      if (i == word.length() || !recognizedLetters.contains(word.charAt(i))) {
        if (count > 1) {
          addEntry(count, rawWord);
        }
        count = 0;
      } else {
        ++count;
      }
    }
  }

}
