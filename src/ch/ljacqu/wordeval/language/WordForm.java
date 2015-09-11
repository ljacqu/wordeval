package ch.ljacqu.wordeval.language;

/**
 * Represents the different "word forms" of a word, i.e. the different types of
 * transformations of a dictionary's word. Evaluators specify which word form
 * they would like to receive for processing a dictionary's words.
 */
public enum WordForm {

  /** The raw form after basic sanitation. */
  RAW,

  /** Word in all lowercase. */
  LOWERCASE,

  /** All lowercase word with diacritics removed. */
  NO_ACCENTS,

  /** All lowercase without diacritics and no hyphens or apostrophes. */
  NO_ACCENTS_WORD_CHARS_ONLY

}
