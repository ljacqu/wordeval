package ch.ljacqu.wordeval.language;

interface LanguageSettings {

  String getCode();
  Sanitizer getSanitizer();

}
