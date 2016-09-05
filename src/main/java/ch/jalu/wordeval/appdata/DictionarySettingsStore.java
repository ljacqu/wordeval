package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.FrSanitizer;
import ch.jalu.wordeval.dictionary.HuSanitizer;
import ch.jalu.wordeval.language.Language;

/**
 * Stores {@link Dictionary} objects.
 */
class DictionarySettingsStore extends ObjectStore<String, Dictionary> {

  private static final String DICT_PATH = "dict/";

  private final LanguageStore languageStore;

  DictionarySettingsStore(LanguageStore languageStore) {
    this.languageStore = languageStore;
    addAll(buildEntries());
  }

  private Dictionary[] buildEntries() {
    return new Dictionary[] {
      newDictionary("af").delimiters('/').skipSequences(".", "µ", "Ð", "ø").build(),
      newDictionary("bg").delimiters('/').build(),
      newDictionary("da").delimiters('/').build(),
      newDictionary("de").delimiters('/').build(),
      newDictionary("de-at").delimiters('/').build(),
      newDictionary("de-ch").delimiters('/').build(),
      newDictionary("de-de").delimiters('/').build(),
      newDictionary("en-us").delimiters('/').build(),
      newDictionary("en-test").delimiters('/').build(),
      // TODO #62: Some Basque entries have _ but most parts seem to be present alone
      newDictionary("es").delimiters('/').build(),
      newDictionary("eu").delimiters('/').skipSequences(".", "+", "_").build(),
      newDictionary("fr").sanitizerClass(FrSanitizer.class).delimiters('/', '\t').skipSequences(".", "&", "µ").build(),
      newDictionary("hu").sanitizerClass(HuSanitizer.class).delimiters('/', '\t').skipSequences(".", "+", "±", "ø", "ʻ", "’", "­").build(),
      newDictionary("it").delimiters('/').build(),
      newDictionary("nb").delimiters('/').build(),
      // TODO: The nl dictionary uses the digraph symbol 'ĳ' instead of 'i'+'j'
      newDictionary("nl").delimiters('/').build(),
      newDictionary("nn").delimiters('/').build(),
      newDictionary("pl").delimiters('/').build(),
      newDictionary("pt-br").delimiters('/').build(),
      newDictionary("pt-pt").delimiters('/', '[').build(),
      newDictionary("ru").delimiters('/').skipSequences(".").build(),
      newDictionary("sr-cyrl").build(),
      newDictionary("sr-latn").delimiters('/').build(),
      newDictionary("tr").delimiters(' ').build()
    };
  }

  @Override
  protected String getKey(Dictionary settings) {
    return settings.getIdentifier();
  }

  private Dictionary.Builder newDictionary(String identifier) {
    return Dictionary.builder()
        .identifier(identifier)
        .file(DICT_PATH + identifier + ".dic")
        .language(getLanguage(identifier));
  }

  private Language getLanguage(String identifier) {
    Language language = languageStore.getLanguageOrNull(identifier);
    if (language == null) {
      if (identifier.indexOf('-') != -1) {
        return getLanguage(identifier.substring(0, identifier.indexOf('-')));
      }
      throw new IllegalStateException("No language stored for code '" + identifier + "'");
    }
    return languageStore.get(identifier);
  }

}
