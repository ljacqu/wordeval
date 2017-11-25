package ch.jalu.wordeval.evaluation.export;

import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.NavigableMap;

/**
 * Types encapsulating the elements the JSON export result may consist of.
 */
abstract class TreeElement {

  /**
   * Returns the value the object is holding.
   *
   * @return the value of the element
   */
  abstract Object getValue();

  /**
   * Internal, intermediary class to define and handle a generic type without exposing it to the main
   * {@link TreeElement} class.
   *
   * @param <T> the type the class is representing
   */
  @AllArgsConstructor
  private abstract static class Wrapper<T> extends TreeElement {
    private final T value;
    
    T getTypedValue() {
      return value;
    }
    
    @Override
    Object getValue() {
      return getTypedValue();
    }
  }

  static final class Rest extends Wrapper<Integer> {
    Rest(int i) {
      super(i);
    }
  }
  
  static final class Total extends Wrapper<Integer> {
    Total(int i) {
      super(i);
    }
  }
  
  static final class WordColl extends Wrapper<Collection<String>> {
    WordColl(Collection<String> c) {
      super(c);
    }
  }
  
  static final class IndexTotalColl extends Wrapper<NavigableMap<String, Integer>> {
    IndexTotalColl(NavigableMap<String, Integer> map) {
      super(map);
    }
  }
  
}
