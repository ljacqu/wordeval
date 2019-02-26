package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import com.google.common.collect.ImmutableMultimap;

import java.util.Collection;

/**
 * Stores the results of an evaluator.
 */
public interface ResultStore {

  /**
   * @return read-only view of the results
   */
  ImmutableMultimap<Double, EvaluatedWord> getEntries();

  /**
   * Fetches all results for the given score (read-only).
   *
   * @param score the key to get the entries for
   * @return the entries for the given key
   */
  Collection<EvaluatedWord> getEntries(Double score);

  void addResult(Word word, EvaluationResult result);

  void addResults(Collection<EvaluatedWord> results);
}
