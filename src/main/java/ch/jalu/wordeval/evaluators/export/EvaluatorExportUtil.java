package ch.jalu.wordeval.evaluators.export;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Utilities for evaluators when exporting their results.
 */
public final class EvaluatorExportUtil {

  private EvaluatorExportUtil() {
  }

  /**
   * Creates a new ListMultimap whose keys are kept by insertion order.
   *
   * @return new list multimap with keys that are traversed by insertion order
   */
  public static ListMultimap<Object, Object> newListMultimap() {
    return MultimapBuilder
        .linkedHashKeys()
        .arrayListValues()
        .build();
  }
}
