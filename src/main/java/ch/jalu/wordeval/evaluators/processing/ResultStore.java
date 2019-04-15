package ch.jalu.wordeval.evaluators.processing;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * Stores the results of an evaluator.
 */
public interface ResultStore<T> {

  /**
   * @return read-only view of the results
   */
  ImmutableList<T> getEntries();

  void addResult(T result);

  default void addResults(Collection<T> results) {
    results.forEach(this::addResult);
  }
}
