package ch.ljacqu.wordeval.evaluation;

import java.util.Map;

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
    palindromes.getResults().entries()
        .stream()
        .filter(entry -> entry.getKey().length() == entry.getValue().length())
        .map(Map.Entry::getValue)
        .forEach(word -> addEntry(word.length(), word));
  }

  @Override
  public void processWord(String word, String rawWord) {
    // --
  }

  @Override public Class<Palindromes> getType() { return Palindromes.class; }

}
