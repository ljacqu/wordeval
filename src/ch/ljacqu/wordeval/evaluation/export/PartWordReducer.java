package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.Set;
import lombok.Getter;

@Getter
public abstract class PartWordReducer {

  private NavigableMap<Number, NavigableMap<String, Set<String>>> topEntries;
  private NavigableMap<Number, NavigableMap<String, Integer>> aggregatedEntries;

  /**
   * Computes the relevance (how "good" a result is).
   * @param key The key of the entry
   * @param words The collection of words for the key
   * @return The relevance (typically int or double); the higher the number the
   *         better the result is
   */
  protected abstract Number computeRelevance(String key, Collection<String> words);

  public static class ByLength extends PartWordReducer {
    @Override
    public Integer computeRelevance(String key, Collection<String> words) {
      return key.length();
    }
  }

  public static class BySize extends PartWordReducer {
    @Override
    public Integer computeRelevance(String key, Collection<String> words) {
      return words.size();
    }
  }

  public static class BySizeAndLength extends PartWordReducer {
    double sizePower = 1.0;
    double lengthPower = 1.0;

    public BySizeAndLength() {
    }

    public BySizeAndLength(double sizePower, double lengthPower) {
      this.sizePower = sizePower;
      this.lengthPower = lengthPower;
    }

    @Override
    public Double computeRelevance(String key, Collection<String> words) {
      return Math.pow(key.length(), lengthPower) + Math.pow(words.size(), sizePower);
    }
  }

}
