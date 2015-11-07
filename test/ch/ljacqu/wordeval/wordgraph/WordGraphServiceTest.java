package ch.ljacqu.wordeval.wordgraph;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

public class WordGraphServiceTest {
  
  private static SimpleGraph<String, DefaultWeightedEdge> graph;
  
  @BeforeClass
  public static void setUpGraph() {
    GraphBuilder builder = new GraphBuilder(getTestWords());
    graph = builder.getGraph();
  }
  
  @Test
  public void shouldGetShortestPath() {    
    Set<String> path = WordGraphService.getShortestPath(graph, "bare", "brat");
    assertThat(path.size(), equalTo(13));
    assertThat(path, contains("bare", "bar", "bear", "bears", "beers", "bees", "beet", "meet", "meat", "heat", 
        "hat", "rat", "brat"));
  }
  
  @Test
  public void shouldGetShortestPathForSameWord() {
    Set<String> path = WordGraphService.getShortestPath(graph, "bear", "bear");
    assertThat(path, contains("bear"));
  }
  
  @Test
  public void shouldReturnEmptySetForNonExistentWord() {
    Set<String> path = WordGraphService.getShortestPath(graph, "bear", "does-not-exist");
    assertThat(path, empty());
    path = WordGraphService.getShortestPath(graph, "does-not-exist", "bear");
    assertThat(path, empty());
    path = WordGraphService.getShortestPath(graph, "does-not-exist", "does-not-exist");
    assertThat(path, empty());
  }
  
  @Test
  public void shouldDisableAndEnableVertex() {
    // Disable
    final String vertex = "bar";
    boolean result = WordGraphService.disableVertexEdges(graph, vertex);
    assertTrue(result);
    for (DefaultWeightedEdge e : graph.edgesOf(vertex)) {
      assertThat(graph.getEdgeWeight(e), equalTo(Double.POSITIVE_INFINITY));
    }
    
    // Enable
    result = WordGraphService.enableVertexEdges(graph, vertex);
    assertTrue(result);
    for (DefaultWeightedEdge e : graph.edgesOf(vertex)) {
      assertThat(graph.getEdgeWeight(e), equalTo(1.0));
    }
  }
  
  @Test
  public void shouldReturnFalseForNonExistentVertex() {
    assertThat(WordGraphService.disableVertexEdges(graph, "non-existent"), equalTo(Boolean.FALSE));
    assertThat(WordGraphService.enableVertexEdges(graph, "non-existent"), equalTo(Boolean.FALSE));
  }
  
  @Test
  public void shouldNotReturnShortestPathForDisabledEdge() {
    SimpleGraph<String, DefaultWeightedEdge> simpleGraph = 
        new UndirectedGraphBuilder<String, DefaultWeightedEdge, SimpleGraph<String, DefaultWeightedEdge>>(
            new SimpleGraph<>(DefaultWeightedEdge.class))
      .addVertices("v1", "v2", "v3", "v4")
      .addEdgeChain("v1", "v2", "v3", "v4")
      .build();
    simpleGraph.setEdgeWeight(simpleGraph.getEdge("v2", "v3"), Double.POSITIVE_INFINITY);
    
    assertThat(WordGraphService.getShortestPath(simpleGraph, "v1", "v4"), empty());
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
