package ch.jalu.wordeval.wordgraph;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.List;

/**
 * Finds which words have a Damerau-Levenshtein distance of 1 and saves these
 * connections, forming a graph over the dictionary words.
 */
@Slf4j
public class GraphBuilder {
  
  /** 
   * The interval in which to display the words that have been processed.
   * This must be a number corresponding to 2^k-1, where k >= 1 (e.g. 255, 1023).
   */
  private static final int STAT_INTERVAL = 255;
  
  @Getter
  private SimpleGraph<String, DefaultWeightedEdge> graph;
  
  /**
   * Builds a new ConnectionsBuilder object and computes the
   * connections based on the given list of words.
   *
   * @param words the list of words to process
   */
  public GraphBuilder(List<String> words) {
    constructGraph(words);
  }

  private void constructGraph(List<String> words) {
    graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    for (int i = 0; i < words.size(); ++i) {
      final String leftWord = words.get(i);
      graph.addVertex(leftWord);
      for (int j = i + 1; j < words.size(); ++j) {
        final String rightWord = words.get(j);
        if (DamerauLevenshtein.isEditDistance1(rightWord, leftWord)) {
          // vertices must always be added before edges. addVertex() checks against a Set,
          // so the check is efficient enough for us to just always call the functions
          graph.addVertex(rightWord);
          graph.addEdge(leftWord, rightWord);
        }
      }
      if ((i & STAT_INTERVAL) == STAT_INTERVAL) {
        log.info("Processed {} words", i);
      }
    }
    log.info("Processed total {} words", words.size());
  }
}
