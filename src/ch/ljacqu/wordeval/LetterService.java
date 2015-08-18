package ch.ljacqu.wordeval;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LetterService {

  private LetterService() {
  }

  public static List<Character> getStandardVowels() {
    Character[] charArray = { 'a', 'e', 'i', 'o', 'u', 'y' };
    return new ArrayList<Character>(Arrays.asList(charArray));
  }

  public static String removeAccentsFromWord(String word) {
    word = Normalizer.normalize(word, Normalizer.Form.NFD);
    return word.replaceAll("\\p{M}", "");
  }

}
