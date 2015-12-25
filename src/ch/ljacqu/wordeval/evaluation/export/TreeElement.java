package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collection;
import java.util.NavigableMap;

import lombok.AllArgsConstructor;

/**
 * Types encapsulating the elements the JSON export result may consist of.
 */
abstract class TreeElement {

  /**
   * Returns the value the object is holding.
   *
   * @return the value of the element
   */
  public abstract Object getValue();

  /**
   * Internal, intermediary class to define and handle a generic type without exposing it to the main
   * {@link TreeElement} class.
   *
   * @param <T> the type the class is representing
   */
  @AllArgsConstructor
  private abstract static class Wrapper<T> extends TreeElement {
    final T value;
    
    public T getTypedValue() {
      return value;
    }
    
    @Override
    public Object getValue() {
      return getTypedValue();
    }
  }

  static final class Rest extends Wrapper<Integer> {
    public Rest(int i) {
      super(i);
    }
  }
  
  static final class Total extends Wrapper<Integer> {
    public Total(int i) {
      super(i);
    }
  }
  
  static final class WordColl extends Wrapper<Collection<String>> {
    public WordColl(Collection<String> c) {
      super(c);
    }
  }
  
  static final class IndexTotalColl extends Wrapper<NavigableMap<String, Integer>> {
    public IndexTotalColl(NavigableMap<String, Integer> map) {
      super(map);
    }
  }
  
}
