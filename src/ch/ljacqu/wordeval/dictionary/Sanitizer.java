package ch.ljacqu.wordeval.dictionary;

import org.apache.commons.lang3.StringUtils;
import ch.ljacqu.wordeval.language.Language;

/**
 * Class responsible for sanitizing a dictionary's entries such that only the
 * word is returned, without any additional data stored on the same line.
 * Sanitizers may return an empty string if the current line is to be skipped.
 */
public class Sanitizer {

  /** The characters whose occurrence mark the end of the word on the line. */
  private final char[] delimiters;
  /** Words containing any entry of skipSequences are discarded. */
  private final String[] skipSequences;
  // TODO: Keep letter lists in Language and make WordFormsBuilder stateless
  private final WordFormsBuilder formsBuilder;

  /**
   * Creates a new sanitizer.
   * @param language the language of the dictionary
   * @param settings the dictionary settings
   */
  public Sanitizer(Language language, DictionarySettings settings) {
    delimiters = settings.getDelimiters();
    skipSequences = settings.getSkipSequences();
    formsBuilder = new WordFormsBuilder(language);
  }

  /**
   * Custom method a word goes through after basic default sanitation. This
   * allows subclasses to append their own behavior to the sanitation process.
   * @param word the word to process
   * @return the sanitized word (empty string to signal skip)
   */
  protected String customSanitize(String word) {
    return word;
  }

  /**
   * Sanitation entry method: takes a line read from the dictionary and converts
   * it into its sanitized form.
   * @param line the line (the word) to process
   * @return the sanitized word (empty string to signal skip)
   */
  public final String[] computeForms(String line) {
    String rawWord = removeDelimiters(line);
    if (shouldBeSkipped(rawWord)) {
      return new String[0];
    }
    return formsBuilder.computeForms(customSanitize(rawWord));
  }

  /**
   * Takes a line and extracts the word (in its raw form) from it. The first
   * occurrence of a <code>delimiter</code> (e.g. a space or '/') signals that
   * the word has ended.
   * @param crudeWord the word (line) to process
   * @return the sanitized word (empty string to signal skip)
   */
  private String removeDelimiters(String crudeWord) {
    int wordEndIndex = StringUtils.indexOfAny(crudeWord, delimiters);
    if (wordEndIndex == -1) {
      wordEndIndex = crudeWord.length();
    }
    return crudeWord.substring(0, wordEndIndex).trim().replace('–', '-');
  }

  /**
   * Checks whether the word contains any of the skip sequences or if it has
   * digits, in which case the word should be skipped.
   * @param word the word to process
   * @return true if the word should be skipped, false otherwise
   */
  private boolean shouldBeSkipped(String word) {
    if (StringUtils.containsAny(word, skipSequences)) {
      return true;
    } else if (word.matches(".*\\d+.*")) {
      return true;
    }
    return false;
  }

}
