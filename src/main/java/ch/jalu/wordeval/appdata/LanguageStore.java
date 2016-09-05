package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;

/**
 * Stores {@link Language} objects.
 */
class LanguageStore extends ObjectStore<String, Language> {

  LanguageStore() {
    addAll(buildEntries());
  }

  Language getLanguageOrNull(String code) {
    return entries.get(code);
  }

  private Language[] buildEntries() {
    return new Language[] {
      newLanguage("af", "Afrikaans", Alphabet.LATIN).build(),
      newLanguage("bg", "Bulgarian", Alphabet.CYRILLIC).build(),
      newLanguage("cs", "Czech", Alphabet.LATIN)
        // TODO #66: is stuff like "ň" really a distinct letter to preserve?
        .additionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
        .additionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý")
        .build(),
      newLanguage("da", "Danish", Alphabet.LATIN)
        .additionalVowels("æ", "ø", "å")
        .build(),
      // TODO #51: How to handle 'ß'?
      newLanguage("de", "German", Alphabet.LATIN).build(),
      newLanguage("en", "English", Alphabet.LATIN)
        // TODO #66: how to deal with Y being consonant and vowel in English?
        .additionalConsonants("y")
        .build(),
      newLanguage("es", "Spanish", Alphabet.LATIN)
        .additionalConsonants("ñ")
        .build(),
      newLanguage("eu", "Basque", Alphabet.LATIN)
        .additionalConsonants("ñ")
        .build(),
      newLanguage("fi", "Finnish", Alphabet.LATIN)
        .additionalVowels("ä", "ö")
        .build(),
      newLanguage("fr", "French", Alphabet.LATIN).build(),
      newLanguage("hu", "Hungarian", Alphabet.LATIN)
        .additionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
        .additionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű")
        .build(),
      newLanguage("it", "Italian", Alphabet.LATIN).build(),
      newLanguage("nb", "Norwegian (Bokmål)", Alphabet.LATIN)
        .additionalVowels("æ", "ø", "å")
        .build(),
      newLanguage("nl", "Dutch", Alphabet.LATIN)
        .additionalVowels("ij")
        .build(),
      newLanguage("nn", "Norwegian (Nynorsk)", Alphabet.LATIN)
        .additionalVowels("æ", "ø", "å")
        .build(),
      newLanguage("pl", "Polish", Alphabet.LATIN)
        .additionalConsonants("ć", "ł", "ń", "ś", "ź", "ż")
        .additionalVowels("ą", "ę", "ó")
        .build(),
      newLanguage("pt", "Portuguese", Alphabet.LATIN).build(),
      newLanguage("ru", "Russian", Alphabet.CYRILLIC).build(),
      newLanguage("sr-cyrl", "Serbian (Cyrillic)", Alphabet.CYRILLIC).build(),
      newLanguage("sr-latn", "Serbian (Latin)", Alphabet.LATIN)
        .additionalConsonants("č", "ć", "dž", "đ", "lj", "nj", "š", "ž")
        .build(),
      newLanguage("tr", "Turkish", Alphabet.LATIN)
        .additionalConsonants("ç", "ğ", "ş", "y")
        .additionalVowels("ı", "ö", "ü")
        .lettersToRemove("y")
        .build()
    };
  }

  @Override
  protected String getKey(Language language) {
    return language.getCode();
  }

  private Language.Builder newLanguage(String code, String name, Alphabet alphabet) {
    return Language.builder()
      .code(code)
      .name(name)
      .alphabet(alphabet);
  }

}
