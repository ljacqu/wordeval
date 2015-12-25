package ch.ljacqu.wordeval.evaluation;

import java.util.Map;
import java.util.Set;

/**
 * Evaluator that finds proper palindromes based on the results of the
 * {@link Palindromes} evaluator, which also matches parts of a word (e.g.
 * "ette" in "better").
 */
public class FullPalindromes extends WordStatEvaluator implements PostEvaluator<Palindromes> {

  /**
   * Filters all words which are proper palindromes (i.e. the entire word is
   * symmetrical).
   * @param palindromes The evaluator whose results should be processed
   */
  @Override
  public void evaluateWith(Palindromes palindromes) {
    for (Map.Entry<String, Set<String>> entry : palindromes.getResults().entrySet()) {
      for (String word : entry.getValue()) {
        if (word.length() == entry.getKey().length()) {
          addEntry(word.length(), word);
        }
      }
    }
  }

  @Override
  public void processWord(String word, String rawWord) {
    // --
  }

  @Override public Class<Palindromes> getType() { return Palindromes.class; }

}
