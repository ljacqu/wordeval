package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.WordForm.RAW;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import ch.ljacqu.wordeval.evaluation.Evaluator;

/**
 * A dictionary to process.
 */
public class Dictionary {

  private static final String DICT_PATH = "dict/";

  private final String languageCode;
  /** The dictionary file to read from. */
  private final String fileName;
  /** The list of evaluators to make the dictionary use. */
  private final List<Evaluator> evaluators;
  /** Sanitizer to sanitize the dictionary's words. */
  private final Sanitizer sanitizer;

  public Dictionary(String fileName, Language language, Sanitizer sanitizer,
      List<Evaluator> evaluators) {
    this.fileName = fileName;
    this.evaluators = evaluators;
    this.sanitizer = sanitizer;
    this.languageCode = language.getCode();
  }

  public static Dictionary getDictionary(String languageCode,
      List<Evaluator> evaluators) {
    String fileName = DICT_PATH + languageCode + ".dic";
    return getDictionary(languageCode, languageCode, fileName, evaluators);
  }

  public static Dictionary getDictionary(String languageCode,
      String sanitizerName, String fileName, List<Evaluator> evaluators) {
    Language language = Language.get(languageCode);
    DictionarySettings settings = DictionarySettings.get(sanitizerName);
    return new Dictionary(fileName, language,
        settings.buildSanitizer(language), evaluators);
  }

  /**
   * Processes a dictionary; each word is passed to the evaluators.
   * @throws IOException If the dictionary file cannot be read
   */
  public final void process() throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

    try (BufferedReader br = new BufferedReader(isr)) {
      for (String line; (line = br.readLine()) != null;) {
        String[] wordForms = sanitizer.computeForms(line);
        if (wordForms.length != 0) {
          processWord(wordForms);
        }
      }
    }
  }

  /**
   * Passes the the current word to the evaluators in their desired form.
   * @param wordForms The array of word forms of the current word (see
   *        {@link #computeWordForms(String)}).
   */
  private void processWord(String[] wordForms) {
    for (Evaluator evaluator : evaluators) {
      evaluator.processWord(getWordForm(wordForms, evaluator.getWordForm()),
          getWordForm(wordForms, RAW));
    }
  }

  /**
   * Gets a given word form from the given list.
   * @param wordForms The list to retrieve the word form from
   * @param wordForm The word form type to get
   * @return The requested word form
   */
  private String getWordForm(String[] wordForms, WordForm wordForm) {
    return wordForms[wordForm.ordinal()];
  }

}
