package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.Map;

final class DictionaryLoader {

  private DictionaryLoader() {
  }

  private static Map<String, LanguageSettings> languages = new HashMap<String, LanguageSettings>();

  static {
    save(new Language("af").setDelimiters('/')
           .setSkipSequences(".", "µ", "Ð", "ø"));
    save(new HuLanguage());
    save(new Language("tr").setDelimiters(' '));
  }

  static Sanitizer getLanguageDictionary(String languageCode) {
    if (languages.get(languageCode) == null) {
      throw new IllegalArgumentException("Could not find language '"
          + languageCode + "'");
    }
    return languages.get(languageCode).getSanitizer();
  }

  static void save(LanguageSettings language) {
    languages.put(language.getCode(), language);
  }

  // ---------
  // Custom language classes
  // ---------
  private static class HuLanguage implements LanguageSettings {
    private Sanitizer sanitizer;

    @Override
    public Sanitizer getSanitizer() {
      if (sanitizer == null) {
        sanitizer = new HuSanitizer('/', '\t');
      }
      return sanitizer;
    }

    @Override
    public String getCode() {
      return "hu";
    }
  }

}
