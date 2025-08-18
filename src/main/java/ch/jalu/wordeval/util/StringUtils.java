package ch.jalu.wordeval.util;

/**
 * String utilities.
 */
public final class StringUtils {

  private StringUtils() {
  }

  /**
   * Returns the last character of the given string. Throws an exception if the string is empty.
   *
   * @param str the string to process
   * @return the string's last character
   */
  public static char getLastChar(String str) {
    return str.charAt(str.length() - 1);
  }
}
