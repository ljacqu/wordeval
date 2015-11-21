package ch.ljacqu.wordeval.evaluation.export;

import java.util.Collection;
import java.util.NavigableMap;

/**
 * Test helper for the export package, particularly for casting
 * {@link TreeElement} objects to the proper subtype.
 */
public final class ExportTestHelper {
  private ExportTestHelper() {
  }
  
  /**
   * Returns the typed value of a {@link TreeElement.WordColl} instance.
   * @param el the tree element
   * @return the underlying collection of words
   */
  public static Collection<String> getWordCollValue(TreeElement el) {
    if (el instanceof TreeElement.WordColl) {
      return ((TreeElement.WordColl) el).getTypedValue();
    }
    throw new IllegalStateException("Element '" + el + "' is not a word list");
  }
  
  /**
   * Returns the typed value of a {@link TreeElement.IndexTotalColl} instance.
   * @param el the tree element
   * @return the underlying map with the total per index
   */
  public static NavigableMap<String, Integer> getIndexTotalCollValue(TreeElement el) {
    if (el instanceof TreeElement.IndexTotalColl) {
      return ((TreeElement.IndexTotalColl) el).getTypedValue();
    }
    throw new IllegalStateException("Element '" + el + "' is not an index total collection");
  }
  
  /**
   * Returns the typed value of a {@link TreeElement.Total} instance.
   * @param el the tree element
   * @return the underlying total value
   */
  public static int getTotalValue(TreeElement el) {
    if (el instanceof TreeElement.Total) {
      return ((TreeElement.Total) el).getTypedValue();
    }
    throw new IllegalStateException("Element '" + el + "' is not a Total element");
  }
}
