package ch.jalu.wordeval.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Util methods for Java streams.
 */
public final class StreamUtils {

  private StreamUtils() {
  }

  /**
   * Creates a predicate that evaluates to true only the first time a property mapped by the given function is
   * encountered.
   *
   * @param distinctPropertyMapper property that should be distinct
   * @param <T> stream type
   * @param <K> property type
   * @return predicate for stream with unique keys as provided by the function
   */
  public static <T, K> Predicate<T> distinctByKey(Function<T, K> distinctPropertyMapper) {
    Set<K> seen = new HashSet<>();
    return t -> seen.add(distinctPropertyMapper.apply(t));
  }
}
