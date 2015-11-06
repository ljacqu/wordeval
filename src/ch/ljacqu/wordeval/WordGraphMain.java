package ch.ljacqu.wordeval;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import ch.ljacqu.wordeval.wordgraph.GraphBuilder;
import ch.ljacqu.wordeval.wordgraph.WordGraphService;

/**
 * Entry point for the word graph feature of <i>wordeval</i>.
 */
public final class WordGraphMain {
  
  static {
    AppData.init();
  }

  /** Directory for graph exports. */
  private static final String GRAPH_EXPORT_DIRECTORY = "export/graph/";
  
  private WordGraphMain() {
  }
  
  /**
   * Main function.
   * @param args .
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    String dictionaryCode = initializeDictionaryCode(scanner);
    
    Optional<String> exportFilename = initializeExportFilename(scanner, dictionaryCode);
    SimpleGraph<String, DefaultWeightedEdge> graph;
    if (exportFilename.isPresent()) {
      graph = WordGraphService.importConnections(exportFilename.get());
    } else {
      GraphBuilder builder = new GraphBuilder(dictionaryCode);
      processExportChoice(scanner, dictionaryCode, builder);
      graph = builder.getGraph();
    }
    
    connectionsFinderLoop(scanner, graph);
    scanner.close();
  }
  
  private static String initializeDictionaryCode(Scanner scanner) {
    System.out.println("Dictionary code:");
    return scanner.nextLine().trim(); 
  }
  
  private static Optional<String> initializeExportFilename(Scanner scanner, String dictionaryCode) {
    String exportFilename = getExportFilename(dictionaryCode, "json");
    boolean useExport = false;
    if (Files.isRegularFile(Paths.get(exportFilename))) {
      System.out.println("Graph for '" + dictionaryCode + "' is saved. Load from cache? [y/n]");
      useExport = getChoice(scanner);
    }
    return useExport ? Optional.of(exportFilename) : Optional.empty();
  }
  
  private static void processExportChoice(Scanner scanner, String dictionaryCode, 
      GraphBuilder builder) {
    System.out.println("Export connections to file? [y/n]");
    boolean doExport = getChoice(scanner);
    if (doExport) {
      WordGraphService.exportConnections(
          getExportFilename(dictionaryCode, "json"), builder.getGraph());
    }
  }
  
  private static void connectionsFinderLoop(Scanner scanner, SimpleGraph<String, DefaultWeightedEdge> graph) {
    System.out.println("Connections finder\n");
    String left, right;
    List<String> disabledVertices = new ArrayList<>();
    while (true) {
      System.out.print("Enter word 1 (empty string to quit, ! to disable vertices): ");
      left = scanner.nextLine().trim();
      if (left.isEmpty()) {
        break;
      } else if (left.equals("!")) {
        toggleVertices(scanner, graph, disabledVertices);
        continue;
      }
      
      System.out.print("Enter word 2 (empty string to quit): ");
      right = scanner.nextLine().trim();
      if (right.isEmpty()) {
        break;
      }

      Set<String> path = WordGraphService.getShortestPath(graph, left, right);
      System.out.println(path);
    }
  }
  
  private static void toggleVertices(Scanner scanner, SimpleGraph<String, DefaultWeightedEdge> graph,
      List<String> disabledVertices) {
    while (true) {
      System.out.println("Disable a vertex? (empty string to quit, ! to see the list of disabled vertices):");
      String word = scanner.nextLine().trim();
      if (word.isEmpty()) {
        return;
      } else if (word.equals("!")) {
        System.out.println(disabledVertices);
        continue;
      }
      
      boolean result;
      if (disabledVertices.contains(word)) {
        result = WordGraphService.enableVertexEdges(graph, word);
        if (result) {
          disabledVertices.remove(word);
          System.out.println("Enabled '" + word + "'");
        }
      } else {
        result = WordGraphService.disableVertexEdges(graph, word);
        if (result) {
          disabledVertices.add(word); 
          System.out.println("Disabled '" + word + "'");
        }
      }
      if (!result) {
        System.out.println("No such vertex in graph");
      }
    }
  }
  
  private static String getExportFilename(String code, String ending) {
    return GRAPH_EXPORT_DIRECTORY + code + "." + ending;
  }
  
  private static boolean getChoice(Scanner scanner) {
    while (true) {
      String line = scanner.nextLine().trim();
      if (line.equals("y")) {
        return true;
      } else if (line.equals("n")) {
        return false;
      }
      System.out.print("\nPlease enter 'y' or 'n': ");
    }
  }

}
