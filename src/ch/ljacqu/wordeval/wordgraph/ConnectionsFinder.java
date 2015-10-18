package ch.ljacqu.wordeval.wordgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class to find the shortest connection in the word graph between
 * two words.
 */
public class ConnectionsFinder {
  
  /** Collection of word connections, symmetrical. */
  private Map<String, List<String>> connections;
  
  private ConnectionsFinder(Map<String, List<String>> connections) {
    this.connections = connections;
  }
  
  /**
   * Creates a ConnectionsFinder instance with the given collection of symmetrical connections.
   * @param connections Collection of connections (symmetrical)
   * @return The instantiated ConnectionsFinder
   */
  public static ConnectionsFinder createFromSymmetrical(Map<String, List<String>> connections) {
    return new ConnectionsFinder(connections);
  }
  
  /**
   * Creates a ConnectionsFinder instance with the given collection of asymmetrical connections.
   * Transforms the connections to a symmetrical collection first.
   * @param connections Collection of connections (asymmetrical)
   * @return The instantiated ConnectionsFinder
   */
  public static ConnectionsFinder createFromAsymmetrical(Map<String, List<String>> connections) {
    Map<String, List<String>> symmetricConnections = 
        WordGraphService.createSymmetricConnections(connections);
    return new ConnectionsFinder(symmetricConnections);
  }
  
  /**
   * Finds the shortest path in the graph between the two given words.
   * @param left The first word
   * @param right The second word
   * @return The shortest path between the words, or empty list if non-existent
   */
  public List<String> findConnection(String left, String right) {
    // Ensure that left != right, and that both have connections
    if (left.equals(right)) {
      return Arrays.asList(left);
    } else if (connections.get(left) == null || connections.get(right) == null) {
      return new ArrayList<>();
    }

    Set<String> usedWords = new HashSet<>();
    List<WordPath> pathsToTry = new LinkedList<>();
    Placeholder<WordPath> successPath = new Placeholder<>();
    // We actually look up right -> left because WordPath.toList() returns
    // the path in reverse order
    // TODO: Optimize by connection size of left and right?
    pathsToTry.add(new WordPath(right));
    while (!pathsToTry.isEmpty() && !successPath.isPresent()) {
      WordPath currentPath = pathsToTry.remove(0);
      pathsToTry.addAll(findConn(currentPath, left, usedWords, successPath));
    }
    if (successPath.isPresent()) {
      return successPath.getValue().toList();
    }
    return new ArrayList<>();
  }

  private List<WordPath> findConn(final WordPath path, final String right, Set<String> usedWords,
                                  Placeholder<WordPath> successPath) {
    List<WordPath> pathList = new ArrayList<>();
    final String left = path.word;
    // connections.get(left) is never null, unless `connections` isn't truly symmetrical
    for (String other : connections.get(left)) {
      if (usedWords.contains(other)) {
        continue;
      } else if (other.equals(right)) {
        successPath.setValue(new WordPath(path, other));
        return new ArrayList<>();
      } else {
        pathList.add(new WordPath(path, other));
        usedWords.add(other);
      }
    }
    return pathList;
  }

  @AllArgsConstructor
  private static final class WordPath {
    public final WordPath path;
    public final String word;

    public WordPath(String initialWord) {
      this.word = initialWord;
      this.path = null;
    }

    @Override
    public String toString() {
      return toList().toString();
    }

    public List<String> toList() {
      List<String> words = new ArrayList<>();
      words.add(word);
      WordPath currentPath = path;
      while (currentPath != null) {
        words.add(currentPath.word);
        currentPath = currentPath.path;
      }
      return words;
    }
  }

  @Data
  private static final class Placeholder<T> {
    private T value = null;

    public boolean isPresent() {
      return value != null;
    }
  }
}
