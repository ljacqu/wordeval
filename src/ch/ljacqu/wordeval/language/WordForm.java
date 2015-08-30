package ch.ljacqu.wordeval.language;

/**
 * Represents the different "word forms" of a word, i.e. the different types of
 * transformations of a dictionary's word.
 */
public enum WordForm {

  /** The literal line read from the file. */
  RAW_UNSAFE,
  /** The raw form after basic sanitation. */
  RAW,
  /** Word in all lowercase. */
  LOWERCASE,
  /** All lowercase word with diacritics removed. */
  NO_ACCENTS

}
