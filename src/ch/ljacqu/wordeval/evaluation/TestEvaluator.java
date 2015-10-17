package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.language.LetterType;

// TODO: Remove or change to a test
public class TestEvaluator extends WordStatEvaluator {
  
  private LetterType letterType;
  
  public TestEvaluator(LetterType letterType) {
    this.letterType = letterType;
  }
  
  @Override
  public void processWord(String word, String rawWord) {
    // --
  }
  
  @PostEvaluator
  public void postEvaluate(MonotoneVowel mv) {
    System.out.println("Got mv with results " + mv.getResults());
  }
  
  @BaseMatcher
  public boolean matchesEvaluator(MonotoneVowel mv) {
    return mv.getLetterType().equals(letterType);
  }
}
