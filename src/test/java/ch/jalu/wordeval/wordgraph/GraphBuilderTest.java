package ch.jalu.wordeval.wordgraph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link GraphBuilder}.
 */
public class GraphBuilderTest {
  
  @Test
  public void shouldBuildConnections() {
    final String emptyVertex = "emptyempty";
    List<String> words = getTestWords();
    words.add(emptyVertex);
    GraphBuilder builder = new GraphBuilder(words);
    
    // Build & check connections
    SimpleGraph<String, DefaultWeightedEdge> graph = builder.getGraph();
    assertThat(graph.vertexSet(), containsInAnyOrder(words.toArray()));
    assertTrue(graph.containsEdge("acre", "care"));
    assertThat(WordGraphService.getNeighbors(graph, "bar"), containsInAnyOrder("bare", "bear", "boar", "car"));
    assertThat(WordGraphService.getNeighbors(graph, "car"), containsInAnyOrder("bar", "care"));
    assertThat(WordGraphService.getNeighbors(graph, "brat"), contains("rat"));
    assertThat(WordGraphService.getNeighbors(graph, "meat"), contains("heat", "meet"));
    assertThat(graph.edgesOf(emptyVertex), empty());
  }
  
  @Test
  public void shouldWorkWithEmptyList() {
    GraphBuilder builder = new GraphBuilder(new ArrayList<>());
    SimpleGraph<String, DefaultWeightedEdge> graph = builder.getGraph();
    assertThat(graph.edgeSet(), empty());
  }

  /**
   * Returns a collection of words which form a graph, i.e. there is a path for
   * any word to any other word returned from this method.
   * @return list of test words
   */
  private static List<String> getTestWords() {
    String givenWords = "acre, care, car, bar, bare, bear, bears, boars, boar, "
        + "beers, bees, bee, be, bet, beet, meet, meat, heat, hat, rat, brat";
    
    return Arrays.stream(givenWords.split(","))
      .map(String::trim)
      .sorted()
      .collect(Collectors.toList());
  }

}
