package ch.ljacqu.wordeval.language;

import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
 * Class responsible for sanitizing a dictionary's entries such that only the
 * word is returned, without any additional data stored on the same line.
 * Sanitizers may return an empty string if the current line is to be skipped.
 */
public class Sanitizer {

  /** The locale of the current language. */
  private Locale locale;
  /** The characters whose occurrence mark the end of the word on the line. */
  private char[] delimiters;
  /** Words containing any entry of skipSequences are discarded. */
  private String[] skipSequences;
  /**
   * Additional letters that should be kept for the language. For characters
   * with diacritics, this means that they should not be replaced to the
   * accent-less version, ever.
   */
  private String[] additionalLetters;

  /**
   * Creates a new Sanitizer object.
   * @param locale The locale of the language
   * @param delimiters The delimiters used in the dictionary file
   * @param skipSequences Sequences that words may not contain
   * @param additionalLetters Additional letters outside the regular alphabet
   *        that should be recognized for the given language.
   */
  public Sanitizer(Locale locale, char[] delimiters, String[] skipSequences,
      String[] additionalLetters) {
    this.locale = locale;
    this.delimiters = delimiters;
    this.skipSequences = skipSequences;
    this.additionalLetters = additionalLetters;
  }

  /**
   * Creates a new Sanitizer object.
   * @param languageCode The code of the language (ISO-639-1)
   * @param delimiters The delimiters used in the dictionary file
   * @param skipSequences Sequences that words may not contain
   * @param additionalLetters Additional letters outside the regular alphabet
   *        that should be recognized for the given language.
   */
  public Sanitizer(String languageCode, char[] delimiters,
      String[] skipSequences, String[] additionalLetters) {
    this(new Locale(languageCode), delimiters, skipSequences, additionalLetters);
  }

  /**
   * Custom method a word goes through after basic default sanitation. This
   * allows subclasses to append their own behavior to the sanitation process.
   * @param word The word to process
   * @return The sanitized word (empty string to signal skip)
   */
  protected String customSanitize(String word) {
    return word;
  }

  /**
   * Sanitation entry method: takes a line read from the dictionary and converts
   * it into its sanitized form.
   * @param crudeWord The word (line) to process
   * @return The sanitized word (empty string to signal skip)
   */
  public final String sanitizeWord(String crudeWord) {
    String rawWord = removeDelimiters(crudeWord);
    if (shouldBeSkipped(rawWord)) {
      return "";
    }
    return customSanitize(rawWord);
  }

  /**
   * Takes a line and extracts the word (in its raw form) from it. The first
   * occurrence of a {@code delimiter} (e.g. a space or '/') signals that the
   * word has ended.
   * @param crudeWord The word (line) to process
   * @return The sanitized word (empty string to signal skip)
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
   * @param word The word to process
   * @return True if the word should be skipped, false otherwise
   */
  private boolean shouldBeSkipped(String word) {
    if (StringUtils.containsAny(word, skipSequences)) {
      return true;
    } else if (word.matches(".*\\d+.*")) {
      return true;
    }
    return false;
  }

  /**
   * Returns the locale of the given language.
   * @return The locale
   */
  public Locale getLocale() {
    return locale;
  }
}
