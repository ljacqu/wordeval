package ch.jalu.wordeval.dictionary;

import lombok.AllArgsConstructor;

import static ch.jalu.wordeval.dictionary.WordForm.NO_ACCENTS_WORD_CHARS_ONLY;

/**
 * Word with all its word form types.
 */
@AllArgsConstructor
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

  public String noAccentsWordCharsOnly() {
    return getForm(NO_ACCENTS_WORD_CHARS_ONLY);
  }

}
