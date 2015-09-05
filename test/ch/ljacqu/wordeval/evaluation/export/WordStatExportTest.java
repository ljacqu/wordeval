package ch.ljacqu.wordeval.evaluation.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;

public class WordStatExportTest {

  private NavigableMap<Integer, List<String>> results;

  @Before
  public void initialize() {
    // Test setup: List of consecutive vowels, indices 7 through 2
    String[][] words = {
        { "eeueoue" },
        { "weggooiooi", "eeueoud", "spreeueier", "leeueaandeel" },
        { "vloeieenheid", "uieoes", "rooiaarde", "ouooi", "koeioog", "sneeuuil" },
        { "borsplooie", "draaiarm", "dooiety", "eeue", "braaier" },
        { "algoïed", "aloue", "akarioot", "afspieël" },
        { "die", "dae", "lae", "leë", "brood", "leeg", "beweeg", "soek" } };

    results = new TreeMap<>();
    for (int i = 2; i <= 7; ++i) {
      results.put(i, asList(words[7 - i]));
    }
  }

  @Test
  public void shouldBuildExportObjectAccordingToParams() {
    ExportParams params = new ExportParamsBuilder().setMaxTopEntrySize(3)
        .setTopKeys(4).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    assertEquals(export.identifier, "test");

    Map<Integer, List<String>> topEntries = export.getTopEntries();
    assertEquals(topEntries.size(), params.topKeys);
    for (int i = 7; i > 3; --i) {
      assertNotNull(topEntries.get(i));
      assertTrue(topEntries.get(i).size() <= params.maxTopEntrySize + 1);
    }
    assertEquals(getLast(topEntries.get(6)), ExportObject.INDEX_REST + "1");
    assertEquals(getLast(topEntries.get(5)), ExportObject.INDEX_REST + "3");
    assertEquals(getLast(topEntries.get(4)), ExportObject.INDEX_REST + "2");

    assertEquals(export.getAggregatedEntries().size(), 2);
    assertEquals(export.getAggregatedEntries().get(3), new Integer(4));
    assertEquals(export.getAggregatedEntries().get(2), new Integer(8));
  }

  @Test
  public void shouldRespectMinimumKeyParam() {
    ExportParams params = new ExportParamsBuilder().setMinimum(6)
        .setMaxTopEntrySize(null).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    assertEquals(export.getTopEntries().size(), 2);
    assertEquals(export.getTopEntries().get(7), results.get(7));
    assertEquals(export.getTopEntries().get(6), results.get(6));
    assertEquals(export.getAggregatedEntries().size(), 4);
  }

  @Test
  public void shouldHandleEmptyTopEntries() {
    ExportParams params = new ExportParamsBuilder().setTopKeys(0).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    assertTrue(export.getTopEntries().isEmpty());
    assertEquals(export.getAggregatedEntries().size(), 6);
  }

  @Test
  public void shouldBeInDescendingOrder() {
    ExportParams params = new ExportParamsBuilder().setDescending(true)
        .setTopKeys(2).setMinimum(null).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    SortedMap<Integer, List<String>> topEntries = export.getTopEntries();
    assertEquals(topEntries.size(), 2);
    assertEquals(topEntries.firstKey(), new Integer(7));
    assertEquals(topEntries.lastKey(), new Integer(6));
    assertEquals(topEntries.get(6).get(3), "leeueaandeel");

    SortedMap<Integer, Integer> aggregatedEntries = export
        .getAggregatedEntries();
    assertEquals(aggregatedEntries.size(), 4);
    assertEquals(aggregatedEntries.firstKey(), new Integer(5));
    assertEquals(aggregatedEntries.lastKey(), new Integer(2));
  }

  @Test
  public void shouldExportWithDefaultParams() {
    WordStatExport export = WordStatExport.create("default test", results);

    assertEquals(export.identifier, "default test");
    assertNotNull(export.getAggregatedEntries());
    assertNotNull(export.getTopEntries());
  }
  
  @Test
  public void shouldHandleEmptyResult() {
    ExportParams params = new ExportParamsBuilder().build();
    
    WordStatExport export = WordStatExport.create("empty", new TreeMap<>(), params);
    
    assertEquals(export.identifier, "empty");
    assertTrue(export.getAggregatedEntries().isEmpty());
    assertTrue(export.getTopEntries().isEmpty());
  }

  private static <T> List<T> asList(T[] arr) {
    // Arrays.asList returns a list on which add() may not be called
    return new ArrayList<T>(Arrays.asList(arr));
  }

  private static <T> T getLast(List<T> list) {
    if (list.isEmpty()) {
      return null;
    }
    return list.get(list.size() - 1);
  }

}
