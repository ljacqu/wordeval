package ch.ljacqu.wordeval.wordgraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Service for word graphs.
 */
public final class WordGraphService {
  
  private WordGraphService() {
  }
  
  /**
   * Exports the connections of a word graph to a file as JSON.
   * @param filename The filename to write the structure to
   * @param connections The connections to store
   */
  public static void exportConnections(String filename, Map<String, List<String>> connections) {
    Gson gson = new Gson();
    writeToFile(filename, gson.toJson(connections));
  }
  
  public static Map<String, List<String>> importConnections(String filename) {
    Gson gson = new Gson();
    return gson.fromJson(readFromFile(filename), Map.class);
  }
  
  /**
   * Converts the given connections into a DOT file (GraphViz).
   * @param connections The collection of word connections
   * @return Graph in DOT format
   */
  public static String convertToDotGraph(Map<String, List<String>> connections) {
    StringBuilder builder = new StringBuilder("digraph G {");
    connections.entrySet()
      .forEach(entry -> {
        final String left = entry.getKey();
        entry.getValue()
          .forEach(right -> builder.append("\n\t")
              .append(left).append(" -- ").append(right));
      });
    builder.append("\n}");
    return builder.toString();
  }
  
  /**
   * Creates symmetric connections. The ConnectionsBuilder only stores a connection as
   * a -> b where a comes before b, but for {@link ConnectionsFinder} it is easier to
   * store both a -> b and b -> a.
   * @param connections The connections collection to render symmetric
   * @return The supplied Map
   */
  public static Map<String, List<String>> createSymmetricConnections(Map<String, List<String>> connections) {
    Map<String, List<String>> newEntries = new HashMap<>();
    connections.entrySet()
      .forEach(entry -> {
        final String left = entry.getKey();
        entry.getValue().stream()
          .forEach(right -> addEntries(newEntries, right, left));
      });
    return newEntries;
  }
  
  /**
   * Wrapper for writing content to a file.
   * @param filename The file to write to
   * @param content The content to store in the file
   */
  private static void writeToFile(String filename, String content) {
    try {
      Files.write(Paths.get(filename), content.getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Could not write to file '" + filename + "'", e);
    }
  }
  
  private static String readFromFile(String filename) {
    try {
      return String.join("", 
          Files.readAllLines(Paths.get(filename)));
    } catch (IOException e) {
      throw new IllegalStateException("Could not read from file", e);
    }
  }
  
  private static void addEntries(Map<String, List<String>> connections, String left, String right) {
    if (connections.get(left) == null) {
      connections.put(left, new ArrayList<>());
    }
    connections.get(left).add(right);
    
    if (connections.get(right) == null) {
      connections.put(right, new ArrayList<>());
    }
    connections.get(right).add(left);
  }

}
