package ch.jalu.wordeval.dictionary;

import lombok.AllArgsConstructor;

/**
 * Word with all its word form types.
 */
@AllArgsConstructor
public class Word {

  private String[] wordForms;

  /**
   * Gets a given word form from the given list.
   *
   * @param wordForm the word form type to get
   * @return the requested word form
   */
  public String getForm(WordForm wordForm) {
    return wordForms[wordForm.ordinal()];
  }

}
