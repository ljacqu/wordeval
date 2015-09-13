package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.CYRILLIC;
import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LanguageService {

  private LanguageService() {
  }

  public static String removeAccentsFromWord(String word) {
    word = Normalizer.normalize(word, Normalizer.Form.NFD);
    // Only add lower-case manual replacements as we will intend to only use
    // this with words in the all lower-case form
    return word.replaceAll("\\p{M}", "").replace("ł", "l");
  }

  public static List<String> getLetters(LetterType letterType, Language language) {
    List<String> charList;
    if (letterType.equals(LetterType.VOWELS)) {
      charList = asList(getStandardVowels(language));
      charList.addAll(asList(language.getAdditionalVowels()));
    } else {
      charList = asList(getStandardConsonants(language));
      charList.addAll(asList(language.getAdditionalConsonants()));
    }
    return charList;
  }

  private static String[] getStandardVowels(Language language) {
    if (language.getAlphabet().equals(LATIN)) {
      return new String[] { "a", "e", "i", "o", "u", "y" };
    } else if (language.getAlphabet().equals(CYRILLIC)) {
      return new String[] { "а", "е", "ё", "є", "и", "і", "ї", "о", "у", "ы",
          "э", "ю", "я" };
    }
    throw new IllegalArgumentException("No vowel list known for alphabet "
        + language.getAlphabet());
  }

  private static String[] getStandardConsonants(Language language) {
    if (language.getAlphabet().equals(LATIN)) {
      return new String[] { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m",
          "n", "p", "q", "r", "s", "t", "v", "w", "x", "z" };
    } else if (language.getAlphabet().equals(CYRILLIC)) {
      return new String[] { "б", "в", "г", "ґ", "д", "ђ", "ж", "з", "й", "ј",
          "к", "л", "љ", "м", "н", "њ", "п", "р", "с", "т", "ћ", "ў", "ф", "х",
          "ц", "ч", "џ", "ш", "щ" };
    }
    throw new IllegalArgumentException("No consonant list known for alphabet "
        + language.getAlphabet());
  }

  public static List<Character> computeCharsToPreserve(Language language) {
    List<Character> charsToPreserve = new ArrayList<Character>();
    for (String letter : language.getAdditionalConsonants()) {
      if (letter.length() == 1 && letter.charAt(0) > 127) {
        charsToPreserve.add(letter.charAt(0));
      }
    }
    for (String letter : language.getAdditionalVowels()) {
      if (letter.length() == 1 && letter.charAt(0) > 127) {
        charsToPreserve.add(letter.charAt(0));
      }
    }
    return charsToPreserve;
  }

  private static <T> List<T> asList(T[] items) {
    return new ArrayList<T>(Arrays.asList(items));
  }

}
