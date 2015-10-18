package ch.ljacqu.wordeval.wordgraph;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinderTestDriver {
  
  public static void main(String[] args) { 
    String givenWords = "acre, care, car, bar, bare, bear, bears, boars, boar, "
        + "beers, bees, bee, be, bet, beet, meet, meat, heat, hat, rat, brat";
    
    List<String> words = Arrays.stream(givenWords.split(","))
      .map(String::trim)
      .sorted()
      .collect(Collectors.toList());

    ConnectionsBuilder builder = new ConnectionsBuilder(words);
    
    ConnectionsFinder finder = ConnectionsFinder.createFromAsymmetrical(builder.getConnections());
    printConns(finder, "acre", "bee");
    printConns(finder, "heat", "boars");
    printConns(finder, "brat", "boars");
  }
  
  private static void printConns(ConnectionsFinder finder, String left, String right) {
    System.out.println(finder.findConnection(left, right));
  }

}
