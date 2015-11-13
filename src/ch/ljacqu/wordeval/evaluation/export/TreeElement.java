package ch.ljacqu.wordeval.evaluation.export;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.ljacqu.wordeval.DataUtils;
import lombok.AllArgsConstructor;

abstract class TreeElement {
  
  public abstract Object getValue();
  
  @AllArgsConstructor
  private abstract static class W<T> extends TreeElement {
    final T value;
    
    public T getTypedValue() {
      return value;
    }
    
    @Override
    public Object getValue() {
      return getTypedValue();
    }
  }

  static final class Rest extends W<Integer> {
    public Rest(int i) { super(i); }
  }
  
  static final class WordColl extends W<Collection<String>> {
    public WordColl(Collection<String> c) {
      super(c);
    }
  }
  
  static final class IndexTotalColl extends W<NavigableMap<String, Integer>> {
    public IndexTotalColl(NavigableMap<String, Integer> map) {
      super(map);
    }
  }
  
  // TODO: Move to test
  public static void main(String[] args) {
    IndexTotalColl indexTotal = new IndexTotalColl(new TreeMap<>());
    indexTotal.getTypedValue().put("www", 1);
    indexTotal.getTypedValue().put("eee", 2);
    WordColl wordColl = new WordColl(Arrays.asList("tree", "element", "test"));
    
    Map<Double, TreeElement> map = new HashMap<>();
    map.put(23.0, new Rest(10));
    map.put(12.0, indexTotal);
    map.put(6.5, wordColl);

    DataUtils dataUtils = new DataUtils();
    System.out.println(dataUtils.toJson(map));
    System.out.println(ExportService.getGson().toJson(map));
  }
  
}
