package ch.ljacqu.wordeval.wordgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class ConnectionsBuilder {

  private static final int MIN_DISTANCE = 1;
  
  private List<String> words;
  
  @Getter
  private Map<String, List<String>> connections;
  
  public ConnectionsBuilder(String dictionary) {    
    WordCollector collector = new WordCollector();
    words = collector.getSortedWordsFromDictionary(dictionary);
    collector = null;
    initializeConnections();
    computeConnections();
  }
  
  private void initializeConnections() {
    connections = new HashMap<>();
    words.stream()
         .forEach(word -> connections.put(word, new ArrayList<>()));
  }

  private void computeConnections() {
    // deleteCost, insertCost, replaceCost, swapCost    
    DamerauLevenshteinAlgorithm levenshtein = new DamerauLevenshteinAlgorithm(1, 1, 2, 1);
    for (int i = 0; i < words.size(); ++i) {
      final String leftWord = words.get(i);
      for (int j = i + 1; j < words.size(); ++j) {
        final String rightWord = words.get(j);
        if (Math.abs(rightWord.length() - leftWord.length()) > 1) {
          continue;
        }
        final int distance = levenshtein.execute(rightWord, leftWord);
        if (distance <= MIN_DISTANCE) {
          saveConnection(leftWord, rightWord);
        }
      }
      if ((i & 255) == 255) {
        System.out.println("Processed " + i + " words");
      }
    }
    System.out.println("Processed total " + words.size() + " words");
  }
  
  private void saveConnection(String left, String right) {
    connections.get(left).add(right);
  }
  
  public void removeIsolatedWords() {
    Iterator<Map.Entry<String, List<String>>> it = 
        connections.entrySet().iterator();
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

  /* TODO: Move `findConn` stuff to another class
  // BFS
  public void findConn(String left, String right) {
    Set<String> usedWords = new HashSet<>();
    // TODO: Change `pathsToTry` to LinkedList
    List<WordPath> pathsToTry = new ArrayList<>();
    // TODO: Change `successPath` to Optional type if possible
    List<WordPath> successPath = new ArrayList<>();
    pathsToTry.add(new WordPath(left));
    while (!pathsToTry.isEmpty() && successPath.isEmpty()) {
      WordPath currentPath = pathsToTry.remove(0);
      pathsToTry.addAll(findConn(currentPath, right, usedWords, successPath));
    }
    if (successPath.isEmpty()) {
      System.out.println("Did not find any connection for " + left + " to " + right);
    } else {
      System.out.println(successPath.get(0));
    }
  }

  private List<WordPath> findConn(final WordPath path, final String right, Set<String> usedWords,
                                  List<WordPath> successList) {
    List<WordPath> pathList = new ArrayList<>();
    final String left = path.newWord;
    if (connections.get(left) == null) {
      return new ArrayList<>();
    }
    for (Connection c : connections.get(left)) {
      final String other = c.getOther(left);
      if (usedWords.contains(other)) {
        continue;
      } else if (other.equals(right)) {
        successList.add(new WordPath(path, other));
        return new ArrayList<>();
      } else {
        pathList.add(new WordPath(path, other));
        usedWords.add(other);
      }
    }
    return pathList;
  }

  private static class Connection {
    public final String left;
    public final String right;
    public Connection(String left, String right) {
      this.left = left;
      this.right = right;
    }
    @Override
    public String toString() {
      return "{" + left + "," + right + "}";
    }
    public String getOther(String current) {
      return current.equals(left) ? right : left;
    }
  }

  private static final class WordPath {
    public WordPath prevPath;
    public String newWord;
    public WordPath(String initialWord) {
      this.newWord = initialWord;
      this.prevPath = null;
    }
    public WordPath(WordPath prevPath, String word) {
      this.prevPath = prevPath;
      this.newWord = word;
    }
    @Override
    public String toString() {
      StringBuilder str = new StringBuilder(newWord);
      WordPath path = prevPath;
      while (path != null) {
        str.append(" -> ")
          .append(path.newWord);
        path = path.prevPath;
      }
      return str.toString();
    }
  }*/

}
