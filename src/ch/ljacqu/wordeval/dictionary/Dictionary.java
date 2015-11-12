package ch.ljacqu.wordeval.dictionary;

import static ch.ljacqu.wordeval.dictionary.WordForm.RAW;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.EvaluatorService;
import ch.ljacqu.wordeval.language.Language;
import lombok.Getter;

/**
 * A dictionary to process.
 */
public class Dictionary {

  private static final String DICT_PATH = "dict/";

  /** The dictionary file to read from. */
  private final String fileName;
  /** Sanitizer to sanitize the dictionary's words. */
  @Getter
  private final Language language;
  private final Sanitizer sanitizer;
  private final WordFormsBuilder wordFormsBuilder;
  private final DataUtils dataUtils = new DataUtils();
  
  /**
   * Creates a Dictionary instance.
   * @param fileName the file name of the dictionary
   * @param language the language
   * @param sanitizer the sanitizer
   */
  public Dictionary(String fileName, Language language, Sanitizer sanitizer) {
    this.fileName = fileName;
    this.language = language;
    this.sanitizer = sanitizer;
    this.wordFormsBuilder = new WordFormsBuilder(language);
  }

  /**
   * Gets a known dictionary.
   * @param languageCode the language code of the dictionary to get
   * @return the dictionary of the given language code
   */
  public static Dictionary getDictionary(String languageCode) {
    String fileName = DICT_PATH + languageCode + ".dic";
    return getDictionary(languageCode, languageCode, fileName);
  }

  /**
   * Gets a known dictionary with custom settings.
   * @param languageCode the language code of the dictionary to get
   * @param sanitizerName the name of the sanitizer (typically same as dictionary)
   * @param fileName the file name where the dictionary is located
   * @return the dictionary object with the given settings
   */
  public static Dictionary getDictionary(String languageCode, String sanitizerName, String fileName) {
    Language language = Language.get(languageCode);
    DictionarySettings settings = DictionarySettings.get(sanitizerName);
    return new Dictionary(fileName, language, settings.buildSanitizer(language));
  }

  /**
   * Processes a dictionary; each word is passed to the evaluators.
   * @param evaluators the list of evaluators to pass the words to
   */
  public void process(Iterable<Evaluator<?>> evaluators) {
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = EvaluatorService.getPostEvaluators(evaluators);
    
    dataUtils.readFileLines(fileName)
      .stream()
      .map(sanitizer::isolateWord)
      .filter(StringUtils::isNotEmpty)
      .forEach(word -> sendToEvaluators(word, evaluators));

    EvaluatorService.executePostEvaluators(postEvaluators);
  }

  /**
   * Passes the the current word to the evaluators in their desired form.
   * @param wordForms the array of word forms of the current word.
   */
  private void sendToEvaluators(String word, Iterable<Evaluator<?>> evaluators) {
    String[] wordForms = wordFormsBuilder.computeForms(word);
    for (Evaluator<?> evaluator : evaluators) {
      evaluator.processWord(getWordForm(wordForms, evaluator.getWordForm()), 
                            getWordForm(wordForms, RAW));
    }
  }

  /**
   * Gets a given word form from the given list.
   * @param wordForms the list to retrieve the word form from
   * @param wordForm the word form type to get
   * @return the requested word form
   */
  private static String getWordForm(String[] wordForms, WordForm wordForm) {
    return wordForms[wordForm.ordinal()];
  }

}
