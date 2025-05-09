package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.language.Language;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Application data holder.
 */
@Component
public class AppData {

  AppData() {
  }

  public Language getLanguage(String code) {
    return LanguageData.getOrThrow(code);
  }

  public Dictionary getDictionary(String code) {
    return DictionaryData.getOrThrow(code);
  }

  public List<Dictionary> getAllDictionaries() {
    return DictionaryData.streamThroughAll().toList();
  }
}
