

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.xmlbeans.impl.common.Levenshtein;

import lombok.Getter;

@Getter
public class LjTemp {

  private static final int MIN_DISTANCE = 1;

  // temp
  private static final int WORD_LIMIT = 100000;

  private List<String> words;
  private Map<String, List<Connection>> connections = new HashMap<>();

  public static void main(String[] args) {
    LjTemp lj = new LjTemp();
    lj.computeConnections();
    System.out.println(lj.getConnections());
    lj.findConn("alarm", "abiyi");
    lj.findConn("car", "bet");
    lj.findConn("car", "nonexistent");
    lj.findConn("brute", "acute");
    lj.findConn("acre", "bear");
  }

  public LjTemp() {
    //loadTestWords();
    loadDictionary();
    initConnections();
  }

  private void loadDictionary() {
    Path path = Paths.get("C:\\Users\\ljacques\\Downloads\\dictionary.txt");
    try {
      words = Files.lines(path)
        .map(String::trim)
        .map(String::toLowerCase)
        .distinct()
        .limit(WORD_LIMIT)
        .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalStateException("Could not read dict", e);
    }
  }

  private void loadTestWords() {
    String givenWords = "acre, care, car, bar, bare, bear, bears, boars, boar, beers, bees, bee, be, bet, beet, meet, "
      + "meat, heat, hat, rat, brat";

    words = Arrays.stream(givenWords.split(","))
      .map(String::trim)
      .collect(Collectors.toList());

    Collections.sort(words);
  }

  public void computeConnections() {

    for (int i = 0; i < words.size(); ++i) {
      final String leftWord = words.get(i);
      for (int j = i + 1; j < words.size(); ++j) {
        final String rightWord = words.get(j);
        if (Math.abs(rightWord.length() - leftWord.length()) > 1) {
          continue;
        }
        final int distance = Levenshtein.distance(rightWord, leftWord);
        if (distance <= MIN_DISTANCE) {
          addConnection(leftWord, rightWord);
        }
      }
      System.out.println("Found " + connections.get(leftWord).size() + " connections for " + leftWord);
    }
    System.out.println("Processed " + words.size() + " words");
  }

  // BFS
  public void findConn(String left, String right) {
    Set<String> usedWords = new HashSet<>();
    List<WordPath> pathsToTry = new ArrayList<>();
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

  private void addConnection(String left, String right) {
    final Connection conn = new Connection(left, right);
    connections.get(left).add(conn);
    connections.get(right).add(conn);
  }

  private void initConnections() {
    connections = new HashMap<>();
    words.stream()
      .forEach(word -> connections.put(word, new ArrayList<>()));
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
  }

}
