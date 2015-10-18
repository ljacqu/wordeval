package ch.ljacqu.wordeval.wordgraph;

import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;


public class ConnectionsBuilderTest {
  
  @Test
  public void shouldBuildConnectionsAndRemoveIsolatedOnes() {
    List<String> words = getTestWords();
    words.add("emptyempty");
    ConnectionsBuilder builder = new ConnectionsBuilder(words);
    
    // Build & check connections
    Map<String, List<String>> connections = builder.getConnections();
    assertThat(connections.keySet(), containsInAnyOrder(words.toArray()));
    assertThat(connections.get("acre"), contains("care"));
    assertThat(connections.get("bar"), containsInAnyOrder("bare", "bear", "boar", "car"));
    assertThat(connections.get("car"), containsInAnyOrder("care"));
    assertThat(connections.get("brat"), contains("rat"));
    assertThat(connections.get("meat"), contains("meet"));
    
    // Remove isolated words
    builder.removeIsolatedWords();
    connections = builder.getConnections();
    assertThat(connections, not(hasKey("emptyempty")));
    assertThat(connections, not(hasKey("meet")));
    assertThat(connections.keySet(), hasItems("acre", "bar", "car", "brat", "meat"));
  }
  
  @Test
  public void shouldWorkWithEmptyList() {
    ConnectionsBuilder builder = new ConnectionsBuilder(new ArrayList<>());
    Map<String, List<String>> connections = builder.getConnections();
    assertThat(connections, anEmptyMap());
    
    builder.removeIsolatedWords();
    connections = builder.getConnections();
    assertThat(connections, anEmptyMap());
  }
  
  @Test
  @Ignore
  public void shouldLoadWordsFromDictionary() {
    // TODO: Write test with mock implementation of WordCollector
  }
  
  private static List<String> getTestWords() {
    String givenWords = "acre, care, car, bar, bare, bear, bears, boars, boar, "
        + "beers, bees, bee, be, bet, beet, meet, meat, heat, hat, rat, brat";
    
    return Arrays.stream(givenWords.split(","))
      .map(String::trim)
      .sorted()
      .collect(Collectors.toList());
  }

}
