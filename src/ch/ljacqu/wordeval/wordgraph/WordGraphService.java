package ch.ljacqu.wordeval.wordgraph;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Service for word graphs.
 */
public final class WordGraphService {
  
  private WordGraphService() {
  }
  
  /**
   * Exports a word graph to a file as JSON.
   * @param filename the filename to write the graph to
   * @param graph the graph to store
   */
  public static void exportConnections(String filename, SimpleGraph<String, DefaultWeightedEdge> graph) {
    Map<String, List<String>> connections = convertEdgesToConnectionsMap(graph);
    Gson gson = new Gson();
    writeToFile(filename, gson.toJson(connections));
  }
  
  /**
   * Imports a graph from a JSON file.
   * @param filename the filename to read the data from
   * @return the stored graph
   */
  public static SimpleGraph<String, DefaultWeightedEdge> importConnections(String filename) {
    Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
    Gson gson = new Gson();
    Map<String, List<String>> connections = gson.fromJson(readFromFile(filename), type);
    return convertConnectionsMapToGraph(connections);
  }
  
  /**
   * Returns the set of neighbors of the given graph.
   * @param <V> the vertex type
   * @param <E> the edge type
   * @param graph the graph
   * @param vertex the vertex to process
   * @return the neighbors of the given vertex in the given graph
   */
  public static <V, E> Set<V> getNeighbors(UndirectedGraph<V, E> graph, V vertex) {
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
  public static <V, E> V getNeighbor(UndirectedGraph<V, E> graph, E edge, V vertex) {
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
  public static <V, E> LinkedHashSet<V> getShortestPath(UndirectedGraph<V, E> graph, V source, V target) {
    if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
      return new LinkedHashSet<>();
    }
    
    // TODO: Is there a better algorithm for an undirected graph?
    LinkedHashSet<V> vertices = new LinkedHashSet<>();
    vertices.add(source);
    V lastVertex = source;
    final List<E> edges = DijkstraShortestPath.findPathBetween(graph, source, target);
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
  public static <V, E> boolean disableVertexEdges(SimpleGraph<V, E> graph, V vertex) {
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
  public static <V, E> boolean enableVertexEdges(SimpleGraph<V, E> graph, V vertex) {
    return setWeightForAllEdges(graph, vertex, 1);
  }
  
  private static <V, E> boolean setWeightForAllEdges(SimpleGraph<V, E> graph, V vertex, double weight) {
    if (!graph.containsVertex(vertex)) {
      return false;
    }
    for (E edge : graph.edgesOf(vertex)) {
      graph.setEdgeWeight(edge, weight);
    }
    return true;
  }

  /**
   * Wrapper for writing content to a file.
   * @param filename the file to write to
   * @param content the content to store in the file
   */
  private static void writeToFile(String filename, String content) {
    try {
      Files.write(Paths.get(filename), content.getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Could not write to file '" + filename + "'", e);
    }
  }
  
  private static <V, E> Map<V, List<V>> convertEdgesToConnectionsMap(UndirectedGraph<V, E> graph) {
    Map<V, List<V>> connections = new HashMap<>();
    for (E edge : graph.edgeSet()) {
      V source = graph.getEdgeSource(edge);
      V target = graph.getEdgeTarget(edge);
      if (connections.get(source) == null) {
        connections.put(source, new ArrayList<>());
      }
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
  
  private static String readFromFile(String filename) {
    try {
      return String.join("", Files.readAllLines(Paths.get(filename)));
    } catch (IOException e) {
      throw new IllegalStateException("Could not read from file", e);
    }
  }

}
