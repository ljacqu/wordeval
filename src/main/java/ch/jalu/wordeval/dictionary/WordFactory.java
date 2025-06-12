package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Constructs an object of a word in different representations, respecting the language's rules.
 */
public class WordFactory {

  private final Locale locale;
  private final String lettersToKeep;
  private final String tempReplacements;
  private final Alphabet alphabet;

  /**
   * Constructor.
   *
   * @param language the language
   */
  public WordFactory(Language language) {
    lettersToKeep = language.getCharsToPreserve();
    tempReplacements = initializeTempReplacements(lettersToKeep);
    locale = language.getLocale();
    alphabet = language.getAlphabet();
  }

  /**
   * Computes the different word forms (all lowercase, accents removed, etc.) for the given word.
   *
   * @param word the word to process in its raw form
   * @return Word with all its forms
   */
  public Word createWordObject(String word) {
    if (word.isEmpty()) {
      throw new IllegalArgumentException("The word may not be empty");
    }

    Word wordObject = new Word();
    wordObject.setRaw(word);
    wordObject.setLowercase(word.toLowerCase(locale));
    wordObject.setWithoutAccents(removeNonLetterAccents(wordObject.getLowercase()));
    wordObject.setWithoutAccentsWordCharsOnly(wordObject.getWithoutAccents()
      .replace("-", "").replace("'", ""));
    return wordObject;
  }

  private String removeNonLetterAccents(String word) {
    if (lettersToKeep.isEmpty()) {
      alphabet.removeAccents(word);
    }

    String escapedWord = alphabet.removeAccents(StringUtils.replaceChars(word, lettersToKeep, tempReplacements));
    return StringUtils.replaceChars(escapedWord, tempReplacements, lettersToKeep);
  }

  private static String initializeTempReplacements(String lettersToKeep) {
    if (lettersToKeep.length() > 20) {
      throw new IllegalStateException("Can only support up to 20 additional letters currently; "
          + "please update WordFactory with more replacements.");
    }

    return "0123456789０１２３４５６７８９".substring(0, lettersToKeep.length());
  }
}
