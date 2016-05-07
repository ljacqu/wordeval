package ch.jalu.wordeval.language;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Service for Language objects.
 */
public final class LanguageService {

  private LanguageService() {
  }

  /**
   * Removes all accents from a word's characters.
   * @param word the word to strip accents off
   * @param alphabet the alphabet the word is written in
   * @return the word without diacritics
   */
  public static String removeAccentsFromWord(String word, Alphabet alphabet) {
    if (alphabet.equals(Alphabet.CYRILLIC)) {
      return word.replace("́", "").replace('ѝ', 'и');
    }

    String decomposedWord = Normalizer.normalize(word, Normalizer.Form.NFD);
    // Only add lower-case manual replacements as we will intend to only use
    // this with words in the all lower-case form
    return StringUtils.replaceChars(decomposedWord.replaceAll("\\p{M}", ""), "łœæ", "loa");
  }

  /**
   * Returns a list of the given letter type for the given language.
   * @param letterType the letter type to retrieve
   * @param language the language of the list to retrieve
   * @return the list according to the parameters
   */
  public static List<String> getLetters(LetterType letterType, Language language) {
    List<String> charList;
    if (LetterType.VOWELS.equals(letterType)) {
      charList = getStandardVowels(language);
      charList.addAll(asList(language.getAdditionalVowels()));
    } else {
      charList = getStandardConsonants(language);
      charList.addAll(asList(language.getAdditionalConsonants()));
    }
    return charList;
  }

  private static List<String> getStandardVowels(Language language) {
    String[] exclusions = language.getLettersToRemove();
    if (Alphabet.LATIN.equals(language.getAlphabet())) {
      return asList(exclusions, "a", "e", "i", "o", "u", "y");
    } else if (Alphabet.CYRILLIC.equals(language.getAlphabet())) {
      return asList(exclusions, "а", "е", "ё", "є", "и", "і", "ї", "о", "у", "ы", "э", "ю", "я");
    }
    throw new IllegalArgumentException("No vowel list known for alphabet " + language.getAlphabet());
  }

  private static List<String> getStandardConsonants(Language language) {
    String[] exclusions = language.getLettersToRemove();
    if (Alphabet.LATIN.equals(language.getAlphabet())) {
      return asList(exclusions, "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v",
          "w", "x", "z");
    } else if (Alphabet.CYRILLIC.equals(language.getAlphabet())) {
      return asList(exclusions, "б", "в", "г", "ґ", "д", "ђ", "ж", "з", "й", "ј", "к", "л", "љ", "м", "н", "њ", "п",
          "р", "с", "т", "ћ", "ў", "ф", "х", "ц", "ч", "џ", "ш", "щ");
    }
    throw new IllegalArgumentException("No consonant list known for alphabet " + language.getAlphabet());
  }

  private static List<String> asList(String... items) {
    return new ArrayList<>(Arrays.asList(items));
  }

  /**
   * Returns a list with the given items besides the given exclusions.
   * @param exclusions the characters to exclude
   * @param items the items to process
   * @return list of the items without any of the exclusions
   */
  private static List<String> asList(String[] exclusions, String... items) {
    return asList(ArrayUtils.removeElements(items, exclusions));
  }

}