package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.Map;

final class DictionaryLoader {

  private DictionaryLoader() {
  }

  private static Map<String, Language> languages = new HashMap<String, Language>();

  static {
    Language.create("af").setDelimiters('/')
        .setSkipSequences(".", "µ", "Ð", "ø");
    Language.create("hu", new HuSanitizer('/', '\t'));
    Language.create("tr").setDelimiters(' ');
  }

  static Sanitizer getLanguageDictionary(String languageCode) {
    if (languages.get(languageCode) == null) {
      throw new IllegalArgumentException("Could not find language '"
          + languageCode + "'");
    }
    return languages.get(languageCode).getSanitizer();
  }

  static void registerLanguage(Language language) {
    languages.put(language.code, language);
  }

}
