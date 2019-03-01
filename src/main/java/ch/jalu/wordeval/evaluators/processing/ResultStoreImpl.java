package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of {@link ResultStore}.
 */
public class ResultStoreImpl implements ResultStore {

  private final Multimap<Double, EvaluatedWord> entries = HashMultimap.create();

  @Override
  public ImmutableMultimap<Double, EvaluatedWord> getEntries() {
    return ImmutableMultimap.copyOf(entries);
  }

  @Override
  public Collection<EvaluatedWord> getEntries(Double score) {
    return Collections.unmodifiableCollection(entries.get(score));
  }

  @Override
  public void addResult(Word word, EvaluationResult result) {
    entries.put(result.getScore(), new EvaluatedWord(word, result));
  }

  @Override
  public void addResults(Collection<EvaluatedWord> results) {
    results.forEach(evalWord -> entries.put(evalWord.getResult().getScore(), evalWord));
  }
}
