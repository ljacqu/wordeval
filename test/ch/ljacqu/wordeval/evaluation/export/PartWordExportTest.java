package ch.ljacqu.wordeval.evaluation.export;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static ch.ljacqu.wordeval.TestUtil.toSet;
import static ch.ljacqu.wordeval.TestUtil.toColl;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
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
    results.put("esifise", asSet("spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer"));

    // Length 6
    results.put("millim", asSet("millimeter"));
    results.put("neffen", asSet("neffens", "hierneffens", "oneffenheid"));
    results.put("marram", asSet("marram"));
    results.put("leggel", asSet("inleggeld"));
    results.put("gerreg", asSet("burgerreg"));
    results.put("eellee", asSet("teëllêer"));
    results.put("arkkra", asSet("markkrag"));

    // Length 5
    results.put("alkla", asSet("smalklap", "taalklas", "vokaalklank", "taalklank"));
    results.put("anana", asSet("ananas"));
    results.put("aadaa", asSet("daeraadaap"));
  }

  @Test
  public void shouldExportWithTopKeys() {
    ExportParams params = ExportParams.builder()
        .topKeys(3)
        .maxTopEntrySize(Optional.empty())
        .build();

    PartWordExport export = PartWordExport.create("a test", results, params, new PartWordReducer.ByLength());

    assertEquals(export.identifier, "a test");

    Map<Double, NavigableMap<String, Object>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(3));
    assertThat(topEntries.get(9.0), aMapWithSize(3));
    assertThat(toSet(topEntries.get(9.0).get("taalplaat")), containsInAnyOrder("metaalplaat", "staalplaat"));
    assertThat(toSet(topEntries.get(9.0).get("ittesetti")), contains("hittesetting"));
    assertThat(toSet(topEntries.get(9.0).get("sigologis")), contains("psigologisme"));
    assertThat(topEntries.get(8.0).keySet(), containsInAnyOrder("aarddraa", "erettere", "kaarraak"));
    assertThat(toSet(topEntries.get(8.0).get("kaarraak")), contains("deurmekaarraak"));
    assertThat(topEntries.get(7.0), aMapWithSize(1));
    assertThat(toSet(topEntries.get(7.0).get("esifise")),
        containsInAnyOrder("spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer"));

    Map<Double, NavigableMap<String, Integer>> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries, aMapWithSize(2));
    assertThat(aggregatedEntries.get(6.0).keySet(),
        containsInAnyOrder("millim", "neffen", "marram", "leggel", "gerreg", "eellee", "arkkra"));
    assertThat(aggregatedEntries.get(6.0).get("millim"), equalTo(1));
    assertThat(aggregatedEntries.get(6.0).get("neffen"), equalTo(3));
    assertThat(aggregatedEntries.get(6.0).get("eellee"), equalTo(1));
    assertThat(aggregatedEntries.get(5.0).keySet(), containsInAnyOrder("alkla", "anana", "aadaa"));
    assertThat(aggregatedEntries.get(5.0).get("alkla"), equalTo(4));
    assertThat(aggregatedEntries.get(5.0).get("anana"), equalTo(1));
    assertThat(aggregatedEntries.get(5.0).get("aadaa"), equalTo(1));
  }

  @Test
  public void shouldRespectMaxParams() {
    ExportParams params = ExportParams.builder()
        .maxTopEntrySize(Optional.of(4))
        .maxPartWordListSize(Optional.of(2))
        .topKeys(4)
        .topEntryMinimum(Optional.of(2.0))
        .build();

    PartWordExport export = PartWordExport.create("test", results, params, new PartWordReducer.ByLength());

    Map<Double, NavigableMap<String, Object>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(4));
    assertThat(topEntries.get(9.0), aMapWithSize(3));
    assertThat(toSet(topEntries.get(9.0).get("taalplaat")), containsInAnyOrder("metaalplaat", "staalplaat"));
    assertThat(toSet(topEntries.get(9.0).get("ittesetti")), contains("hittesetting"));
    assertThat(toSet(topEntries.get(9.0).get("sigologis")), contains("psigologisme"));
    assertThat(topEntries.get(8.0).keySet(), containsInAnyOrder("aarddraa", "erettere", "kaarraak"));
    assertThat(toSet(topEntries.get(8.0).get("erettere")), contains("veretterende"));
    
    assertThat(topEntries.get(7.0), aMapWithSize(1));
    Object[] allowedItems = { "spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer" };
    checkReducedList(topEntries.get(7.0).get("esifise"), params, allowedItems);

    assertThat(topEntries.get(6.0), aMapWithSize(params.maxTopEntrySize.get() + 1));
    Set<String> foundKeysSet = topEntries.get(6.0).keySet();
    String[] foundKeys = foundKeysSet.toArray(new String[foundKeysSet.size()]);
    assertThat(asSet("millim", "neffen", "marram", "leggel", "gerreg", "eellee", "arkkra", ExportObject.INDEX_REST),
        hasItems(foundKeys));
    assertThat(topEntries.get(6.0).get(ExportObject.INDEX_REST), equalTo("3"));

    Map<Double, NavigableMap<String, Integer>> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries, aMapWithSize(1));
    assertThat(aggregatedEntries.get(5.0).keySet(), containsInAnyOrder("alkla", "anana", "aadaa"));
    assertThat(aggregatedEntries.get(5.0).get("anana"), equalTo(1));
    assertThat(aggregatedEntries.get(5.0).get("alkla"), equalTo(4));
    assertThat(aggregatedEntries.get(5.0).get("aadaa"), equalTo(1));
  }

  @Test
  public void shouldUseDescendingOrder() {
    ExportParams params = ExportParams.builder()
        .isDescending(true)
        .topKeys(10)
        .topEntryMinimum(Optional.of(8.0))
        .build();

    PartWordExport export = PartWordExport.create("test", results, params, new PartWordReducer.ByLength());

    Map<Double, NavigableMap<String, Object>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(2));
    assertThat(topEntries.keySet(), contains(9.0, 8.0));
    assertThat(topEntries.get(9.0), aMapWithSize(3));
    assertThat(topEntries.get(8.0), aMapWithSize(3));

    Map<Double, NavigableMap<String, Integer>> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), contains(7.0, 6.0, 5.0));
    assertThat(aggregatedEntries.get(6.0).get("neffen"), equalTo(3));
    assertThat(aggregatedEntries.get(6.0).get("eellee"), equalTo(1));
  }
  
  @Test
  public void shouldRespectGeneralMinimum() {
    ExportParams params = ExportParams.builder()
        .generalMinimum(Optional.of(6.0))
        .topEntryMinimum(Optional.of(8.0))
        .build();
    
    PartWordExport export = PartWordExport.create("min test", results, params, new PartWordReducer.ByLength());
    
    Map<Double, NavigableMap<String, Object>> topEntries = export.getTopEntries();
    assertThat(topEntries.keySet(), contains(9.0, 8.0));
    Map<Double, NavigableMap<String, Integer>> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), contains(7.0, 6.0));
  }

  @Test
  public void shouldHandleEmptyResult() {
    PartWordExport export = PartWordExport.create("empty test", new TreeMap<>());

    assertEquals(export.identifier, "empty test");
    assertThat(export.getTopEntries(), anEmptyMap());
    assertThat(export.getAggregatedEntries(), anEmptyMap());
  }
  
  private static void checkReducedList(Object result, ExportParams params, Object... allowedItems) {
    Collection<Object> foundItems = toColl(result);
    List<Object> allowedItemsList = new ArrayList<>(Arrays.asList(allowedItems));
    String restIndex = ExportObject.INDEX_REST + (allowedItems.length - foundItems.size() + 1);
    allowedItemsList.add(restIndex);
    
    // foundItems may only have elements given in allowedItemsList, but not necessarily all
    // We can check this by changing the usual order in the assert
    assertThat(allowedItemsList, hasItems(foundItems.toArray()));
    // Make specifically sure that the rest index is also present and that the size is correct
    assertThat(foundItems, hasItem(restIndex));
    assertThat(foundItems, hasSize(params.maxPartWordListSize.get() + 1));
  }

}
