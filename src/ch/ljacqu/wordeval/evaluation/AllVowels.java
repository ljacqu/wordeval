package ch.ljacqu.wordeval.evaluation;

import java.util.Map;
import java.util.Set;

import ch.ljacqu.wordeval.language.LetterType;
import lombok.AllArgsConstructor;

/**
 * Evaluator that collects words with the most different vowels or consonants.
 */
@AllArgsConstructor
public class AllVowels extends PartWordEvaluator {

  private final LetterType letterType;
  
  @Override
  public void processWord(String word, String rawWord) {
  }

  /**
   * Post evaluator method to collect words with the most vowels or consonants.
   * @param counter the vowel counter to base results on
   */
  @PostEvaluator
  public void postEvaluate(VowelCount counter) {
    Map<String, Set<String>> results = counter.getResults();
    Integer max = results.keySet().stream()
        .map(String::length)
        .max(Integer::compare)
        .orElse(null);
    if (max == null || max == 0) {
      throw new IllegalStateException("No words with letter type?");
    }
    
    results.entrySet().stream()
      .filter(entry -> entry.getKey().length() >= max)
      .forEach(entry -> getResults().put(entry.getKey(), entry.getValue()));
  }
  
  /**
   * Base matcher method.
   * @param counter the instance to investigate
   * @return base match result
   */
  @BaseMatcher
  public boolean isBaseMatch(VowelCount counter) {
    return letterType.equals(counter.getLetterType());
  }
  
  // TODO #50: Set export params to prefer short words with all vowels

}
