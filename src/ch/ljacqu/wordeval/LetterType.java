package ch.ljacqu.wordeval;

public enum LetterType {

  VOWELS,

  CONSONANTS;
  
  public String getName() {
    return this.toString().toLowerCase();
  }

}
