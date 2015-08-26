package ch.ljacqu.wordeval.language;

import java.util.HashMap;
import java.util.Map;

class DictionaryLoader {

  private static Map<String, char[]> languages = new HashMap<String, char[]>();

  static {
    addLanguage("af", '/');
    addLanguage("hu", '/', '\t');
    addLanguage("tr", ' ');
  }

  static char[] getLanguageDictionary(String languageCode) throws Exception {
    if (languages.get(languageCode) == null) {
      throw new Exception("Could not find language '" + languageCode + "'");
    }
    return languages.get(languageCode);
  }

  private static void addLanguage(String languageCode, char... delimiters) {
    languages.put(languageCode, delimiters);
  }

}
