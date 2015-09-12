package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;

/**
 * Finds words with multiple consecutive letter groups following each other,
 * e.g. <code>voorraaddrakoste</code> in Afrikaans (oo + rr + aa + dd = 4).
 */
public class ConsecutiveLetterPairs extends WordStatEvaluator {

  @Override
  public void processWord(String word, String rawWord) {
    int letterCounter = 0;
    int pairCounter = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++letterCounter;
      } else {
        if (letterCounter > 1) {
          ++pairCounter;
        }
        if (letterCounter <= 1 || i == word.length()) {
          if (pairCounter > 1) {
            addEntry(pairCounter, rawWord);
          }
          pairCounter = 0;
        }
        lastChar = (i < word.length()) ? word.charAt(i) : '\0';
        letterCounter = 1;
      }
    }
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  @Override
  public ExportObject toExportObject() {
    return toExportObject(new ExportParamsBuilder().setTopKeys(3).build());
  }

}
