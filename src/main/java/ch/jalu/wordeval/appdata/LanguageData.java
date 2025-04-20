package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;

import java.util.stream.Stream;

import static ch.jalu.wordeval.language.Language.builder;

/**
 * Stores all {@link Language} objects.
 *
 * @see AppData
 */
final class LanguageData {

  public static final Language AF = builder("af", "Afrikaans", Alphabet.LATIN).build();
  public static final Language BG = builder("bg", "Bulgarian", Alphabet.CYRILLIC).build();
  public static final Language CS = builder("cs", "Czech", Alphabet.LATIN)
      // TODO #66: is stuff like "ň" really a distinct letter to preserve?
      .additionalConsonants("č", "ď", "ch", "ň", "ř", "š", "ť", "ž")
      .additionalVowels("á", "é", "ě", "í", "ó", "ú", "ů", "ý")
      .build();
  public static final Language DA = builder("da", "Danish", Alphabet.LATIN)
      .additionalVowels("æ", "ø", "å")
      .build();
  // TODO #51: How to handle 'ß'?
  public static final Language DE = builder("de", "German", Alphabet.LATIN).build();
  public static final Language EN = builder("en", "English", Alphabet.LATIN)
      // TODO #66: how to deal with Y being consonant and vowel in English?
      //.additionalConsonants("y")
      .build();
  public static final Language ES = builder("es", "Spanish", Alphabet.LATIN)
      .additionalConsonants("ñ")
      .build();
  public static final Language EU = builder("eu", "Basque", Alphabet.LATIN)
      .additionalConsonants("ñ")
      .build();
  public static final Language FI = builder("fi", "Finnish", Alphabet.LATIN)
      .additionalVowels("ä", "ö")
      .build();
  public static final Language FR = builder("fr", "French", Alphabet.LATIN)
      .additionalVowels("æ", "œ")
      .build();
  public static final Language HU = builder("hu", "Hungarian", Alphabet.LATIN)
      .additionalConsonants("cs", "dz", "dzs", "gy", "ly", "ny", "sz", "ty", "zs")
      .additionalVowels("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű")
      .build();
  public static final Language IT = builder("it", "Italian", Alphabet.LATIN).build();
  public static final Language NB = builder("nb", "Norwegian (Bokmål)", Alphabet.LATIN)
      .additionalVowels("æ", "ø", "å")
      .build();
  public static final Language NL = builder("nl", "Dutch", Alphabet.LATIN)
      .additionalVowels("ij")
      .build();
  public static final Language NN = builder("nn", "Norwegian (Nynorsk)", Alphabet.LATIN)
      .additionalVowels("æ", "ø", "å")
      .build();
  public static final Language PL = builder("pl", "Polish", Alphabet.LATIN)
      .additionalConsonants("ć", "ł", "ń", "ś", "ź", "ż")
      .additionalVowels("ą", "ę", "ó")
      .build();
  public static final Language PT = builder("pt", "Portuguese", Alphabet.LATIN).build();
  public static final Language RU = builder("ru", "Russian", Alphabet.CYRILLIC).build();
  public static final Language SR_CYRL = builder("sr-cyrl", "Serbian (Cyrillic)", Alphabet.CYRILLIC).build();
  public static final Language SR_LATN = builder("sr-latn", "Serbian (Latin)", Alphabet.LATIN)
      .additionalConsonants("č", "ć", "dž", "đ", "lj", "nj", "š", "ž")
      .build();
  public static final Language TR = builder("tr", "Turkish", Alphabet.LATIN)
      .additionalConsonants("ç", "ğ", "ş", "y")
      .additionalVowels("ı", "ö", "ü")
      .lettersToRemove("y")
      .build();

  private LanguageData() {
  }

  public static Language getOrThrow(String code) {
    Language language = getOrNull(code);
    if (language == null) {
      throw new IllegalStateException("Unknown language code: " + code);
    }
    return language;
  }

  public static Language getOrNull(String code) {
    return streamThroughAll()
        .filter(lang -> code.equals(lang.getCode()))
        .findFirst().orElse(null);
  }

  public static Stream<Language> streamThroughAll() {
    return Stream.of(AF, BG, CS, DA, DE, EN, ES, EU, FI, FR, HU, IT,
        NB, NL, NN, PL, PT, RU, SR_CYRL, SR_LATN, TR);
  }
}
