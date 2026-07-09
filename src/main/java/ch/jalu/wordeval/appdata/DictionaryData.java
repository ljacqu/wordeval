package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.FrLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.HuLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.ItLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.NlLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.NoLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.PtPtLineProcessor;
import ch.jalu.wordeval.language.Language;

import java.util.stream.Stream;

/**
 * Stores all {@link Dictionary} objects.
 *
 * @see AppData
 */
final class DictionaryData {

  private static final String DICT_PATH = "dict/";

  public static final Dictionary AF = hunspellDictionary("af").lineProcessor(".", "µ", "Ð", "ø").build();
  public static final Dictionary BG = hunspellDictionary("bg").build();
  public static final Dictionary DA = hunspellDictionary("da").build();
  public static final Dictionary DE_DE = hunspellDictionary("de-de").lineProcessor("#", "°").build();
  public static final Dictionary EN_US = hunspellDictionary("en-us").build();
  public static final Dictionary EN_TEST = hunspellDictionary("en-test").build();
  // TODO #62: Some Basque entries have _ but most parts seem to be present alone
  public static final Dictionary ES = hunspellDictionary("es").build();
  public static final Dictionary EU = hunspellDictionary("eu").lineProcessor(".", "+", "_").build();
  public static final Dictionary FR = hunspellDictionary("fr").lineProcessor(new FrLineProcessor()).build();
  public static final Dictionary HU = hunspellDictionary("hu").lineProcessor(new HuLineProcessor()).build();
  public static final Dictionary IT = hunspellDictionary("it").lineProcessor(new ItLineProcessor()).build();
  public static final Dictionary NB = hunspellDictionary("nb").lineProcessor(new NoLineProcessor()).build();
  public static final Dictionary NL = hunspellDictionary("nl").lineProcessor(new NlLineProcessor()).build();
  public static final Dictionary NN = hunspellDictionary("nn").lineProcessor(new NoLineProcessor()).build();
  public static final Dictionary PL = hunspellDictionary("pl").lineProcessor(".", "uuu").build();
  public static final Dictionary PT_BR = hunspellDictionary("pt-br").build();
  public static final Dictionary PT_PT = hunspellDictionary("pt-pt").lineProcessor(new PtPtLineProcessor()).build();
  public static final Dictionary RU = hunspellDictionary("ru").lineProcessor(".").build();
  public static final Dictionary SR_CYRL = hunspellDictionary("sr-cyrl").build();
  public static final Dictionary SR_LATN = hunspellDictionary("sr-latn").build();
  public static final Dictionary TR = hunspellDictionary("tr").build();

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

  private static HunspellDictionary.Builder hunspellDictionary(String identifier) {
    return HunspellDictionary.newHunspellDictionary(identifier)
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
