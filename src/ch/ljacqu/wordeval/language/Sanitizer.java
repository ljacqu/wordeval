package ch.ljacqu.wordeval.language;

import java.util.Locale;

/**
 * Class responsible for sanitizing a dictionary's entries such that only the
 * word is returned, without any additional data stored on the same line.
 * Sanitizers may return an empty string if the current line is to be skipped.
 */
public class Sanitizer {

  /** The characters whose occurrence mark the end of the word on the line. */
  private char[] delimiters;
  /** Words containing any entry of skipSequences are discarded. */
  private String[] skipSequences;
  /** The locale of the current language. */
  private Locale locale;

  /**
   * Creates a new Sanitizer object.
   * @param locale The locale of the language
   * @param delimiters The delimiters used in the dictionary file
   * @param skipSequences Sequences that words may not contain
   */
  public Sanitizer(Locale locale, char[] delimiters, String[] skipSequences) {
    this.locale = locale;
    this.delimiters = delimiters;
    this.skipSequences = skipSequences;
  }

  /**
   * Creates a new Sanitizer object.
   * @param languageCode The code of the language (ISO-639-1)
   * @param delimiters The delimiters used in the dictionary file
   * @param skipSequences Sequences that words may not contain
   */
  public Sanitizer(String languageCode, char[] delimiters,
      String[] skipSequences) {
    this(new Locale(languageCode), delimiters, skipSequences);
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
    int minIndex = crudeWord.length();
    for (char delimiter : delimiters) {
      int delimiterIndex = crudeWord.indexOf(delimiter);
      if (delimiterIndex > -1 && delimiterIndex < minIndex) {
        minIndex = delimiterIndex;
      }
    }
    return crudeWord.substring(0, minIndex).trim().replace('–', '-');
  }

  /**
   * Checks whether the word contains any of the skip sequences or if it has
   * digits, in which case the word should be skipped.
   * @param word The word to process
   * @return True if the word should be skipped, false otherwise
   */
  private boolean shouldBeSkipped(String word) {
    for (String skipSequence : skipSequences) {
      if (word.contains(skipSequence)) {
        return true;
      }
    }
    if (word.matches(".*\\d+.*")) {
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
