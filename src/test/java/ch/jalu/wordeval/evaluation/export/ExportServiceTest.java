package ch.jalu.wordeval.evaluation.export;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import ch.jalu.wordeval.evaluation.export.TreeElement.IndexTotalColl;
import ch.jalu.wordeval.evaluation.export.TreeElement.Rest;
import ch.jalu.wordeval.evaluation.export.TreeElement.WordColl;

@SuppressWarnings("javadoc")
public class ExportServiceTest {
  
  @Test
  public void shouldSerializeWithoutActualTreeClass() {
    IndexTotalColl indexTotal = new IndexTotalColl(new TreeMap<>());
    indexTotal.getTypedValue().put("www", 1);
    indexTotal.getTypedValue().put("eee", 2);
    WordColl wordColl = new WordColl(Arrays.asList("tree", "element", "test"));
    
    Map<Double, TreeElement> map = new HashMap<>();
    map.put(23.0, new Rest(10));
    map.put(12.0, indexTotal);
    map.put(6.5, wordColl);

    String result = ExportService.getGson().toJson(map);

    String expected = "{\"23.0\":10,\"12.0\":{\"eee\":2,\"www\":1},"
        + "\"6.5\":[\"tree\",\"element\",\"test\"]}";
    // Replace all whitespace so the test is independent of JSON pretty print or not
    String briefResult = result.replaceAll("\\s", "");
    assertThat(briefResult, equalTo(expected));
  }

}
