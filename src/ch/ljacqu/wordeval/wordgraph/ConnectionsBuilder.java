package ch.ljacqu.wordeval.wordgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * Finds which words have a Damerau-Levenshtein distance of 1 and saves these
 * connections, forming a graph over the dictionary words.
 */
public class ConnectionsBuilder {

  /** The minimum distance for words to be connected. */
  private static final int MIN_DISTANCE = 1;
  
  /** 
   * The interval in which to display the words that have been processed.
   * This must be a number corresponding to 2^k-1, where k >= 1 (e.g. 255, 1023).
   */
  private static final int STAT_INTERVAL = 255;
  
  private List<String> words;
  
  @Getter
  private Map<String, List<String>> connections;
  
  /**
   * Builds a new ConnectionsBuilder object and computes the
   * connections for the given dictionary.
   * @param dictionary The dictionary code
   */
  public ConnectionsBuilder(String dictionary) {    
    this(getDictionaryWords(dictionary));
  }
  
  /**
   * Builds a new ConnectionsBuilder object and computes the
   * connections based on the given list of words.
   * @param words The list of words to process
   */
  public ConnectionsBuilder(List<String> words) {
    this.words = words;
    initializeConnections();
    computeConnections();
  }
  
  private static List<String> getDictionaryWords(String dictionaryCode) {
    WordCollector collector = new WordCollector();
    return collector.getSortedWordsFromDictionary(dictionaryCode);
  }
  
  private void initializeConnections() {
    connections = new HashMap<>();
    words.stream()
         .forEach(word -> connections.put(word, new ArrayList<>()));
  }

  private void computeConnections() {
    // deleteCost, insertCost, replaceCost, swapCost    
    DamerauLevenshteinAlgorithm levenshtein = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
    for (int i = 0; i < words.size(); ++i) {
      final String leftWord = words.get(i);
      for (int j = i + 1; j < words.size(); ++j) {
        final String rightWord = words.get(j);
        if (Math.abs(rightWord.length() - leftWord.length()) <= MIN_DISTANCE
            && levenshtein.execute(rightWord, leftWord) <= MIN_DISTANCE) {
          connections.get(leftWord).add(rightWord);
        }
      }
      if ((i & STAT_INTERVAL) == STAT_INTERVAL) {
        System.out.println("Processed " + i + " words");
      }
    }
    System.out.println("Processed total " + words.size() + " words");
  }
  
  /**
   * Removes entries in the connections map for words which are isolated,
   * i.e. for words which do not have any connections to others.
   */
  public void removeIsolatedWords() {
    Iterator<Map.Entry<String, List<String>>> it = connections.entrySet().iterator();
    while (it.hasNext()) {
      if (it.next().getValue().isEmpty()) {
        it.remove();
      }
    }
  }
  
  /*private void loadTestWords() {
    // TODO: Move test words to a test class
    String givenWords = "acre, care, car, bar, bare, bear, bears, boars, boar, beers, bees, bee, be, bet, beet, meet, "
      + "meat, heat, hat, rat, brat";
  
    Arrays.stream(givenWords.split(","))
      .map(String::trim)
      .sorted()
      .forEach(word -> processWord(word, word));
  }*/
}
