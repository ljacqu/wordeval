package ch.ljacqu.wordeval.evaluation.export;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
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
      results.put(i, Arrays.asList(words[7 - i]));
    }
  }

  @Test
  public void shouldBuildExportObjectAccordingToParams() {
    ExportParams params = new ExportParamsBuilder().setMaxTopEntrySize(3)
        .setTopKeys(4).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    assertThat(export.identifier, equalTo("test"));

    Map<Integer, List<String>> topEntries = export.getTopEntries();
    assertThat(topEntries.size(), equalTo(params.topKeys));
    for (int i = 7; i > 3; --i) {
      assertThat(topEntries.get(i), notNullValue());
      assertThat(topEntries.get(i).size(),
          not(greaterThan(params.maxTopEntrySize + 1)));
    }
    assertThat(topEntries.get(6), hasItem(ExportObject.INDEX_REST + "1"));
    assertThat(topEntries.get(5), hasItem(ExportObject.INDEX_REST + "3"));
    assertThat(topEntries.get(4), hasItem(ExportObject.INDEX_REST + "2"));

    assertThat(export.getAggregatedEntries(), aMapWithSize(2));
    assertThat(export.getAggregatedEntries().get(3), equalTo(4));
    assertThat(export.getAggregatedEntries().get(2), equalTo(8));
  }

  @Test
  public void shouldRespectMinimumKeyParam() {
    ExportParams params = new ExportParamsBuilder().setMinimum(6)
        .setMaxTopEntrySize(null).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    assertThat(export.getTopEntries(), aMapWithSize(2));
    // TODO: Do not compare with results directly
    assertThat(export.getTopEntries().get(7), equalTo(results.get(7)));
    assertThat(export.getTopEntries().get(6), equalTo(results.get(6)));
    assertThat(export.getAggregatedEntries(), aMapWithSize(4));
  }

  @Test
  public void shouldHandleEmptyTopEntries() {
    ExportParams params = new ExportParamsBuilder().setTopKeys(0).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    assertThat(export.getTopEntries(), anEmptyMap());
    assertThat(export.getAggregatedEntries(), aMapWithSize(6));
  }

  @Test
  public void shouldBeInDescendingOrder() {
    ExportParams params = new ExportParamsBuilder().setDescending(true)
        .setTopKeys(2).setMinimum(null).build();

    WordStatExport export = WordStatExport.create("test", results, params);

    SortedMap<Integer, List<String>> topEntries = export.getTopEntries();
    assertThat(topEntries, aMapWithSize(2));
    assertThat(topEntries.firstKey(), equalTo(7));
    assertThat(topEntries.lastKey(), equalTo(6));
    assertThat(topEntries.get(6), hasItem("leeueaandeel"));

    SortedMap<Integer, Integer> aggregatedEntries = export
        .getAggregatedEntries();
    assertThat(aggregatedEntries, aMapWithSize(4));
    assertThat(aggregatedEntries.firstKey(), equalTo(5));
    assertThat(aggregatedEntries.lastKey(), equalTo(2));
  }

  @Test
  public void shouldExportWithDefaultParams() {
    WordStatExport export = WordStatExport.create("default test", results);

    assertThat(export.identifier, equalTo("default test"));
    assertThat(export.getAggregatedEntries(), notNullValue());
    assertThat(export.getTopEntries(), notNullValue());
  }

  @Test
  public void shouldHandleEmptyResult() {
    ExportParams params = new ExportParamsBuilder().build();

    WordStatExport export = WordStatExport.create("empty", new TreeMap<>(),
        params);

    assertThat(export.identifier, equalTo("empty"));
    assertThat(export.getAggregatedEntries(), anEmptyMap());
    assertThat(export.getTopEntries(), anEmptyMap());
  }

}
