package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Evaluator<K, V> {

  protected Map<K, List<V>> results = new HashMap<K, List<V>>();

  public abstract void processWord(String word);

  protected void addEntry(K key, V entry) {
    if (results.get(key) == null) {
      results.put(key, new ArrayList<V>());
    }
    results.get(key).add(entry);
  }

  public void outputAggregatedResult() {
    for (Entry<K, List<V>> entry : results.entrySet()) {
      outputEntry(entry.getKey(), entry.getValue());
    }
  }

  protected void outputEntry(K key, List<V> entry) {
    System.out.println(key + ": " + entry.size());
  }

}
