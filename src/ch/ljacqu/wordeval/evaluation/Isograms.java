package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds isograms (word without a repeating letter).
 */
public class Isograms extends Evaluator<Integer, String> {

  @Override
  public void processWord(String word, String rawWord) {
    List<Character> charList = new ArrayList<Character>();
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

}
