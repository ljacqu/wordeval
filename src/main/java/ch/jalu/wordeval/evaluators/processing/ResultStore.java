package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.EvaluatedWord;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * Stores the results of an evaluator.
 */
public interface ResultStore<K extends Comparable<K>> {

  /**
   * @return read-only view of the results
   */
  Multimap<K, EvaluatedWord<K>> getEntries();

  /**
   * Fetches all results for the given score (read-only).
   *
   * @param score the key to get the entries for
   * @return the entries for the given key
   */
  Collection<EvaluatedWord<K>> getEntries(K score);

}
