package ch.ljacqu.wordeval.evaluation.export;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static ch.ljacqu.wordeval.evaluation.export.ExportTestHelper.getIndexTotalCollValue;
import static ch.ljacqu.wordeval.evaluation.export.ExportTestHelper.getWordCollValue;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
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

@SuppressWarnings("javadoc")
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

    Map<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(3));
    assertThat(topEntries.get(9.0), aMapWithSize(3));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(9.0).get("taalplaat")), 
        containsInAnyOrder("metaalplaat", "staalplaat"));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(9.0).get("ittesetti")), contains("hittesetting"));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(9.0).get("sigologis")), contains("psigologisme"));
    assertThat(topEntries.get(8.0).keySet(), containsInAnyOrder("aarddraa", "erettere", "kaarraak"));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(8.0).get("kaarraak")), contains("deurmekaarraak"));
    assertThat(topEntries.get(7.0), aMapWithSize(1));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(7.0).get("esifise")),
        containsInAnyOrder("spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer"));

    Map<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries, aMapWithSize(2));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).keySet(),
        containsInAnyOrder("millim", "neffen", "marram", "leggel", "gerreg", "eellee", "arkkra"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).get("millim"), equalTo(1));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).get("neffen"), equalTo(3));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).get("eellee"), equalTo(1));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).keySet(), 
        containsInAnyOrder("alkla", "anana", "aadaa"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("alkla"), equalTo(4));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("anana"), equalTo(1));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("aadaa"), equalTo(1));
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

    Map<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(4));
    assertThat(topEntries.get(9.0), aMapWithSize(3));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(9.0).get("taalplaat")), 
        containsInAnyOrder("metaalplaat", "staalplaat"));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(9.0).get("ittesetti")), contains("hittesetting"));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(9.0).get("sigologis")), contains("psigologisme"));
    assertThat(topEntries.get(8.0).keySet(), containsInAnyOrder("aarddraa", "erettere", "kaarraak"));
    assertThat(ExportTestHelper.getWordCollValue(topEntries.get(8.0).get("erettere")), contains("veretterende"));
    
    assertThat(topEntries.get(7.0), aMapWithSize(1));
    String[] allowedItems = { "spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer" };
    checkReducedList(topEntries.get(7.0).get("esifise"), params.maxPartWordListSize.get(), allowedItems);

    assertThat(topEntries.get(6.0), aMapWithSize(params.maxTopEntrySize.get() + 1));
    Set<String> foundKeysSet = topEntries.get(6.0).keySet();
    String[] foundKeys = foundKeysSet.toArray(new String[foundKeysSet.size()]);
    assertThat(asSet("millim", "neffen", "marram", "leggel", "gerreg", "eellee", "arkkra", ExportObject.INDEX_REST),
        hasItems(foundKeys));
    assertThat(topEntries.get(6.0).get(ExportObject.INDEX_REST), instanceOf(TreeElement.Rest.class));
    assertThat(topEntries.get(6.0).get(ExportObject.INDEX_REST).getValue(), equalTo(3));

    Map<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries, aMapWithSize(1));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).keySet(), 
        containsInAnyOrder("alkla", "anana", "aadaa"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("anana"), equalTo(1));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("alkla"), equalTo(4));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("aadaa"), equalTo(1));
  }

  @Test
  public void shouldUseDescendingOrder() {
    ExportParams params = ExportParams.builder()
        .isDescending(true)
        .topKeys(10)
        .topEntryMinimum(Optional.of(8.0))
        .build();

    PartWordExport export = PartWordExport.create("test", results, params, new PartWordReducer.ByLength());

    Map<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(2));
    assertThat(topEntries.keySet(), contains(9.0, 8.0));
    assertThat(topEntries.get(9.0), aMapWithSize(3));
    assertThat(topEntries.get(8.0), aMapWithSize(3));

    Map<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), contains(7.0, 6.0, 5.0));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).get("neffen"), equalTo(3));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).get("eellee"), equalTo(1));
  }
  
  @Test
  public void shouldRespectGeneralMinimum() {
    ExportParams params = ExportParams.builder()
        .generalMinimum(Optional.of(6.0))
        .topEntryMinimum(Optional.of(8.0))
        .build();
    
    PartWordExport export = PartWordExport.create("min test", results, params, new PartWordReducer.ByLength());
    
    Map<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries.keySet(), contains(9.0, 8.0));
    Map<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), contains(7.0, 6.0));
  }
  
  @Test
  public void shouldRespectDescendingEntryParam() {
    ExportParams params = ExportParams.builder()
        .hasDescendingEntries(true)
        .isDescending(false)
        .topKeys(3)
        .build();
    
    PartWordExport export = PartWordExport.create("desc test", results, params, new PartWordReducer.ByLength());
    
    Map<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries.keySet(), contains(7.0, 8.0, 9.0));
    assertThat(topEntries.get(9.0).keySet(), contains("taalplaat", "sigologis", "ittesetti"));
    assertThat(topEntries.get(8.0).keySet(), contains("kaarraak", "erettere", "aarddraa"));
    assertThat(topEntries.get(7.0).keySet(), contains("esifise"));
    
    Map<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), contains(5.0, 6.0));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).keySet(), contains("anana", "alkla", "aadaa"));
  }
  
  @Test
  public void shouldHandleMaxTopEntryAndMaxPartWordListSizeParams() {
    ExportParams params = ExportParams.builder()
        .maxTopEntrySize(Optional.of(2))
        .maxPartWordListSize(Optional.of(1))
        .topKeys(3)
        .build();
    
    PartWordExport export = PartWordExport.create("size test", results, params, new PartWordReducer.ByLength());
    
    NavigableMap<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries.keySet(), contains(9.0, 8.0, 7.0));
    checkReducedKeySet(topEntries.get(9.0).keySet(), params.maxTopEntrySize.get(), 
        "taalplaat", "ittesetti", "sigologis");
    checkReducedKeySet(topEntries.get(8.0).keySet(), params.maxTopEntrySize.get(), 
        "aarddraa", "erettere", "kaarraak");
    assertThat(topEntries.get(7.0).keySet(), contains("esifise"));
    checkReducedList(topEntries.get(7.0).get("esifise"), params.maxPartWordListSize.get(), 
        "spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer");
    assertThat(export.getAggregatedEntries().keySet(), contains(6.0, 5.0));
  }
  
  @Test
  public void shouldHandleNumberOfDetailedAggregationSetting() {
    ExportParams params = ExportParams.builder()
        .topKeys(1)
        .numberOfDetailedAggregation(Optional.of(2))
        .build();
    
    PartWordExport export = PartWordExport.create("detailed agg. test", results, params,
        new PartWordReducer.ByLength());
    
    NavigableMap<Double, NavigableMap<String, TreeElement>> topEntries = export.getTopEntries();
    assertThat(topEntries.keySet(), contains(9.0));
    NavigableMap<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), contains(8.0, 7.0, 6.0, 5.0));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(8.0)).keySet(), 
        containsInAnyOrder("aarddraa", "erettere", "kaarraak"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(7.0)).keySet(), contains("esifise"));
    assertThat(aggregatedEntries.get(6.0), instanceOf(TreeElement.Total.class));
    assertThat(aggregatedEntries.get(6.0).getValue(), equalTo(9));
    assertThat(aggregatedEntries.get(5.0), instanceOf(TreeElement.Total.class));
    assertThat(aggregatedEntries.get(5.0).getValue(), equalTo(6));
  }

  @Test
  public void shouldHandleEmptyResult() {
    PartWordExport export = PartWordExport.create("empty test", new TreeMap<>());

    assertEquals(export.identifier, "empty test");
    assertThat(export.getTopEntries(), anEmptyMap());
    assertThat(export.getAggregatedEntries(), anEmptyMap());
  }
  
  private static void checkReducedList(TreeElement result, int maxAllowedSize, String... allowedItems) {
    String restIndex = ExportObject.INDEX_REST + (allowedItems.length - maxAllowedSize);
    checkReducedCollection(getWordCollValue(result), maxAllowedSize, allowedItems, restIndex);
  }
  
  private static void checkReducedKeySet(Set<String> keys, int maxAllowedSize, String... allowedItems) {
    checkReducedCollection(keys, maxAllowedSize, allowedItems, ExportObject.INDEX_REST);
  }
  
  private static void checkReducedCollection(Collection<String> foundItems, int maxAllowedSize, 
                                             String[] allowedItems, String restIndex) {
    List<Object> allowedItemsList = new ArrayList<>(Arrays.asList(allowedItems));
    allowedItemsList.add(restIndex);
    
    // foundItems may only have elements given in allowedItemsList, but not necessarily all
    // We can check this by changing the usual order in the assert
    assertThat(allowedItemsList, hasItems(foundItems.toArray()));
    // Make specifically sure that the rest index is also present and that the size is correct
    assertThat(foundItems, hasItem(restIndex));
    assertThat(foundItems, hasSize(maxAllowedSize + 1));
  }

}
