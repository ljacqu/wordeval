package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Utility class used in {@link Sanitizer} to 
 * generate the various word forms of a word.
 */
public class WordFormsBuilder {

  private final Locale locale;
  private final String lettersToKeep;
  private final String tempReplacements;
  private final Alphabet alphabet;

  /**
   * Constructor.
   *
   * @param language the language
   */
  public WordFormsBuilder(Language language) {
    lettersToKeep = language.getCharsToPreserve();
    tempReplacements = initializeTempReplacements(lettersToKeep);
    locale = language.getLocale();
    alphabet = language.getAlphabet();
  }

  /**
   * Computes the different word forms (all lowercase, accents removed, etc.) for the given word.
   *
   * @param word the word to process
   * @return Word with all its forms
   */
  public Word computeForms(String word) {
    if (word.isEmpty()) {
      throw new IllegalStateException("The word may not be empty");
    }

    String[] wordForms = new String[WordForm.values().length];
    wordForms[WordForm.RAW.ordinal()] = word;

    String lowerCaseWord = word.toLowerCase(locale);
    wordForms[WordForm.LOWERCASE.ordinal()] = lowerCaseWord;
    wordForms[WordForm.NO_ACCENTS.ordinal()] = removeNonLetterAccents(lowerCaseWord);
    wordForms[WordForm.NO_ACCENTS_WORD_CHARS_ONLY.ordinal()] = wordForms[WordForm.NO_ACCENTS.ordinal()]
        .replace("-", "").replace("'", "");
    return new Word(wordForms);
  }

  private String removeNonLetterAccents(String word) {
    if (lettersToKeep.isEmpty()) {
      alphabet.removeAccents(word);
    }

    String escapedWord = alphabet.removeAccents(StringUtils.replaceChars(word, lettersToKeep, tempReplacements));
    return StringUtils.replaceChars(escapedWord, tempReplacements, lettersToKeep);
  }

  private static String initializeTempReplacements(String lettersToKeep) {
    if (lettersToKeep.length() > 10) {
      throw new IllegalStateException("Can only support up to 10 additional letters currently; "
          + "please update WordFormsGenerator with more replacements.");
    }

    return "0123456789".substring(0, lettersToKeep.length());
  }
}
