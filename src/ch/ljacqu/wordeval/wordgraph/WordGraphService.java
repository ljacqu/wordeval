package ch.ljacqu.wordeval.wordgraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
    Map<String, String> specialVertices = new HashMap<>();
    StringBuilder builder = new StringBuilder("graph G {");
    connections.entrySet()
      .forEach(entry -> {
        final String left = entry.getKey();
        entry.getValue()
          .forEach(right -> builder.append("\n\t")
              .append(wordToDotNode(left, specialVertices)).append(" -- ")
              .append(wordToDotNode(right, specialVertices)).append(";"));
      });
    specialVertices.entrySet()
      .forEach(entry -> {
        builder.append("\n\t" + entry.getValue() + " [label=\""
            + entry.getKey().replace("\"", "\\\"") + "\"];");
      });
    builder.append("\n}");
    return builder.toString();
  }
  
  private static String wordToDotNode(String word, Map<String, String> vertices) {
    List<String> dotKeywords = Arrays.asList("digraph");
    if (!word.matches(".*[^a-z]+.*") && !dotKeywords.contains(word)) {
      return word;
    } else if (vertices.containsKey(word)) {
      return vertices.get(word);
    }
    
    if (dotKeywords.contains(word)) {
      vertices.put(word, "_" + word);
      return "_" + word;
    }
    
    String decomposedWord = Normalizer.normalize(word, Form.NFD);
    // TODO: Deal with accents by replacing them with a digit from the decomposed form
    String vertexKey = decomposedWord.replaceAll("'", "0");
    vertices.put(word, vertexKey);
    return vertexKey;
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
  
  public static void writeDotFile(Map<String, List<String>> connections, String filename) {
    writeToFile(filename, convertToDotGraph(connections));
  }
  
  public static void executeDotProcess(String dotFile, String outputFile) {
    String command = "dot -Tpng " + dotFile + " -o " + outputFile;
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);
    processBuilder.redirectOutput(Redirect.INHERIT);
    try {
      processBuilder.start();
    } catch (IOException e) {
      throw new IllegalStateException("Could not execute process", e);
    }
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
