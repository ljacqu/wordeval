package ch.ljacqu.wordeval.language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public abstract class Dictionary {

  @SuppressWarnings("unused")
  private String languageCode;
  private String fileName;
  private List<Evaluator> evaluators;
  private char[] delimiters;

  public Dictionary(String fileName, String languageCode,
      List<Evaluator> evaluators, char... delimiters) {
    this.languageCode = languageCode;
    this.fileName = fileName;
    this.evaluators = evaluators;
    this.delimiters = delimiters;
  }

  protected String sanitizeWord(String crudeWord) {
    int minIndex = crudeWord.length();
    for (char delimiter : delimiters) {
      int delimiterIndex = crudeWord.indexOf(delimiter);
      if (delimiterIndex > -1 && delimiterIndex < minIndex) {
        minIndex = delimiterIndex;
      }
    }
    return crudeWord.substring(0, minIndex);
  }

  public final void processDictionary() throws IOException {
    loadLineDictionary();
  }

  private void loadLineDictionary() throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

    try (BufferedReader br = new BufferedReader(isr)) {
      for (String line; (line = br.readLine()) != null;) {
        if (!line.trim().isEmpty()) {
          String word = sanitizeWord(line);
          if (!word.trim().isEmpty()) {
            processWord(word);
          }
        }
      }
    }
  }

  private void processWord(String word) {
    for (Evaluator evaluator : evaluators) {
      evaluator.processWord(word);
    }
  }

}
