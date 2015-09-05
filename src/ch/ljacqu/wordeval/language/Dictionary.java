package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.WordForm.LOWERCASE;
import static ch.ljacqu.wordeval.language.WordForm.NO_ACCENTS;
import static ch.ljacqu.wordeval.language.WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
import static ch.ljacqu.wordeval.language.WordForm.RAW;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import ch.ljacqu.wordeval.LetterService;
import ch.ljacqu.wordeval.evaluation.Evaluator;

/**
 * A dictionary to process.
 */
public class Dictionary {

  /** The code of the language; should correspond to ISO-639-1. */
  private String languageCode;
  /** The dictionary file to read from. */
  private String fileName;
  /** The list of evaluators to make the dictionary use. */
  private List<Evaluator> evaluators;
  /** Sanitizer to sanitize the dictionary's words. */
  private Sanitizer sanitizer;

  /**
   * Creates a new Dictionary instance with custom options. See also
   * {@link #getLanguageDictionary()}.
   * @param languageCode The code of the language
   * @param fileName The file of the dictionary
   * @param evaluators The list of evaluators to use on the words
   * @param sanitizer The sanitizer for the dictionary entries
   */
  public Dictionary(String languageCode, String fileName,
      List<Evaluator> evaluators, Sanitizer sanitizer) {
    this.languageCode = languageCode;
    this.fileName = fileName;
    this.evaluators = evaluators;
    this.sanitizer = sanitizer;
  }

  /**
   * Returns a Dictionary object for one of the registered languages.
   * @param languageCode The code of the language to retrieve
   * @param evaluators The list of evaluators to use on the words
   * @return A Dictionary object for the given language
   */
  public static Dictionary getLanguageDictionary(String languageCode,
      List<Evaluator> evaluators) {
    return getLanguageDictionary(languageCode, evaluators, "dict/");
  }

  /**
   * Returns a Dictionary object for one of the registered languages.
   * @param languageCode The code of the language to retrieve
   * @param evaluators The list of evaluators to use on the words
   * @param path The path of the dictionary folder
   * @return A Dictionary object for the given language
   */
  public static Dictionary getLanguageDictionary(String languageCode,
      List<Evaluator> evaluators, String path) {
    Sanitizer sanitizer = DictionaryLoader.getLanguageDictionary(languageCode);
    String fileName = path + languageCode + ".dic";
    return new Dictionary(languageCode, fileName, evaluators, sanitizer);
  }

  /**
   * Processes a dictionary; each word is passed to the evaluators.
   * @throws IOException If the dictionary file cannot be read
   */
  public final void processDictionary() throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

    try (BufferedReader br = new BufferedReader(isr)) {
      for (String line; (line = br.readLine()) != null;) {
        String[] wordForms = computeWordForms(line);
        if (!getWordForm(wordForms, RAW).isEmpty()) {
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
   * Computes the different word forms (all lowercase, accents removed, etc.)
   * for the given word.
   * @param crudeWord The word to process
   * @return List of all word forms
   */
  private String[] computeWordForms(String crudeWord) {
    String[] wordForms = new String[WordForm.values().length];
    String rawWord = sanitizer.sanitizeWord(crudeWord);
    wordForms[RAW.ordinal()] = rawWord;

    String lowerCaseWord = rawWord.toLowerCase(sanitizer.getLocale());
    wordForms[LOWERCASE.ordinal()] = lowerCaseWord;
    wordForms[NO_ACCENTS.ordinal()] = LetterService
        .removeAccentsFromWord(lowerCaseWord);
    wordForms[NO_ACCENTS_WORD_CHARS_ONLY.ordinal()] = wordForms[NO_ACCENTS
        .ordinal()].replace("-", "").replace("'", "");
    return wordForms;
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
