package ch.jalu.wordeval.evaluation.export;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import java.util.NavigableMap;

import static ch.jalu.wordeval.evaluation.export.ExportTestHelper.getIndexTotalCollValue;
import static ch.jalu.wordeval.evaluation.export.ExportTestHelper.getTotalValue;
import static ch.jalu.wordeval.evaluation.export.ExportTestHelper.getWordCollValue;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SuppressWarnings("javadoc")
public class PartWordReducerTest {
  
  private Multimap<String, String> results;
  private ExportParams params = ExportParams.builder()
      .topKeys(2)
      .isDescending(true)
      .build();
  
  @Before
  public void initialize() {
    results = HashMultimap.create();
    // Length 9
    results.putAll("taalplaat", asList("metaalplaat", "staalplaat"));
    results.putAll("ittesetti", singleton("hittesetting"));
    results.putAll("sigologis", singleton("psigologisme"));
    // Length 8
    results.putAll("aarddraa", singleton("aarddraad"));
    // Length 7
    results.putAll("esifise", asList("spesifisering", "gespesifiseer", "gespesifiseerd", "spesifiseer"));
    // Length 6
    results.putAll("neffen", asList("neffens", "hierneffens", "oneffenheid"));
    results.putAll("marram", singleton("marram"));
    // Length 5
    results.putAll("alkla", asList("smalklap", "taalklas", "vokaalklank", "taalklank"));
    results.putAll("anana", singleton("ananas"));
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
    
    assertEquals(export.getIdentifier(), "by_size");
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
    
    assertEquals(export.getIdentifier(), "sizeAndLength");
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
