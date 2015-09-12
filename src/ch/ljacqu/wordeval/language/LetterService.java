package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static java.util.Arrays.asList;
import java.text.Normalizer;
import java.util.List;

public final class LetterService {

  private LetterService() {
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
    }
    throw new IllegalArgumentException("No vowel list known for alphabet "
        + language.getAlphabet());
  }

  private static String[] getStandardConsonants(Language language) {
    if (language.getAlphabet().equals(LATIN)) {
      return new String[] { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m",
          "n", "p", "q", "r", "s", "t", "v", "w", "x", "z" };
    }
    throw new IllegalArgumentException("No consonant list known for alphabet "
        + language.getAlphabet());
  }

  public static String removeAccentsFromWord(String word) {
    word = Normalizer.normalize(word, Normalizer.Form.NFD);
    // Only add lower-case manual replacements as we will intend to only use
    // this with words in the all lower-case form
    return word.replaceAll("\\p{M}", "").replace("ł", "l");
  }

}
