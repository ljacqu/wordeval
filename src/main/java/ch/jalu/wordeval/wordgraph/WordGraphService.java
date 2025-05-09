package ch.jalu.wordeval.wordgraph;

import ch.jalu.wordeval.DataUtils;
import com.google.gson.reflect.TypeToken;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for word graphs.
 */
@Service
public class WordGraphService {

  @Autowired
  private DataUtils dataUtils;
  
  private WordGraphService() {
  }
  
  /**
   * Exports a word graph to a file as JSON.
   * @param filename the filename to write the graph to
   * @param graph the graph to store
   */
  public void exportConnections(String filename, SimpleGraph<String, DefaultWeightedEdge> graph) {
    Map<String, List<String>> connections = convertEdgesToConnectionsMap(graph);
    dataUtils.writeToFile(filename, dataUtils.toJson(connections));
  }
  
  /**
   * Imports a graph from a JSON file.
   * @param filename the filename to read the data from
   * @return the stored graph
   */
  public SimpleGraph<String, DefaultWeightedEdge> importConnections(String filename) {
    Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
    Map<String, List<String>> connections = dataUtils.fromJson(dataUtils.readFile(filename), type);
    return convertConnectionsMapToGraph(connections);
  }
  
  /**
   * Returns the set of neighbors of the given vertex.
   * @param <V> the vertex type
   * @param <E> the edge type
   * @param graph the graph
   * @param vertex the vertex to analyze
   * @return the neighbors of the vertex in the given graph
   */
  public static <V, E> Set<V> getNeighbors(Graph<V, E> graph, V vertex) {
    if (!graph.containsVertex(vertex)) {
      return new HashSet<>();
    }
    return graph.edgesOf(vertex).stream()
      .map(edge -> getNeighbor(graph, edge, vertex))
      .collect(Collectors.toSet());
  }
  
  /**
   * Gets the neighbor of the given edge.
   * @param <V> the vertex type
   * @param <E> the edge type
   * @param graph the graph
   * @param edge the edge to process
   * @param vertex the vertex whose neighbor should be returned
   * @return the neighbor of the given vertex
   */
  private static <V, E> V getNeighbor(Graph<V, E> graph, E edge, V vertex) {
    V source = graph.getEdgeSource(edge);
    return source.equals(vertex) ? graph.getEdgeTarget(edge) : source;
  }
  
  /**
   * Returns the shortest path between two vertices as a list of vertices that have
   * to be visited.
   * @param <V> the vertex type
   * @param <E> the edge type
   * @param graph the graph
   * @param source the source vertex (start)
   * @param target the target vertex (end)
   * @return the shortest path from source to target
   */
  public <V, E> LinkedHashSet<V> getShortestPath(Graph<V, E> graph, V source, V target) {
    if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
      return new LinkedHashSet<>();
    }

    // TODO #67: Is there a better algorithm for an undirected graph?
    GraphPath<V, E> path = DijkstraShortestPath.findPathBetween(graph, source, target);
    final List<E> edges = path == null ? null : path.getEdgeList();
    if (edges == null || pathHasDisabledEdge(graph, edges)) {
      return new LinkedHashSet<>();
    }

    LinkedHashSet<V> vertices = new LinkedHashSet<>();
    vertices.add(source);
    V lastVertex = source;
    for (E edge : edges) {
      V newVertex = getNeighbor(graph, edge, lastVertex);
      vertices.add(newVertex);
      lastVertex = newVertex;
    }
    return vertices;
  }
  
  /**
   * Sets all edges of the given vertex to a weight of infinite.
   * They will be avoided in a shortest path query, unless it is
   * impossible not to traverse any edge with such a weight.
   * @param <V> the vertex class
   * @param <E> the edge class
   * @param graph the graph
   * @param vertex the vertex to disable
   * @return {@code true} if the vertex exists, {@code false} otherwise
   */
  public <V, E> boolean disableVertexEdges(SimpleGraph<V, E> graph, V vertex) {
    return setWeightForAllEdges(graph, vertex, Double.POSITIVE_INFINITY);
  }
  
  /**
   * Sets the weight of the given vertex' edges to the default of 1.
   * @param <V> the vertex class
   * @param <E> the edge class
   * @param graph the graph
   * @param vertex the vertex to enable
   * @return {@code true} if the vertex exists, {@code false} otherwise
   */
  public <V, E> boolean enableVertexEdges(SimpleGraph<V, E> graph, V vertex) {
    return setWeightForAllEdges(graph, vertex, 1);
  }
  
  private <V, E> boolean setWeightForAllEdges(SimpleGraph<V, E> graph, V vertex, double weight) {
    if (!graph.containsVertex(vertex)) {
      return false;
    }
    for (E edge : graph.edgesOf(vertex)) {
      graph.setEdgeWeight(edge, weight);
    }
    return true;
  }
  
  /**
   * Checks if the list of edges traverses an edge with infinite weight.
   * @param <V> the vertex class
   * @param <E> the edge class
   * @param graph the graph
   * @param edges the list of vertices (the path) to verify
   * @return {@code true} if an edge has infinite weight, {@code false} otherwise
   */
  public <V, E> boolean pathHasDisabledEdge(Graph<V, E> graph, Collection<E> edges) {
    return edges.stream()
      .anyMatch(edge -> Double.isInfinite(graph.getEdgeWeight(edge)));
  }
  
  private static <V, E> Map<V, List<V>> convertEdgesToConnectionsMap(Graph<V, E> graph) {
    Map<V, List<V>> connections = new HashMap<>();
    for (E edge : graph.edgeSet()) {
      V source = graph.getEdgeSource(edge);
      V target = graph.getEdgeTarget(edge);
      connections.putIfAbsent(source, new ArrayList<>());
      connections.get(source).add(target);
    }
    return connections;
  }
  
  private static <V> SimpleGraph<V, DefaultWeightedEdge> convertConnectionsMapToGraph(Map<V, List<V>> connections) {
    SimpleGraph<V, DefaultWeightedEdge> graph = new SimpleGraph<>(DefaultWeightedEdge.class);
    for (Map.Entry<V, List<V>> entry : connections.entrySet()) {
      graph.addVertex(entry.getKey());
      entry.getValue().stream()
        .peek(graph::addVertex)
        .forEach(rightWord -> graph.addEdge(entry.getKey(), rightWord));
    }
    return graph;
  }

}
