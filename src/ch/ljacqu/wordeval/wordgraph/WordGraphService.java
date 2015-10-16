package ch.ljacqu.wordeval.wordgraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

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
    try {
      Files.write(
        Paths.get(filename),
        gson.toJson(connections).getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Could not export connections to file '" + filename + "'");
    }
  }
  
  public static void convertToGraphViz(String filename, Map<String, List<String>> connections) {
    StringBuilder builder = new StringBuilder("digraph G {");
    connections.entrySet()
      .forEach(entry -> {
        final String left = entry.getKey();
        entry.getValue()
          .forEach(right -> builder.append("\n\t")
              .append(left).append(" -- ").append(right));
      });
    builder.append("\n}");
    
    try {
      Files.write(
          Paths.get(filename), 
          builder.toString().getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Could not export GraphViz format to file '" + filename + "'");
    }
  }
  
  public static void createSymmetricConnections(HashMap<String, List<String>> connections) {
    connections.entrySet()
      .forEach(entry -> {
        final String left = entry.getKey();
        entry.getValue()
          .forEach(right -> addEntry(right, left, connections));
      });
  }
  
  private static void addEntry(String key, String value, HashMap<String, List<String>> connections) {
    if (connections.get(key) == null) {
      connections.put(key, new ArrayList<>());
    }
    connections.get(key).add(value);
  }

}
