package ch.ljacqu.wordeval.evaluation;

import java.util.List;
import ch.ljacqu.wordeval.LetterService;

/**
 * Filters long words which only use one different vowel or consonant, like
 * "abracadabra".
 */
public class MonotoneVowel extends Evaluator<Integer, String> {

  private List<Character> letters;

  public MonotoneVowel(LetterType letterType) {
    if (letterType == LetterType.VOWELS) {
      letters = LetterService.getVowels();
    } else {
      letters = LetterService.getConsonants();
    }
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

  @Override
  protected void outputEntry(Integer key, List<String> entry) {
    if (key < 7) {
      System.out.println(key + ": " + entry.size());
    } else {
      System.out.println(key + ": " + entry);
    }
  }

}
