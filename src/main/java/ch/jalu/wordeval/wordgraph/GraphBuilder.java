package ch.jalu.wordeval.wordgraph;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import ch.jalu.wordeval.evaluation.WordCollector;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Finds which words have a Damerau-Levenshtein distance of 1 and saves these
 * connections, forming a graph over the dictionary words.
 */
@Log4j2
public class GraphBuilder {

  /** The minimum distance for words to be connected. */
  private static final int MIN_DISTANCE = 1;
  
  /** 
   * The interval in which to display the words that have been processed.
   * This must be a number corresponding to 2^k-1, where k >= 1 (e.g. 255, 1023).
   */
  private static final int STAT_INTERVAL = 255;
  
  @Getter
  private SimpleGraph<String, DefaultWeightedEdge> graph;
  
  /**
   * Builds a new ConnectionsBuilder object and computes the
   * connections for the given dictionary.
   * @param dictionary the dictionary code
   */
  public GraphBuilder(String dictionary) {    
    this(getDictionaryWords(dictionary));
  }
  
  /**
   * Builds a new ConnectionsBuilder object and computes the
   * connections based on the given list of words.
   * @param words the list of words to process
   */
  public GraphBuilder(List<String> words) {
    constructGraph(words);
  }
  
  private static List<String> getDictionaryWords(String dictionaryCode) {
    WordCollector collector = new WordCollector();
    return collector.getSortedWordsFromDictionary(dictionaryCode);
  }

  private void constructGraph(List<String> words) {
    graph = new SimpleGraph<>(DefaultWeightedEdge.class);
    // deleteCost, insertCost, replaceCost, swapCost    
    DamerauLevenshteinAlgorithm levenshtein = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
    for (int i = 0; i < words.size(); ++i) {
      final String leftWord = words.get(i);
      graph.addVertex(leftWord);
      for (int j = i + 1; j < words.size(); ++j) {
        final String rightWord = words.get(j);
        if (Math.abs(rightWord.length() - leftWord.length()) <= MIN_DISTANCE
            && levenshtein.execute(rightWord, leftWord) <= MIN_DISTANCE) {
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
