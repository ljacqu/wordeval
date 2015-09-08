package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.Map;

final class DictionaryLoader {

  private DictionaryLoader() {
  }

  private static Map<String, LanguageSettings> languages = new HashMap<String, LanguageSettings>();

  static {
    save(new Language("af").setDelimiters('/').setSkipSequences(".", "µ", "Ð",
        "ø"));
    save(new CustomLanguage("hu", HuSanitizer.class));
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
  private static class CustomLanguage implements LanguageSettings {
    private Sanitizer sanitizer;
    private String code;
    private Class<? extends Sanitizer> sanitizerClass;

    public CustomLanguage(String code, Class<? extends Sanitizer> sanitizerClass) {
      this.code = code;
      this.sanitizerClass = sanitizerClass;
    }

    @Override
    public Sanitizer getSanitizer() {
      if (sanitizer == null) {
        try {
          sanitizer = sanitizerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
          throw new UnsupportedOperationException("Could not get sanitizer", e);
        }
      }
      return sanitizer;
    }

    @Override
    public String getCode() {
      return code;
    }
  }

}
