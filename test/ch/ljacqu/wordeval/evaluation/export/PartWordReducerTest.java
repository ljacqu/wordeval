package ch.ljacqu.wordeval.evaluation.export;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static ch.ljacqu.wordeval.evaluation.export.ExportTestHelper.getIndexTotalCollValue;
import static ch.ljacqu.wordeval.evaluation.export.ExportTestHelper.getTotalValue;
import static ch.ljacqu.wordeval.evaluation.export.ExportTestHelper.getWordCollValue;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class PartWordReducerTest {
  
  private NavigableMap<String, Set<String>> results;
  private ExportParams params = ExportParams.builder()
      .topKeys(2)
      .isDescending(true)
      .build();
  
  @Before
  public void initialize() {
    results = new TreeMap<>();
    // Length 9
    results.put("taalplaat", asSet("metaalplaat", "staalplaat"));
    results.put("ittesetti", asSet("hittesetting"));
    results.put("sigologis", asSet("psigologisme"));
    // Length 8
    results.put("aarddraa", asSet("aarddraad"));
    // Length 7
    results.put("esifise", asSet("spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer"));
    // Length 6
    results.put("neffen", asSet("neffens", "hierneffens", "oneffenheid"));
    results.put("marram", asSet("marram"));
    // Length 5
    results.put("alkla", asSet("smalklap", "taalklas", "vokaalklank", "taalklank"));
    results.put("anana", asSet("ananas"));
  }
  
  @Test
  public void shouldOrderByLength() {
    PartWordReducer reducer = new PartWordReducer.ByLength();
    
    PartWordExport export = PartWordExport.create("test", results, params, reducer);
    
    assertThat(export.getTopEntries().keySet(), contains(9.0, 8.0));
    assertThat(export.getAggregatedEntries().keySet(), contains(7.0, 6.0, 5.0));
  }
  
  @Test
  public void shouldOrderBySize() {
    PartWordReducer reducer = new PartWordReducer.BySize();
    
    PartWordExport export = PartWordExport.create("by_size", results, params, reducer);
    
    assertEquals(export.identifier, "by_size");
    assertThat(export.getTopEntries().keySet(), contains(4.0, 3.0));
    assertThat(export.getTopEntries().get(4.0).keySet(), containsInAnyOrder("alkla", "esifise"));
    assertThat(export.getTopEntries().get(3.0).keySet(), contains("neffen"));
    assertThat(export.getAggregatedEntries().keySet(), contains(2.0, 1.0));
    assertThat(getIndexTotalCollValue(export.getAggregatedEntries().get(2.0)).keySet(), 
        contains("taalplaat"));
    assertThat(getIndexTotalCollValue(export.getAggregatedEntries().get(1.0)), aMapWithSize(5));
  }
  
  @Test
  public void shouldOrderBySizeAndLength() {
    PartWordReducer reducer = new PartWordReducer.BySizeAndLength(1.0, 0.5);
    
    PartWordExport export = PartWordExport.create("sizeAndLength", results, params, reducer);
    
    assertEquals(export.identifier, "sizeAndLength");
    assertThat(export.getTopEntries().keySet(), contains(7.5, 6.5));
    assertThat(export.getTopEntries().get(7.5).keySet(), contains("esifise"));
    assertThat(getWordCollValue(export.getTopEntries().get(7.5).get("esifise")), 
        containsInAnyOrder("spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer"));
    
    assertThat(export.getTopEntries().get(6.5).keySet(), containsInAnyOrder("taalplaat", "alkla"));
    assertThat(getWordCollValue(export.getTopEntries().get(6.5).get("taalplaat")),
        containsInAnyOrder("metaalplaat", "staalplaat"));
    assertThat(getWordCollValue(export.getTopEntries().get(6.5).get("alkla")),
        containsInAnyOrder("smalklap", "taalklas", "vokaalklank", "taalklank"));
    
    NavigableMap<Double, TreeElement> aggregatedEntries = export.getAggregatedEntries();
    assertThat(aggregatedEntries.keySet(), containsInAnyOrder(6.0, 5.5, 5.0, 4.0, 3.5));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).keySet(), contains("neffen"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(6.0)).get("neffen"), equalTo(3));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.5)).keySet(), 
        containsInAnyOrder("ittesetti", "sigologis"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).keySet(), contains("aarddraa"));
    assertThat(getIndexTotalCollValue(aggregatedEntries.get(5.0)).get("aarddraa"), equalTo(1));
    assertThat(getTotalValue(aggregatedEntries.get(4.0)), equalTo(1));
    assertThat(getTotalValue(aggregatedEntries.get(3.5)), equalTo(1));
  }

}
