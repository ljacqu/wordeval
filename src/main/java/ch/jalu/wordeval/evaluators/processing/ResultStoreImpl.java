package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of {@link ResultStore}.
 */
class ResultStoreImpl<K extends Comparable<K>> implements ResultStore<K> {

  private final Multimap<K, EvaluatedWord<K>> entries = TreeMultimap.create();

  @Override
  public Multimap<K, EvaluatedWord<K>> getEntries() {
    return ImmutableMultimap.copyOf(entries);
  }

  @Override
  public Collection<EvaluatedWord<K>> getEntries(K score) {
    return Collections.unmodifiableCollection(entries.get(score));
  }

  public void addResult(Word word, EvaluationResult<K> result) {
    entries.put(result.getScore(), new EvaluatedWord<>(word, result));
  }

  public void addResults(Collection<EvaluatedWord<K>> results) {
    results.forEach(evalWord -> entries.put(evalWord.getResult().getScore(), evalWord));
  }
}
