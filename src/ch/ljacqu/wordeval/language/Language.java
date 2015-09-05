package ch.ljacqu.wordeval.language;

class Language {
  final String code;
  private Sanitizer sanitizer;
  private char[] delimiters = {};
  private String[] skipSequences = {};

  private Language(String code, Sanitizer sanitizer) {
    this.code = code;
    this.sanitizer = sanitizer;
  }

  static Language create(String code) {
    return create(code, null);
  }

  static Language create(String code, Sanitizer sanitizer) {
    Language lang = new Language(code, sanitizer);
    DictionaryLoader.registerLanguage(lang);
    return lang;
  }

  public Language setDelimiters(char... delimiters) {
    this.delimiters = delimiters;
    return this;
  }

  public Language setSkipSequences(String... skipSequences) {
    this.skipSequences = skipSequences;
    return this;
  }

  public Sanitizer getSanitizer() {
    if (sanitizer == null) {
      sanitizer = new Sanitizer(code, delimiters, skipSequences);
    }
    return sanitizer;
  }
}
