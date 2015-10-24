package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Defines how to compute the "relevance" of a PartWordEvaluator's results.
 */
@Getter
public abstract class PartWordReducer {

  private NavigableMap<Number, NavigableMap<String, Set<String>>> topEntries;
  private NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries;

  /**
   * Computes the relevance (how "good" a result is).
   * @param key The key of the entry
   * @param words The collection of words for the key
   * @return The relevance (int or double); the higher the number the better the
   *         result is
   */
  public abstract Number computeRelevance(String key, Collection<String> words);

  /**
   * Reducer keeping the entries with the longest keys.
   */
  public static class ByLength extends PartWordReducer {
    @Override
    public Integer computeRelevance(String key, Collection<String> words) {
      return key.length();
    }
  }

  /**
   * Reducer keeping the entries with the biggest size.
   */
  public static class BySize extends PartWordReducer {
    @Override
    public Integer computeRelevance(String key, Collection<String> words) {
      return words.size();
    }
  }

  /**
   * Reducer determining the top entries by weighed key length and entry size.
   */
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BySizeAndLength extends PartWordReducer {
    double sizeWeight = 1.0;
    double lengthWeight = 1.0;

    @Override
    public Double computeRelevance(String key, Collection<String> words) {
      return key.length() * lengthWeight + words.size() * sizeWeight;
    }
  }

}
