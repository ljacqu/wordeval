package ch.ljacqu.wordeval.wordgraph;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ConnectionsFinderTest {
  
  private static ConnectionsFinder finder;
  
  @BeforeClass
  public static void setUpFinder() {    
    String givenWords = "acre, care, car, bar, bare, bear, bears, boars, boar, "
        + "beers, bees, bee, be, bet, beet, meet, meat, heat, hat, rat, brat, "
        + "isolate, isolate, isolates";
    
    List<String> words = Arrays.stream(givenWords.split(","))
      .map(String::trim)
      .sorted()
      .collect(Collectors.toList());
    
    ConnectionsBuilder builder = new ConnectionsBuilder(words);
    Map<String, List<String>> connections = builder.getConnections();
    
    finder = ConnectionsFinder.createFromAsymmetrical(connections);
  }
  
  @Test
  public void shouldFindShortestPath() {
    List<String> path = finder.findConnection("beet", "meat");
    assertThat(path, contains("beet", "meet", "meat"));
    
    path = finder.findConnection("boar", "car");
    assertThat(path, contains("boar", "bar", "car"));
    
    path = finder.findConnection("be", "rat");
    assertThat(path, contains("be", "bee", "beet", "meet", "meat", "heat", "hat", "rat"));
  }
  
  @Test
  public void shouldReturnShortestPathForSpecialCases() {
    // left == right
    List<String> path = finder.findConnection("beet", "beet");
    assertThat(path, contains("beet"));
    
    // left.empty or right.empty
    assertThat(finder.findConnection("bogus", "meet"), empty());
    assertThat(finder.findConnection("meet", "bogus"), empty());
    assertThat(finder.findConnection("bogus", "bogus2"), empty());
    
    // no connection
    assertThat(finder.findConnection("meet", "isolate"), empty());
    assertThat(finder.findConnection("isolated", "meet"), empty());
  }

}
