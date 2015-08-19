package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.LetterService;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice as 2 ("oi", "ée").
 */
public class VowelCount extends Evaluator<Integer, String> {

  private boolean isVowel;

  private List<Character> recognizedVowels = LetterService.getVowels();

  public VowelCount(LetterType type) {
    isVowel = (type == LetterType.VOWELS);
  }

  @Override
  public void processWord(String word, String rawWord) {
    word = LetterService.removeAccentsFromWord(word);
    int count = 0;
    for (int i = 0; i <= word.length(); ++i) {
      if (i == word.length()
          || recognizedVowels.contains(word.charAt(i)) != isVowel) {
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
