package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class DictionaryLoader {

  private DictionaryLoader() {
  }

  private static Map<String, Language> languages = new HashMap<String, Language>();

  static {
    addLanguage("af", '/');
    addLanguage("hu", new HuSanitizer('/', '\t'));
    addLanguage("tr", ' ');
  }

  static Sanitizer getLanguageDictionary(String languageCode) {
    if (languages.get(languageCode) == null) {
      throw new IllegalArgumentException("Could not find language '"
          + languageCode + "'");
    }
    return languages.get(languageCode).getSanitizer();
  }

  private static void addLanguage(String languageCode, char... delimiters) {
    languages.put(languageCode, new Language(languageCode, null, delimiters));
  }

  private static void addLanguage(String languageCode, Sanitizer sanitizer) {
    languages.put(languageCode, new Language(languageCode, sanitizer));
  }

  private static class Language {
    public Language(String languageCode, Sanitizer sanitizer,
        char... delimiters) {
      this.languageCode = languageCode;
      this.sanitizer = sanitizer;
      this.delimiters = delimiters;
    }

    private final char[] delimiters;
    public final String languageCode;
    private Sanitizer sanitizer;

    Sanitizer getSanitizer() {
      if (sanitizer == null) {
        sanitizer = new Sanitizer(new Locale(languageCode), delimiters);
      }
      return sanitizer;
    }
  }

}
