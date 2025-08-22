package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.sanitizer.FrSanitizer;
import ch.jalu.wordeval.dictionary.sanitizer.HuSanitizer;
import ch.jalu.wordeval.dictionary.sanitizer.ItSanitizer;
import ch.jalu.wordeval.language.Language;

import java.util.stream.Stream;

/**
 * Stores all {@link Dictionary} objects.
 *
 * @see AppData
 */
final class DictionaryData {

  private static final String DICT_PATH = "dict/";

  public static final Dictionary AF = newDictionary("af").delimiters('/').skipSequences(".", "µ", "Ð", "ø").build();
  public static final Dictionary BG = newDictionary("bg").delimiters('/').build();
  public static final Dictionary DA = newDictionary("da").delimiters('/').build();
  public static final Dictionary DE_DE = newDictionary("de-de").delimiters('/', '#').skipSequences("°").build();
  public static final Dictionary EN_US = newDictionary("en-us").delimiters('/').build();
  public static final Dictionary EN_TEST = newDictionary("en-test").delimiters('/').build();
  // TODO #62: Some Basque entries have _ but most parts seem to be present alone
  public static final Dictionary ES = newDictionary("es").delimiters('/').build();
  public static final Dictionary EU = newDictionary("eu").delimiters('/').skipSequences(".", "+", "_").build();
  public static final Dictionary FR = newDictionary("fr").sanitizerCreator(FrSanitizer::new)
      .delimiters('/', '\t')
      .skipSequences(".", "&", "µ", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉", "ᵈ", "ᵉ", "ᵍ", "ˡ", "ᵐ", "ʳ", "ˢ")
      .build();
  public static final Dictionary HU = newDictionary("hu").sanitizerCreator(HuSanitizer::new)
      .delimiters('/', '\t')
      .skipSequences(".", "+", "±", "ø", "ʻ", "’", "­")
      .build();
  public static final Dictionary IT = newDictionary("it").sanitizerCreator(ItSanitizer::new)
      .delimiters('/')
      .build();
  public static final Dictionary NB = newDictionary("nb").delimiters('/').build();
  // TODO: The nl dictionary uses the digraph symbol 'ĳ' instead of 'i'+'j'
  public static final Dictionary NL = newDictionary("nl").delimiters('/').build();
  public static final Dictionary NN = newDictionary("nn").delimiters('/').build();
  public static final Dictionary PL = newDictionary("pl").delimiters('/').build();
  public static final Dictionary PT_BR = newDictionary("pt-br").delimiters('/').build();
  public static final Dictionary PT_PT = newDictionary("pt-pt").delimiters('/', '[').build();
  public static final Dictionary RU = newDictionary("ru").delimiters('/').skipSequences(".").build();
  public static final Dictionary SR_CYRL = newDictionary("sr-cyrl").build();
  public static final Dictionary SR_LATN = newDictionary("sr-latn").delimiters('/').build();
  public static final Dictionary TR = newDictionary("tr").delimiters(' ').build();

  private DictionaryData() {
  }

  public static Dictionary getOrThrow(String code) {
    Dictionary dictionary = getOrNull(code);
    if (dictionary == null) {
      throw new IllegalStateException("Unknown dictionary code: " + code);
    }
    return dictionary;
  }

  public static Dictionary getOrNull(String code) {
    return streamThroughAll()
        .filter(dict -> code.equals(dict.getIdentifier()))
        .findFirst().orElse(null);
  }

  public static Stream<Dictionary> streamThroughAll() {
    return Stream.of(AF, BG, DA, DE_DE, EN_US, EN_TEST, ES, EU, FR, HU, IT,
        NB, NL, NN, PL, PT_BR, PT_PT, RU, SR_CYRL, SR_LATN, TR);
  }

  private static Dictionary.Builder newDictionary(String identifier) {
    return Dictionary.builder()
        .identifier(identifier)
        .file(DICT_PATH + identifier + ".dic")
        .language(getLanguage(identifier));
  }

  private static Language getLanguage(String identifier) {
    Language language = LanguageData.getOrNull(identifier);
    if (language == null) {
      if (identifier.indexOf('-') != -1) {
        return getLanguage(identifier.substring(0, identifier.indexOf('-')));
      }
      throw new IllegalStateException("No language stored for code '" + identifier + "'");
    }
    return LanguageData.getOrThrow(identifier);
  }
}
