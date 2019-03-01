package ch.jalu.wordeval.dictionary;

/**
 * Test implementation of {@link Word} which uses the same text for all forms.
 */
public class TestWord extends Word {

  public TestWord(String word) {
    setRaw(word);
    setLowercase(word);
    setWithoutAccents(word);
    setWithoutAccentsWordCharsOnly(word);
  }

}