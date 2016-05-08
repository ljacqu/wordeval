package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.WordStatExport;

import java.util.Map;

/**
 * Evaluator that finds proper palindromes based on the results of the
 * {@link Palindromes} evaluator, which also matches parts of a word (e.g.
 * "ette" in "better").
 */
public class FullPalindromes extends PostEvaluator<Integer, Palindromes> {

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

  @Override public Class<Palindromes> getType() { return Palindromes.class; }

  // FIXME: IMplemetation from WordStatEvaluator
  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    if (params == null) {
      return WordStatExport.create(identifier, getNavigableResults());
    }
    return WordStatExport.create(identifier, getNavigableResults(), params);
  }
}
