package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Class responsible for sanitizing a dictionary's entries such that only the
 * word is returned, without any additional data stored on the same line.
 * Sanitizers may return an empty string if the current line should be skipped.
 */
public class Sanitizer {

  /** The characters whose occurrence mark the end of the word on the line. */
  private final char[] delimiters;
  /** Words containing any entry of skipSequences are discarded. */
  private final String[] skipSequences;

  /**
   * Creates a new sanitizer.
   * @param dictionary the dictionary settings
   */
  public Sanitizer(Dictionary dictionary) {
    delimiters = dictionary.getDelimiters();
    skipSequences = dictionary.getSkipSequences();
  }

  /**
   * Custom method a word goes through after basic default sanitation. This
   * allows subclasses to append their own behavior to the sanitation process.
   * @param word the word to process
   * @return the sanitized word (empty string to signal skip)
   */
  protected String customSanitize(String word) {
    return DictionaryUtils.isRomanNumeral(word) ? "" : word;
  }

  /**
   * Takes a line read from the dictionary file and returns the word
   * it contains without any additional information. Note that an
   * empty String may be returned to signal that the word should be
   * skipped.
   * @param line the line to process
   * @return the sanitized word or empty string if line should be skipped
   */
  public final String isolateWord(String line) {
    String word = removeDelimiters(line);
    return shouldBeSkipped(word)
      ? ""
      : customSanitize(word);
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
    return StringUtils.containsAny(word, skipSequences) 
        || word.matches(".*\\d+.*");
  }

}
