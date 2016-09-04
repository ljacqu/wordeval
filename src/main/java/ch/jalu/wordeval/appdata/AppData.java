package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.DictionarySettings;
import ch.jalu.wordeval.language.Language;

import java.util.Set;

/**
 * Application data holder.
 */
public class AppData {

  private final DictionarySettingsStore dictionaryStore;
  private final LanguageStore languageStore;

  public AppData() {
    this.languageStore = new LanguageStore();
    this.dictionaryStore = new DictionarySettingsStore(languageStore);
  }

  public Language getLanguage(String code) {
    return languageStore.get(code);
  }

  public DictionarySettings getDictionary(String code) {
    return dictionaryStore.get(code);
  }

  public Set<String> getAllDictionaryCodes() {
    return dictionaryStore.keySet();
  }
}
