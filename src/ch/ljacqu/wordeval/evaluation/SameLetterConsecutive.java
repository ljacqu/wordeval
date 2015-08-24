package ch.ljacqu.wordeval.evaluation;

import org.apache.commons.lang3.StringUtils;
import ch.ljacqu.wordeval.language.WordForm;

public class SameLetterConsecutive extends Evaluator<String, String> {

  @Override
  public void processWord(String word, String rawWord) {
    int counter = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++counter;
      } else {
        if (counter > 1) {
          addEntry(StringUtils.repeat(lastChar, counter), rawWord);
        }
        lastChar = i < word.length() ? word.charAt(i) : '\0';
        counter = 1;
      }
    }
  }
  
  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

}
