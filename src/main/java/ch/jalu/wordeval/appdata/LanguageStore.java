package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;

import static ch.jalu.wordeval.language.Language.builder;

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
      builder("af", "Afrikaans", Alphabet.LATIN).build(),
      builder("bg", "Bulgarian", Alphabet.CYRILLIC).build(),
      builder("cs", "Czech", Alphabet.LATIN)
        // TODO #66: is stuff like "ň" really a distinct letter to preserve?
        .additionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
        .additionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý")
        .build(),
      builder("da", "Danish", Alphabet.LATIN)
        .additionalVowels("æ", "ø", "å")
        .build(),
      // TODO #51: How to handle 'ß'?
      builder("de", "German", Alphabet.LATIN).build(),
      builder("en", "English", Alphabet.LATIN)
        // TODO #66: how to deal with Y being consonant and vowel in English?
        .additionalConsonants("y")
        .build(),
      builder("es", "Spanish", Alphabet.LATIN)
        .additionalConsonants("ñ")
        .build(),
      builder("eu", "Basque", Alphabet.LATIN)
        .additionalConsonants("ñ")
        .build(),
      builder("fi", "Finnish", Alphabet.LATIN)
        .additionalVowels("ä", "ö")
        .build(),
      builder("fr", "French", Alphabet.LATIN)
          .additionalVowels("æ", "œ")
          .build(),
      builder("hu", "Hungarian", Alphabet.LATIN)
        .additionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
        .additionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű")
        .build(),
      builder("it", "Italian", Alphabet.LATIN).build(),
      builder("nb", "Norwegian (Bokmål)", Alphabet.LATIN)
        .additionalVowels("æ", "ø", "å")
        .build(),
      builder("nl", "Dutch", Alphabet.LATIN)
        .additionalVowels("ij")
        .build(),
      builder("nn", "Norwegian (Nynorsk)", Alphabet.LATIN)
        .additionalVowels("æ", "ø", "å")
        .build(),
      builder("pl", "Polish", Alphabet.LATIN)
        .additionalConsonants("ć", "ł", "ń", "ś", "ź", "ż")
        .additionalVowels("ą", "ę", "ó")
        .build(),
      builder("pt", "Portuguese", Alphabet.LATIN).build(),
      builder("ru", "Russian", Alphabet.CYRILLIC).build(),
      builder("sr-cyrl", "Serbian (Cyrillic)", Alphabet.CYRILLIC).build(),
      builder("sr-latn", "Serbian (Latin)", Alphabet.LATIN)
        .additionalConsonants("č", "ć", "dž", "đ", "lj", "nj", "š", "ž")
        .build(),
      builder("tr", "Turkish", Alphabet.LATIN)
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
}
