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


  // Override setters to make them public instead of package-private

  @Override
  public void setLowercase(String lowercase) {
    super.setLowercase(lowercase);
  }

  @Override
  public void setWithoutAccents(String withoutAccents) {
    super.setWithoutAccents(withoutAccents);
  }

  @Override
  public void setWithoutAccentsWordCharsOnly(String withoutAccentsWordCharsOnly) {
    super.setWithoutAccentsWordCharsOnly(withoutAccentsWordCharsOnly);
  }
}