package ch.jalu.wordeval.evaluators.processing;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ResultStore}.
 */
public class ResultStoreImpl<T> implements ResultStore<T> {

  private final List<T> entries = new ArrayList<>();

  @Override
  public ImmutableList<T> getEntries() {
    return ImmutableList.copyOf(entries);
  }

  @Override
  public void addResult(T result) {
    entries.add(result);
  }
}
