package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.language.WordForm;

/**
 * Finds words with multiple consecutive letter groups following each other,
 * e.g. <code>voorraaddrakoste</code> in Afrikaans (oo + rr + aa + dd = 4).
 */
public class ConsecutiveLetterPairs extends Evaluator<Integer, String> {

  @Override
  public void processWord(String word, String rawWord) {
    int letterCounter = 0;
    int pairCounter = 0;
    int pairCountMax = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++letterCounter;
      } else {
        if (letterCounter > 1) {
          ++pairCounter;
          if (pairCounter > pairCountMax) {
            pairCountMax = pairCounter;
          }
        } else {
          pairCounter = 0;
        }
        lastChar = i < word.length() ? word.charAt(i) : '\0';
        letterCounter = 1;
      }
    }
    if (pairCountMax > 1) {
      addEntry(pairCountMax, rawWord);
    }
  }
  
  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

}
