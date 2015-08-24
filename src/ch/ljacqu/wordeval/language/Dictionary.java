package ch.ljacqu.wordeval.language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.evaluation.Evaluator;

@SuppressWarnings("rawtypes")
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

  public final void processDictionary() throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

    try (BufferedReader br = new BufferedReader(isr)) {
      for (String line; (line = br.readLine()) != null;) {
        String[] wordForms = computeWordForms(line);
        if (!getWordForm(wordForms, WordForm.RAW).isEmpty()) {
          processWord(wordForms);
        }
      }
    }
  }

  private void processWord(String[] wordForms) {
    for (Evaluator evaluator : evaluators) {
      evaluator.processWord(getWordForm(wordForms, evaluator.getWordForm()),
          getWordForm(wordForms, WordForm.RAW));
    }
  }

  private String[] computeWordForms(String crudeWord) {
    String[] wordForms = new String[WordForm.values().length];
    wordForms[WordForm.RAW_UNSAFE.ordinal()] = crudeWord;
    String rawWord = sanitizeWord(crudeWord);
    wordForms[WordForm.RAW.ordinal()] = rawWord;
    String lowerCaseWord = rawWord.toLowerCase(locale);
    wordForms[WordForm.LOWERCASE.ordinal()] = lowerCaseWord;
    wordForms[WordForm.NO_ACCENTS.ordinal()] = LetterService
        .removeAccentsFromWord(lowerCaseWord);
    return wordForms;
  }

  private String getWordForm(String[] wordForms, WordForm wordForm) {
    return wordForms[wordForm.ordinal()];
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

}
