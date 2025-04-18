package ch.jalu.wordeval.language;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.util.List;

/**
 * The writing system.
 */
public enum Alphabet {

  /** The Latin alphabet. */
  LATIN {
    @Override
    public List<String> getDefaultVowels() {
      return List.of("a", "e", "i", "o", "u", "y");
    }

    @Override
    public List<String> getDefaultConsonants() {
      return List.of(
          "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "z");
    }

    @Override
    public String removeAccents(String lowercaseWord) {
      String decomposedWord = Normalizer.normalize(lowercaseWord, Normalizer.Form.NFD);
      // Only add lower-case manual replacements as we will intend to only use
      // this with words in the all lower-case form
      return StringUtils.replaceChars(decomposedWord.replaceAll("\\p{M}", ""), "łœæ", "loa");
    }
  },
  
  /** The Cyrillic alphabet. */
  CYRILLIC {
    @Override
    public List<String> getDefaultVowels() {
      return List.of("а", "е", "ё", "є", "и", "і", "ї", "о", "у", "ы", "э", "ю", "я");
    }

    @Override
    public List<String> getDefaultConsonants() {
      return List.of("б", "в", "г", "ґ", "д", "ђ", "ж", "з", "й", "ј", "к", "л", "љ", "м", "н", "њ", "п",
          "р", "с", "т", "ћ", "ў", "ф", "х", "ц", "ч", "џ", "ш", "щ");
    }

    @Override
    public String removeAccents(String lowercaseWord) {
      return lowercaseWord.replace("́", "").replace('ѝ', 'и');
    }
  };

  public abstract List<String> getDefaultVowels();

  public abstract List<String> getDefaultConsonants();

  /**
   * Removes all accents (diacritics) from a word's characters.
   *
   * @param lowercaseWord the word to strip accents off (expected in lowercase)
   * @return the word without diacritics
   */
  public abstract String removeAccents(String lowercaseWord);

}
