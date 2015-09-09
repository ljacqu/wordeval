package ch.ljacqu.wordeval.evaluation.export;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;

public class PartWordExportTest {

  private Map<String, Set<String>> results;

  @Before
  public void initialize() {
    // Test data: list of words with a palindrome part in it
    results = new TreeMap<>();

    // Length 9
    results.put("taalplaat", asSet("metaalplaat", "staalplaat"));
    results.put("ittesetti", asSet("hittesetting"));
    results.put("sigologis", asSet("psigologisme"));

    // Length 8
    results.put("aarddraa", asSet("aarddraad"));
    results.put("erettere", asSet("veretterende"));
    results.put("kaarraak", asSet("deurmekaarraak"));

    // Length 7
    Set<String> wordList = asSet("spesifisering", "gespesifiseer",
        "gespesifiseerd", "spesifiseer");
    results.put("esifise", wordList);

    // Length 6
    results.put("millim", asSet("millimeter"));
    results.put("neffen", asSet("neffens", "hierneffens", "oneffenheid"));
    results.put("marram", asSet("marram"));
    results.put("leggel", asSet("inleggeld"));
    results.put("gerreg", asSet("burgerreg"));
    results.put("eellee", asSet("teëllêer"));
    results.put("arkkra", asSet("markkrag"));

    // Length 5
    results.put("alkla",
        asSet("smalklap", "taalklas", "vokaalklank", "taalklank"));
    results.put("anana", asSet("ananas"));
    results.put("aadaa", asSet("daeraadaap"));
  }

  @Test
  public void shouldExportWithTopKeys() {
    ExportParams params = new ExportParamsBuilder().setTopKeys(3)
        .setMaxTopEntrySize(null).build();

    PartWordExport export = PartWordExport.create("a test", results, params);

    assertEquals(export.identifier, "a test");

    Map<Integer, SortedMap<String, Object>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(3));
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
    assertThat(aggregatedEntries, aMapWithSize(2));
    assertEquals(aggregatedEntries.get(6).size(), 7);
    assertThat(aggregatedEntries.get(6).get("gerreg"), equalTo(1));
    assertThat(aggregatedEntries.get(6).get("neffen"), equalTo(3));
    assertEquals(aggregatedEntries.get(5).size(), 3);
    assertThat(aggregatedEntries.get(5).get("alkla"), equalTo(4));
  }

  @Test
  public void shouldRespectMaxParams() {
    ExportParams params = new ExportParamsBuilder().setMaxTopEntrySize(4)
        .setMaxPartWordListSize(2).setTopKeys(4).setMinimum(2).build();

    PartWordExport export = PartWordExport.create("test", results, params);

    Map<Integer, SortedMap<String, Object>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(4));
    assertEquals(topEntries.get(9).size(), 3);
    String[] expected = { "metaalplaat", "staalplaat" };
    assertArrayEquals(toArray(topEntries.get(9).get("taalplaat")), expected);
    assertEquals(topEntries.get(8).size(), 3);
    assertEquals(toArray(topEntries.get(8).get("kaarraak"))[0],
        "deurmekaarraak");

    assertEquals(topEntries.get(7).size(), 1);
    checkReducedList(toArray(topEntries.get(7).get("esifise")),
        results.get("esifise"), 2);

    assertEquals(topEntries.get(6).size(), 5);
    assertEquals(topEntries.get(6).firstKey(), ExportObject.INDEX_REST);
    assertEquals(topEntries.get(6).get(ExportObject.INDEX_REST), 3);

    Map<Integer, SortedMap<String, Integer>> aggregatedEntries = export
        .getAggregatedEntries();
    assertEquals(aggregatedEntries.size(), 1);
    assertEquals(aggregatedEntries.get(5).size(), 3);
    assertThat(aggregatedEntries.get(5).get("anana"), equalTo(1));
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
    Integer[] expectedKeys2 = { 7, 6, 5 };
    assertArrayEquals(aggregatedEntries.keySet().toArray(), expectedKeys2);
    assertThat(aggregatedEntries.get(6).get("neffen"), equalTo(3));
    assertThat(aggregatedEntries.get(6).get("eellee"), equalTo(1));
  }

  @Test
  public void shouldHandleEmptyResult() {
    PartWordExport export = PartWordExport
        .create("empty test", new TreeMap<>());

    assertEquals(export.identifier, "empty test");
    assertTrue(export.getTopEntries().isEmpty());
    assertTrue(export.getAggregatedEntries().isEmpty());
  }

  private static void checkReducedList(String[] list,
      Set<String> originalWords, int restTotal) {
    for (int i = 0; i < list.length - 1; ++i) {
      if (!originalWords.contains(list[i])) {
        fail("Found word '" + list[i] + "' which is not in given array.");
      }
    }
    assertEquals(list[list.length - 1], ExportObject.INDEX_REST + restTotal);
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

  private static Set<String> asSet(String... words) {
    return new HashSet<String>(Arrays.asList(words));
  }

}
