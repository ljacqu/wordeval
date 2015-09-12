package ch.ljacqu.wordeval.language;

public enum LetterType {

  VOWELS,

  CONSONANTS;
  
  public String getName() {
    return this.toString().toLowerCase();
  }

}
