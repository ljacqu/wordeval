package ch.jalu.wordeval.evaluation.export;


import lombok.Getter;

import java.util.NavigableMap;

/**
 * Class representing the results of an evaluator in a format suitable for
 * exporting. An object typically has a collection of <i>top entries</i> which
 * are the most extreme/interesting elements the evaluator looks for. The other
 * entries are summed up and displayed as <i>aggregated entries</i>.
 *
 * @param <K> the key of the entries (scores)
 * @param <T> values of top entries
 * @param <A> values of the aggregated entries
 */
@Getter
public abstract class ExportObject<K, T, A> {

  /**
   * String used as special key to include the total of a group.
   */
  public static final String INDEX_TOTAL = "/total";
  /**
   * String used as special key to store the total number of entries that have
   * been trimmed.
   */
  public static final String INDEX_REST = "/rest";

  /**
   * The identifier of the export object (unique name per evaluator/configuration).
   */
  private final String identifier;

  /** List of most relevant entries. */
  private final NavigableMap<K, T> topEntries;

  /** List of aggregated entries. */
  private final NavigableMap<K, A> aggregatedEntries;

  /**
   * Creates a new ExportObject instance.
   *
   * @param identifier the identifier of the new object
   * @param topEntries collection of top entries
   * @param aggregatedEntries collection of aggregated entries
   */
  ExportObject(String identifier, NavigableMap<K, T> topEntries, NavigableMap<K, A> aggregatedEntries) {
    this.identifier = identifier;
    this.topEntries = topEntries;
    this.aggregatedEntries = aggregatedEntries;
  }

}