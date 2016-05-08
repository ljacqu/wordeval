package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.EvaluatorInvoker;
import ch.jalu.wordeval.language.Language;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * A dictionary to process.
 */
public class Dictionary {

  private static final String DICT_PATH = "dict/";

  /** The dictionary file to read from. */
  @Getter
  private final String fileName;
  @Getter
  private final Language language;
  /** Sanitizer to sanitize the dictionary's words. */
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
    return new Dictionary(fileName, language, settings.buildSanitizer());
  }

  /**
   * Processes a dictionary; each word is passed to the evaluators.
   *
   * @param evaluators the list of evaluators to pass the words to
   * @return The total number of words read
   */
  public long process(Iterable<Evaluator<?>> evaluators) {
    EvaluatorInvoker invoker = new EvaluatorInvoker(evaluators);
    
    long totalWords = dataUtils.readFileLines(fileName)
      .stream()
      .map(sanitizer::isolateWord)
      .filter(StringUtils::isNotEmpty)
      .map(wordFormsBuilder::computeForms)
      .peek(invoker::processWord)
      .count();

    invoker.executePostEvaluators();
    evaluators.forEach(e -> e.filterDuplicateWords(language.getLocale()));
    return totalWords;
  }

}
