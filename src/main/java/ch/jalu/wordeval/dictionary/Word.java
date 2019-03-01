package ch.jalu.wordeval.dictionary;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Word with all its word form types.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Word {

  private final String[] wordForms;

  /**
   * Gets a given word form from the given list.
   *
   * @param wordForm the word form type to get
   * @return the requested word form
   */
  public String getForm(WordForm wordForm) {
    return wordForms[wordForm.ordinal()];
  }

  public String getRaw() {
    return getForm(WordForm.RAW);
  }

  public String getLowercase() {
    return getForm(WordForm.LOWERCASE);
  }

  public String getWithoutAccents() {
    return getForm(WordForm.NO_ACCENTS);
  }

  public String getWithoutAccentsWordCharsOnly() {
    return getForm(WordForm.NO_ACCENTS_WORD_CHARS_ONLY);
  }
}
