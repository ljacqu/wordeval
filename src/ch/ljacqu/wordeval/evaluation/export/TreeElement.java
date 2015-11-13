package ch.ljacqu.wordeval.evaluation.export;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import ch.ljacqu.wordeval.DataUtils;
import lombok.AllArgsConstructor;

abstract class TreeElement {
  
  public abstract Object getValue();
  
  @AllArgsConstructor
  private static class W<T> extends TreeElement {
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
  
  static final class SimpleTopList extends W<NavigableMap<Integer, Set<String>>> {
    public SimpleTopList() {
      this(new TreeMap<Integer, Set<String>>());
    }
    public SimpleTopList(NavigableMap<Integer, Set<String>> map) {
      super(map);
    }
  }
  
  static final class WordColl extends W<Collection<String>> {
    public WordColl(Collection<String> c) {
      super(c);
    }
  }
  
  // TODO: Move to test
  public static void main(String[] args) {
    DataUtils dataUtils = new DataUtils();
    Map<String, TreeElement> map = new HashMap<>();
    map.put("test", new Rest(10));
    map.put("toast", new SimpleTopList());
    ((Map) map.get("toast").getValue()).put(3, new HashSet<>(Arrays.asList("t", "o", "r")));
    System.out.println(dataUtils.toJson(map));
    System.out.println(new GsonExporter().getGson().toJson(map));
  }
  
}
