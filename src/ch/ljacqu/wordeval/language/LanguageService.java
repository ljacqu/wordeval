package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.CYRILLIC;
import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public final class LanguageService {

  private LanguageService() {
  }

  public static String removeAccentsFromWord(String word, Alphabet alphabet) {
    if (alphabet.equals(CYRILLIC)) {
      return word.replace("́", "").replace('ѝ', 'и');
    }

    word = Normalizer.normalize(word, Normalizer.Form.NFD);
    // Only add lower-case manual replacements as we will intend to only use
    // this with words in the all lower-case form
    // TODO: Not sure if ł replacement is still necessary
    return word.replaceAll("\\p{M}", "");
  }

  public static List<String> getLetters(LetterType letterType, Language language) {
    List<String> charList;
    if (letterType.equals(LetterType.VOWELS)) {
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
    if (language.getAlphabet().equals(LATIN)) {
      return asList(exclusions, "a", "e", "i", "o", "u", "y");
    } else if (language.getAlphabet().equals(CYRILLIC)) {
      return asList(exclusions, "а", "е", "ё", "є", "и", "і", "ї", "о", "у",
          "ы", "э", "ю", "я");
    }
    throw new IllegalArgumentException("No vowel list known for alphabet "
        + language.getAlphabet());
  }

  private static List<String> getStandardConsonants(Language language) {
    String[] exclusions = language.getLettersToRemove();
    if (language.getAlphabet().equals(LATIN)) {
      return asList(exclusions, "b", "c", "d", "f", "g", "h", "j", "k", "l",
          "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "z");
    } else if (language.getAlphabet().equals(CYRILLIC)) {
      return asList(exclusions, "б", "в", "г", "ґ", "д", "ђ", "ж", "з", "й",
          "ј", "к", "л", "љ", "м", "н", "њ", "п", "р", "с", "т", "ћ", "ў", "ф",
          "х", "ц", "ч", "џ", "ш", "щ");
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

  private static List<String> asList(String... items) {
    return new ArrayList<String>(Arrays.asList(items));
  }

  private static List<String> asList(String[] exclusions, String... items) {
    return asList(ArrayUtils.removeElements(items, exclusions));
  }

}
