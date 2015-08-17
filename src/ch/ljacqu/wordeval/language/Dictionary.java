package ch.ljacqu.wordeval.language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import ch.ljacqu.wordeval.evaluation.Evaluator;

public class Dictionary {

  private String languageCode;
  private final Locale locale;
  private String fileName;
  private List<Evaluator> evaluators;
  private char[] delimiters;

  public Dictionary(String languageCode, String fileName,
      List<Evaluator> evaluators, char... delimiters) {
    this.fileName = fileName;
    this.evaluators = evaluators;
    this.delimiters = delimiters;
    locale = new Locale(languageCode);
  }

  public static Dictionary getLanguageDictionary(String languageCode,
      List<Evaluator> evaluators) throws Exception {
    return getLanguageDictionary(languageCode, evaluators, "dict/");
  }

  public static Dictionary getLanguageDictionary(String languageCode,
      List<Evaluator> evaluators, String path) throws Exception {
    char[] delimiters = DictionaryLoader.getLanguageDictionary(languageCode);
    String fileName = path + languageCode + ".dic";
    return new Dictionary(languageCode, fileName, evaluators, delimiters);
  }

  protected String sanitizeWord(String crudeWord) {
    int minIndex = crudeWord.length();
    for (char delimiter : delimiters) {
      int delimiterIndex = crudeWord.indexOf(delimiter);
      if (delimiterIndex > -1 && delimiterIndex < minIndex) {
        minIndex = delimiterIndex;
      }
    }
    return crudeWord.substring(0, minIndex).trim().toLowerCase(locale);
  }

  public final void processDictionary() throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

    try (BufferedReader br = new BufferedReader(isr)) {
      for (String line; (line = br.readLine()) != null;) {
        String cleanWord = sanitizeWord(line);
        if (!cleanWord.isEmpty()) {
          processWord(cleanWord, line);
        }
      }
    }
  }

  private void processWord(String word, String rawWord) {
    if (!word.trim().isEmpty()) {
      for (Evaluator evaluator : evaluators) {
        evaluator.processWord(word, rawWord);
      }
    }
  }

}
