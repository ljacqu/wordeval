package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.WordForm.LOWERCASE;
import static ch.ljacqu.wordeval.language.WordForm.NO_ACCENTS;
import static ch.ljacqu.wordeval.language.WordForm.NO_ACCENTS_WORD_CHARS_ONLY;
import static ch.ljacqu.wordeval.language.WordForm.RAW;
import org.apache.commons.lang3.StringUtils;
import ch.ljacqu.wordeval.LetterService;

class WordFormsGenerator {

  private Sanitizer sanitizer;
  private String lettersToKeep;
  private String tempReplacements;

  public WordFormsGenerator(Sanitizer sanitizer) {
    this.sanitizer = sanitizer;
    lettersToKeep = charArrayToString(sanitizer.getAdditionalLetters());
    tempReplacements = initializeTempReplacements(lettersToKeep);
  }

  /**
   * Computes the different word forms (all lowercase, accents removed, etc.)
   * for the given word.
   * @param crudeWord The word to process
   * @return List of all word forms, empty array if the word should be skipped
   */
  String[] computeForms(String line) {
    String word = sanitizer.sanitizeWord(line);
    if (word.isEmpty()) {
      return new String[0];
    }

    String[] wordForms = new String[WordForm.values().length];
    wordForms[RAW.ordinal()] = word;

    String lowerCaseWord = word.toLowerCase(sanitizer.getLocale());
    wordForms[LOWERCASE.ordinal()] = lowerCaseWord;
    wordForms[NO_ACCENTS.ordinal()] = removeNonLetterAccents(lowerCaseWord);
    wordForms[NO_ACCENTS_WORD_CHARS_ONLY.ordinal()] = wordForms[NO_ACCENTS
        .ordinal()].replace("-", "").replace("'", "");
    return wordForms;
  }

  private String removeNonLetterAccents(String word) {
    if (lettersToKeep.isEmpty()) {
      return LetterService.removeAccentsFromWord(word);
    }

    String escapedWord = LetterService.removeAccentsFromWord(StringUtils
        .replaceChars(word, lettersToKeep, tempReplacements));
    return StringUtils.replaceChars(escapedWord, tempReplacements,
        lettersToKeep);
  }

  private static String charArrayToString(char[] arr) {
    StringBuilder builder = new StringBuilder();
    for (char c : arr) {
      builder.append(c);
    }
    return builder.toString();
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
