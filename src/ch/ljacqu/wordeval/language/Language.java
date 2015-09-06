package ch.ljacqu.wordeval.language;

class Language implements LanguageSettings {
  private final String code;
  private Sanitizer sanitizer;
  private char[] delimiters = {};
  private String[] skipSequences = {};
  private char[] additionalLetters = {};

  Language(String code) {
    this.code = code;
  }

  Language setDelimiters(char... delimiters) {
    this.delimiters = delimiters;
    return this;
  }

  Language setSkipSequences(String... skipSequences) {
    this.skipSequences = skipSequences;
    return this;
  }

  Language setAdditionalLetters(char... additionalLetters) {
    this.additionalLetters = additionalLetters;
    return this;
  }

  @Override
  public Sanitizer getSanitizer() {
    if (sanitizer == null) {
      sanitizer = new Sanitizer(code, delimiters, skipSequences,
          additionalLetters);
    }
    return sanitizer;
  }
  
  @Override
  public String getCode() {
    return code;
  }
}
