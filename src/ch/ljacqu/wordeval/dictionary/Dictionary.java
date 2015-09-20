package ch.ljacqu.wordeval.dictionary;

import static ch.ljacqu.wordeval.dictionary.WordForm.RAW;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.language.Language;

/**
 * A dictionary to process.
 */
public class Dictionary {

  private static final String DICT_PATH = "dict/";

  @Getter
  private final String languageCode;
  /** The dictionary file to read from. */
  private final String fileName;
  /** The list of evaluators to make the dictionary use. */
  private final List<Evaluator<?>> evaluators;
  /** Sanitizer to sanitize the dictionary's words. */
  private final Sanitizer sanitizer;

  /**
   * Creates a new Dictionary instance.
   * @param fileName The file name where the dictionary is located
   * @param language The language of the dictionary
   * @param sanitizer The sanitizer to use while reading the dictionary
   * @param evaluators The list of evaluators
   */
  public Dictionary(String fileName, Language language, Sanitizer sanitizer, List<Evaluator<?>> evaluators) {
    this.fileName = fileName;
    this.evaluators = evaluators;
    this.sanitizer = sanitizer;
    this.languageCode = language.getCode();
  }

  /**
   * Gets a known dictionary.
   * @param languageCode The language code of the dictionary to get
   * @param evaluators The list of evaluators to process the words with
   * @return The dictionary of the given language code
   */
  public static Dictionary getDictionary(String languageCode, List<Evaluator<?>> evaluators) {
    String fileName = DICT_PATH + languageCode + ".dic";
    return getDictionary(languageCode, languageCode, fileName, evaluators);
  }

  /**
   * Gets a known dictionary with custom settings.
   * @param languageCode The language code of the dictionary to get
   * @param sanitizerName The name of the sanitizer (typically same as dictionary)
   * @param fileName The file name where the dictionary is located
   * @param evaluators The list of evaluators to process the words with
   * @return The dictionary object with the given settings
   */
  public static Dictionary getDictionary(String languageCode, String sanitizerName, String fileName,
      List<Evaluator<?>> evaluators) {
    Language language = Language.get(languageCode);
    DictionarySettings settings = DictionarySettings.get(sanitizerName);
    return new Dictionary(fileName, language, settings.buildSanitizer(language), evaluators);
  }
  
  /**
   * Returns all known dictionary codes.
   * @return All known dictionary codes
   */
  public static Set<String> getAllCodes() {
    return DictionarySettings.getAllCodes();
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
   * @param wordForms The array of word forms of the current word.
   */
  private void processWord(String[] wordForms) {
    for (Evaluator<?> evaluator : evaluators) {
      evaluator.processWord(getWordForm(wordForms, evaluator.getWordForm()), getWordForm(wordForms, RAW));
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
