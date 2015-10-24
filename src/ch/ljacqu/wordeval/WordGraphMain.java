package ch.ljacqu.wordeval;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;

import org.jgrapht.graph.DefaultEdge;
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
    SimpleGraph<String, DefaultEdge> graph;
    
    Optional<String> exportFilename = initializeExportFilename(scanner, dictionaryCode);
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
  
  private static void connectionsFinderLoop(Scanner scanner, SimpleGraph<String, DefaultEdge> graph) {
    System.out.println("Connections finder\n");
    String left, right;
    while (true) {
      System.out.print("Enter word 1 (! to quit): ");
      left = scanner.nextLine().trim();
      if (left.equals("!")) {
        break;
      }
      
      System.out.print("Enter word 2 (! to quit): ");
      right = scanner.nextLine().trim();
      if (right.equals("!")) {
        break;
      }

      System.out.println(WordGraphService.getShortestPath(graph, left, right));
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
