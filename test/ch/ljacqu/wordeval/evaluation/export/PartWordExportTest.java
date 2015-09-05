package ch.ljacqu.wordeval.evaluation.export;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;

public class PartWordExportTest {

  private NavigableMap<String, List<String>> results;

  @Before
  public void initialize() {
    // Test data: list of words with a palindrome part in it
    results = new TreeMap<>();

    // Length 9
    results.put("taalplaat", asList("metaalplaat", "staalplaat"));
    results.put("ittesetti", asList("hittesetting"));
    results.put("sigologis", asList("psigologisme"));

    // Length 8
    results.put("aarddraa", asList("aarddraad"));
    results.put("erettere", asList("veretterende"));
    results.put("kaarraak", asList("deurmekaarraak"));

    // Length 7
    List<String> wordList = asList("spesifisering", "gespesifiseer",
        "gespesifiseerd", "spesifiseer");
    results.put("esifise", wordList);

    // Length 6
    results.put("millim", asList("millimeter"));
    results.put("neffen", asList("neffens", "hierneffens", "oneffenheid"));
    results.put("marram", asList("marram"));
    results.put("leggel", asList("inleggeld"));
    results.put("gerreg", asList("burgerreg"));
    results.put("eellee", asList("teëllêer"));
    results.put("arkkra", asList("markkrag"));

    // Length 5
    results.put("alkla",
        asList("smalklap", "taalklas", "vokaalklank", "taalklank"));
    results.put("anana", asList("ananas"));
    results.put("aadaa", asList("daeraadaap"));
  }

  @Test
  public void shouldExportWithTopKeys() {
    ExportParams params = new ExportParamsBuilder().setTopKeys(3)
        .setMaxTopEntrySize(null).build();

    PartWordExport export = PartWordExport.create("a test", results, params);

    assertEquals(export.identifier, "a test");

    Map<Integer, SortedMap<String, Object>> topEntries = export.getTopEntries();
    assertEquals(topEntries.size(), 3);
    assertEquals(topEntries.get(9).size(), 3);
    assertEquals(toArray(topEntries.get(9).get("taalplaat")).length, 2);
    assertEquals(topEntries.get(8).size(), 3);
    assertEquals(toArray(topEntries.get(8).get("erettere"))[0], "veretterende");
    assertEquals(toArray(topEntries.get(8).get("kaarraak"))[0],
        "deurmekaarraak");
    assertEquals(topEntries.get(7).size(), 1);
    assertEquals(toArray(topEntries.get(7).get("esifise")).length, 4);

    Map<Integer, SortedMap<String, Integer>> aggregatedEntries = export
        .getAggregatedEntries();
    assertEquals(aggregatedEntries.size(), 2);
    assertEquals(aggregatedEntries.get(6).size(), 7);
    assertEqlInt(aggregatedEntries.get(6).get("gerreg"), 1);
    assertEqlInt(aggregatedEntries.get(6).get("neffen"), 3);
    assertEquals(aggregatedEntries.get(5).size(), 3);
    assertEqlInt(aggregatedEntries.get(5).get("alkla"), 4);
  }

  @Test
  public void shouldRespectMaxParams() {
    ExportParams params = new ExportParamsBuilder().setMaxTopEntrySize(4)
        .setMaxPartWordListSize(2).setTopKeys(4).setMinimum(2).build();

    PartWordExport export = PartWordExport.create("test", results, params);

    Map<Integer, SortedMap<String, Object>> topEntries = export.getTopEntries();
    assertEquals(topEntries.size(), 4);
    assertEquals(topEntries.get(9).size(), 3);
    String[] expected = { "metaalplaat", "staalplaat" };
    assertArrayEquals(toArray(topEntries.get(9).get("taalplaat")), expected);
    assertEquals(topEntries.get(8).size(), 3);
    assertEquals(toArray(topEntries.get(8).get("kaarraak"))[0],
        "deurmekaarraak");

    assertEquals(topEntries.get(7).size(), 1);
    String[] expected2 = { "spesifisering", "gespesifiseer",
        ExportObject.INDEX_REST + "2" };
    assertArrayEquals(toArray(topEntries.get(7).get("esifise")), expected2);

    assertEquals(topEntries.get(6).size(), 5);
    assertEquals(topEntries.get(6).firstKey(), ExportObject.INDEX_REST);
    assertEquals(topEntries.get(6).get(ExportObject.INDEX_REST), 3);

    Map<Integer, SortedMap<String, Integer>> aggregatedEntries = export
        .getAggregatedEntries();
    assertEquals(aggregatedEntries.size(), 1);
    assertEquals(aggregatedEntries.get(5).size(), 3);
    assertEqlInt(aggregatedEntries.get(5).get("anana"), 1);
  }

  @Test
  public void shouldUseDescendingOrder() {
    ExportParams params = new ExportParamsBuilder().setDescending(true)
        .setTopKeys(10).setMinimum(8).build();

    PartWordExport export = PartWordExport.create("test", results, params);

    Map<Integer, SortedMap<String, Object>> topEntries = export.getTopEntries();
    assertEquals(topEntries.size(), 2);
    Integer[] expectedKeys = { 9, 8 };
    assertArrayEquals(topEntries.keySet().toArray(), expectedKeys);
    assertEquals(topEntries.get(9).size(), 3);
    assertEquals(topEntries.get(8).size(), 3);

    Map<Integer, SortedMap<String, Integer>> aggregatedEntries = export
        .getAggregatedEntries();
    assertEquals(aggregatedEntries.size(), 3);
    Integer[] expectedKeys2 = {7, 6, 5};
    assertArrayEquals(aggregatedEntries.keySet().toArray(), expectedKeys2);
    assertEqlInt(aggregatedEntries.get(6).get("neffen"), 3);
    assertEqlInt(aggregatedEntries.get(6).get("eellee"), 1);
  }

  @Test
  public void shouldHandleEmptyResult() {
    PartWordExport export = PartWordExport
        .create("empty test", new TreeMap<>());

    assertEquals(export.identifier, "empty test");
    assertTrue(export.getTopEntries().isEmpty());
    assertTrue(export.getAggregatedEntries().isEmpty());
  }

  /**
   * Quick fix for assertEquals(Integer, int) not being possible.
   * @param i The left-hand side
   * @param j The right-hand side
   */
  private static void assertEqlInt(Integer i, int j) {
    assertEquals(i.intValue(), j);
  }

  /**
   * Checks if an Object type is a String array and returns the cast version if
   * this is the case. Fails otherwise.
   * @param entry The entry to process and cast
   * @return The cast version of the object
   */
  private static String[] toArray(Object entry) {
    if (entry instanceof String[]) {
      return (String[]) entry;
    }
    fail("Entry '" + entry + "' is not a string array!");
    return null;
  }

  private static List<String> asList(String... words) {
    // Arrays.asList does not allow add(), so wrap it with ArrayList
    return new ArrayList<String>(Arrays.asList(words));
  }

}
