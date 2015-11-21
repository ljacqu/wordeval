package ch.ljacqu.wordeval.wordgraph;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ch.ljacqu.wordeval.DataUtils;
import ch.ljacqu.wordeval.TestUtil;

public class WordGraphServiceTest {
  
  private static SimpleGraph<String, DefaultWeightedEdge> graph;
  private static DataUtils mockDataUtils;
  
  @BeforeClass
  public static void setUpGraph() {
    GraphBuilder builder = new GraphBuilder(getTestWords());
    graph = builder.getGraph();
    
    // Set mock for DataUtils
    mockDataUtils = Mockito.mock(DataUtils.class);
    TestUtil.R.setField(WordGraphService.class, null, "dataUtils", mockDataUtils);
  }
  
  // ---
  // Shortest path
  // ---
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
  
  // ---
  // Disable/enable vertices
  // ---
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
    WordGraphService.disableVertexEdges(simpleGraph, "v3");
    
    assertThat(WordGraphService.getShortestPath(simpleGraph, "v1", "v4"), empty());
  }
  
  // ---
  // Get neighbors
  // ---
  @Test
  public void shouldGetNeighborsOfVertex() {
    Set<String> neighbors = WordGraphService.getNeighbors(graph, "bear");
    assertThat(neighbors, containsInAnyOrder("bar", "bears", "boar"));
    
    neighbors = WordGraphService.getNeighbors(graph, "bar");
    assertThat(neighbors, containsInAnyOrder("bear", "car", "bare", "boar"));
  }
  
  @Test
  public void shouldReturnEmptyListForNonExistentVertex() {
    Set<String> neighbors = WordGraphService.getNeighbors(graph, "unknown-vertex");
    assertThat(neighbors, empty());
  }
  
  // ---
  // Export/import
  // ---
  @Test
  public void shouldExportGraph() {
    final String filename = "test-export.json";
    final String sampleJson = "{isMock: true}";
    ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
    when(mockDataUtils.toJson(captor.capture())).thenReturn(sampleJson);
    doNothing().when(mockDataUtils).writeToFile(filename, sampleJson);
    
    WordGraphService.exportConnections(filename, graph);
    
    verify(mockDataUtils).toJson(any(Map.class));
    verify(mockDataUtils).writeToFile(filename, sampleJson);
    Map<String, List<String>> connectionsMap = captor.getValue();
    assertTrue(hasConnection(connectionsMap, "bear", "boar"));
    assertTrue(hasConnection(connectionsMap, "bear", "bar"));
    assertTrue(hasConnection(connectionsMap, "hat", "heat"));
    assertTrue(hasConnection(connectionsMap, "meat", "meet"));
    assertTrue(!hasConnection(connectionsMap, "bear", "meet"));
  }
  
  @Test
  public void shouldImportGraph() {
    final String sampleJson = "{context: \"test\"}";
    final String filename = "import-test.json";
    Map<String, List<String>> connections = new HashMap<>();
    connections.put("v1", Arrays.asList("v2"));
    connections.put("v3", Arrays.asList("v2", "v4"));
    connections.put("v4", Arrays.asList("v1"));
    when(mockDataUtils.readFile(filename)).thenReturn(sampleJson);
    when(mockDataUtils.fromJson(eq(sampleJson), any(Type.class))).thenReturn(connections);
    
    SimpleGraph<String, DefaultWeightedEdge> graphResult = WordGraphService.importConnections(filename);
    
    verify(mockDataUtils).fromJson(eq(sampleJson), any(Type.class));
    verify(mockDataUtils).readFile(filename);
    assertThat(graphResult.vertexSet(), containsInAnyOrder("v1", "v2", "v3", "v4"));
    assertThat(graphResult.getEdge("v1", "v2"), not(nullValue()));
    assertThat(graphResult.getEdge("v2", "v3"), not(nullValue()));
    assertThat(graphResult.getEdge("v1", "v3"), nullValue());
    assertThat(graphResult.edgeSet(), hasSize(4));
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
  
  /**
   * Checks that either map.get(a).contains(b) || map.get(b).contains(a).
   * @param map the map to check
   * @param a the first word
   * @param b the second word
   * @return true if the words are connected
   */
  private static boolean hasConnection(Map<String, List<String>> map, String a, String b) {
    return (map.containsKey(a) && map.get(a).contains(b))
        || (map.containsKey(b) && map.get(b).contains(a));
  }

}
