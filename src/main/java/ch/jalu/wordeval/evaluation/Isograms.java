package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.WordForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds isograms (words with all different letters).
 */
public class Isograms extends WordStatEvaluator {

  @Override
  public void processWord(String word, String rawWord) {
    List<Character> charList = new ArrayList<>();
    for (int i = 0; i < word.length(); ++i) {
      char currentChar = word.charAt(i);
      if (charList.contains(currentChar)) {
        // we already saw currentChar, so stop
        return;
      }
      charList.add(currentChar);
    }
    addEntry(word.length(), rawWord);
  }
  
  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
  }

}
