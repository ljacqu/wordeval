package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collection;
import java.util.NavigableMap;

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
    public Rest(int i) {
      super(i);
    }
  }
  
  static final class Total extends W<Integer> {
    public Total(int i) {
      super(i);
    }
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
  
}
