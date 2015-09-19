package ch.ljacqu.wordeval.dictionary;

import static ch.ljacqu.wordeval.dictionary.WordForm.LOWERCASE;
import static ch.ljacqu.wordeval.dictionary.WordForm.NO_ACCENTS;
import static ch.ljacqu.wordeval.dictionary.WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
import static ch.ljacqu.wordeval.dictionary.WordForm.RAW;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LanguageService;

/**
 * Utility class to generate the various word forms of a word.
 */
class WordFormsBuilder {

  private final Locale locale;
  private final String lettersToKeep;
  private final String tempReplacements;
  private final Alphabet alphabet;

  WordFormsBuilder(Language language) {
    lettersToKeep = charsToString(LanguageService
        .computeCharsToPreserve(language));
    tempReplacements = initializeTempReplacements(lettersToKeep);
    locale = language.buildLocale();
    alphabet = language.getAlphabet();
  }

  /**
   * Computes the different word forms (all lowercase, accents removed, etc.)
   * for the given word.
   * @param word The word to process
   * @return Collection of all word forms; empty array if the word should be
   *         skipped
   */
  String[] computeForms(String word) {
    if (word.isEmpty()) {
      return new String[0];
    }

    String[] wordForms = new String[WordForm.values().length];
    wordForms[RAW.ordinal()] = word;

    String lowerCaseWord = word.toLowerCase(locale);
    wordForms[LOWERCASE.ordinal()] = lowerCaseWord;
    wordForms[NO_ACCENTS.ordinal()] = removeNonLetterAccents(lowerCaseWord);
    wordForms[NO_ACCENTS_WORD_CHARS_ONLY.ordinal()] = wordForms[NO_ACCENTS
        .ordinal()].replace("-", "").replace("'", "");
    return wordForms;
  }

  private String removeNonLetterAccents(String word) {
    if (lettersToKeep.isEmpty()) {
      return LanguageService.removeAccentsFromWord(word, alphabet);
    }

    String escapedWord = LanguageService.removeAccentsFromWord(
        StringUtils.replaceChars(word, lettersToKeep, tempReplacements),
        alphabet);
    return StringUtils.replaceChars(escapedWord, tempReplacements,
        lettersToKeep);
  }

  private static String charsToString(Iterable<Character> letters) {
    StringBuilder sb = new StringBuilder();
    for (char letter : letters) {
      sb.append(letter);
    }
    return sb.toString();
  }

  private static String initializeTempReplacements(String lettersToKeep) {
    if (lettersToKeep.length() > 10) {
      throw new IllegalStateException(
          "Can only support up to 10 additional letters currently; "
              + "please update WordFormsGenerator with more replacements.");
    }

    return "0123456789".substring(0, lettersToKeep.length());
  }
}