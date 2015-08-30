package ch.ljacqu.wordeval.language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.evaluation.Evaluator;

public class Dictionary {

  private String languageCode;
  private String fileName;
  private List<Evaluator> evaluators;
  private Sanitizer sanitizer;

  public Dictionary(String languageCode, String fileName,
      List<Evaluator> evaluators, Sanitizer sanitizer) {
    this.languageCode = languageCode;
    this.fileName = fileName;
    this.evaluators = evaluators;
    this.sanitizer = sanitizer;
  }

  public static Dictionary getLanguageDictionary(String languageCode,
      List<Evaluator> evaluators) {
    return getLanguageDictionary(languageCode, evaluators, "dict/");
  }

  public static Dictionary getLanguageDictionary(String languageCode,
      List<Evaluator> evaluators, String path) {
    Sanitizer sanitizer = DictionaryLoader.getLanguageDictionary(languageCode);
    String fileName = path + languageCode + ".dic";
    return new Dictionary(languageCode, fileName, evaluators, sanitizer);
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
    String rawWord = sanitizer.sanitizeWord(crudeWord);
    wordForms[WordForm.RAW.ordinal()] = rawWord;

    String lowerCaseWord = rawWord.toLowerCase(sanitizer.getLocale());
    wordForms[WordForm.LOWERCASE.ordinal()] = lowerCaseWord;
    wordForms[WordForm.NO_ACCENTS.ordinal()] = LetterService
        .removeAccentsFromWord(lowerCaseWord);
    return wordForms;
  }

  private String getWordForm(String[] wordForms, WordForm wordForm) {
    return wordForms[wordForm.ordinal()];
  }

}
