package ch.ljacqu.wordeval.evaluation.export;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;
import ch.ljacqu.wordeval.evaluation.WordStatEvaluator;

public abstract class ExportObject implements Serializable {

  private static final long serialVersionUID = 1L;

  public final String identifier;

  public ExportObject(String identifier) {
    this.identifier = identifier;
  }

  public static <K> ExportObject create(String identifier, int topKeys,
      Evaluator<K> evaluator) {
    if (evaluator instanceof WordStatEvaluator) {
      return WordStatExport.createInstance(identifier, topKeys,
          ((WordStatEvaluator) evaluator).getResults());
    } else if (evaluator instanceof PartWordEvaluator) {
      return PartWordExport.createInstance(identifier, topKeys,
          ((PartWordEvaluator) evaluator).getResults());
    }
    throw new UnsupportedOperationException(
        "Unknown how to create an ExportResult for evaluator with class "
            + evaluator.getClass());
  }

  protected static final <K, V> NavigableMap<K, V> getBiggestKeys(
      NavigableMap<K, V> map, int number) {
    Iterator<K> descendingIterator = map.descendingKeySet().iterator();
    K key = null;
    for (int i = 0; i < number && descendingIterator.hasNext(); ++i) {
      key = descendingIterator.next();
    }
    if (key != null) {
      return Collections.unmodifiableNavigableMap(map.tailMap(key, true));
    }
    return new TreeMap<>();
  }

  protected static final <K, V> NavigableMap<K, Integer> computeAggregatedMap(
      NavigableMap<K, List<V>> map, K toKey) {
    NavigableMap<K, List<V>> headMap = map.headMap(toKey, false);
    NavigableMap<K, Integer> resultMap = new TreeMap<>();
    for (Map.Entry<K, List<V>> entry : headMap.entrySet()) {
      resultMap.put(entry.getKey(), entry.getValue().size());
    }
    return resultMap;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [identifier=" + identifier + "]";
  }
}
