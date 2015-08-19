package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.LetterType;

/**
 * Filters long words which only use one different vowel or consonant, like
 * "abracadabra".
 */
public class MonotoneVowel extends Evaluator<Integer, String> {

  private List<Character> letters;

  public MonotoneVowel(LetterType letterType) {
    letters = LetterService.getLetters(letterType);
  }

  @Override
  public void processWord(String word, String rawWord) {
    boolean foundLetter = false;
    for (char letter : letters) {
      if (word.indexOf(letter) != -1) {
        if (foundLetter) {
          // already found another letter of that category, so stop
          return;
        } else {
          foundLetter = true;
        }
      }
    }
    addEntry(word.length(), rawWord);
  }

}
