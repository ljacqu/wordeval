package ch.ljacqu.wordeval.language;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Sanitizer {

  private char[] delimiters;
  private Locale locale;
  private Set<String> seenWords;

  public Sanitizer(Locale locale, char... delimiters) {
    this.locale = locale;
    this.delimiters = delimiters;
    this.seenWords = new HashSet<>();
  }

  public Sanitizer(String languageCode, char... delimiters) {
    this(new Locale(languageCode), delimiters);
  }

  protected String customSanitize(String word) {
    return word;
  }

  public final String sanitizeWord(String crudeWord) {
    String cleanWord = customSanitize(removeDelimiters(crudeWord));
    String cleanWordToLower = cleanWord.toLowerCase(locale);
    if (cleanWordToLower.matches(".*\\d+.*")) {
      return "";
    } else if (seenWords.contains(cleanWordToLower)) {
      return "";
    }
    seenWords.add(cleanWordToLower);
    return cleanWord;
  }

  private String removeDelimiters(String crudeWord) {
    int minIndex = crudeWord.length();
    for (char delimiter : delimiters) {
      int delimiterIndex = crudeWord.indexOf(delimiter);
      if (delimiterIndex > -1 && delimiterIndex < minIndex) {
        minIndex = delimiterIndex;
      }
    }
    return crudeWord.substring(0, minIndex).trim();
  }

  public Locale getLocale() {
    return locale;
  }
}
