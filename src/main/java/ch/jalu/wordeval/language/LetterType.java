package ch.jalu.wordeval.language;

import java.util.List;
import java.util.function.Function;

/**
 * The type of the letter in an alphabet.
 */
public enum LetterType {

  /** Vowels. */
  VOWELS(Language::getVowels),

  /** Consonants. */
  CONSONANTS(Language::getConsonants);

  private final Function<Language, List<String>> lettersProvider;

  LetterType(Function<Language, List<String>> lettersProvider) {
    this.lettersProvider = lettersProvider;
  }

  public List<String> getLetters(Language language) {
    return lettersProvider.apply(language);
  }

}
