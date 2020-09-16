package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;

import java.util.Collection;

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

  public Dictionary getDictionary(String code) {
    return dictionaryStore.get(code);
  }

  public Collection<Dictionary> getAllDictionaries() {
    return dictionaryStore.entries.values();
  }
}
