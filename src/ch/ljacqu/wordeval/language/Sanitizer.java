package ch.ljacqu.wordeval.language;

import java.util.Locale;

class Sanitizer {

  private char[] delimiters;
  private Locale locale;

  Sanitizer(Locale locale, char... delimiters) {
    this.locale = locale;
    this.delimiters = delimiters;
  }

  Sanitizer(String languageCode, char... delimiters) {
    this(new Locale(languageCode), delimiters);
  }

  protected String customSanitize(String word) {
    return word;
  }

  final String sanitizeWord(String crudeWord) {
    String cleanWord = removeDelimiters(crudeWord);
    return customSanitize(cleanWord);
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

  Locale getLocale() {
    return locale;
  }
}
