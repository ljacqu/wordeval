package ch.jalu.wordeval;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.wordgraph.GraphBuilder;
import ch.jalu.wordeval.wordgraph.WordGraphService;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * Entry point for the word graph feature of <i>wordeval</i>.
 */
public class WordGraphMain extends SpringContainedRunner {
  
  /** Directory for graph exports. */
  private static final String GRAPH_EXPORT_DIRECTORY = "export/graph/";

  @Autowired
  private AppData appData;

  @Autowired
  private WordGraphService wordGraphService;
  
  /**
   * Main function.
   *
   * @param args .
   */
  public static void main(String[] args) {
    runApplication(WordGraphMain.class);
  }

  @Override
  public void run(String... args) {
    try (Scanner scanner = new Scanner(System.in)) {
      String dictionaryCode = initializeDictionaryCode(scanner);

      Optional<String> exportFilename = initializeExportFilename(scanner, dictionaryCode);
      SimpleGraph<String, DefaultWeightedEdge> graph;
      if (exportFilename.isPresent()) {
        graph = wordGraphService.importConnections(exportFilename.get());
      } else {
        GraphBuilder builder = new GraphBuilder(appData.getDictionary(dictionaryCode));
        processExportChoice(scanner, dictionaryCode, builder);
        graph = builder.getGraph();
      }

      connectionsFinderLoop(scanner, graph);
    }
  }

  private static String initializeDictionaryCode(Scanner scanner) {
    System.out.println("Dictionary code:");
    return scanner.nextLine().trim(); 
  }
  
  private static Optional<String> initializeExportFilename(Scanner scanner, String dictionaryCode) {
    String exportFilename = getExportFilename(dictionaryCode);
    boolean useExport = false;
    if (Files.isRegularFile(Paths.get(exportFilename))) {
      System.out.println("Graph for '" + dictionaryCode + "' is saved. Load from cache? [y/n]");
      useExport = getChoice(scanner);
    }
    return useExport ? Optional.of(exportFilename) : Optional.empty();
  }
  
  private void processExportChoice(Scanner scanner, String dictionaryCode, GraphBuilder builder) {
    System.out.println("Export connections to file? [y/n]");
    boolean doExport = getChoice(scanner);
    if (doExport) {
      wordGraphService.exportConnections(
          getExportFilename(dictionaryCode), builder.getGraph());
    }
  }
  
  private void connectionsFinderLoop(Scanner scanner, SimpleGraph<String, DefaultWeightedEdge> graph) {
    System.out.println("Connections finder\n");
    String left, right;
    List<String> disabledVertices = new ArrayList<>();
    while (true) {
      System.out.print("Enter word 1 (empty string to quit, ! to disable vertices): ");
      left = scanner.nextLine().trim();
      if (left.isEmpty()) {
        break;
      } else if ("!".equals(left)) {
        toggleVertices(scanner, graph, disabledVertices);
        continue;
      }

      System.out.print("Enter word 2: ");
      right = scanner.nextLine().trim();

      Set<String> path = wordGraphService.getShortestPath(graph, left, right);
      System.out.println(path);
    }
  }
  
  private void toggleVertices(Scanner scanner, SimpleGraph<String, DefaultWeightedEdge> graph,
                              List<String> disabledVertices) {
    while (true) {
      System.out.println("Disable a vertex? (empty string to quit, ! to see the list of disabled vertices):");
      String word = scanner.nextLine().trim();
      if (word.isEmpty()) {
        return;
      } else if ("!".equals(word)) {
        System.out.println(disabledVertices);
        continue;
      }
      
      boolean result;
      if (disabledVertices.contains(word)) {
        result = wordGraphService.enableVertexEdges(graph, word);
        if (result) {
          disabledVertices.remove(word);
          System.out.println("Enabled '" + word + "'");
        }
      } else {
        result = wordGraphService.disableVertexEdges(graph, word);
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
  
  private static String getExportFilename(String code) {
    return GRAPH_EXPORT_DIRECTORY + code + ".json";
  }
  
  private static boolean getChoice(Scanner scanner) {
    while (true) {
      String line = scanner.nextLine().trim();
      if ("y".equals(line)) {
        return true;
      } else if ("n".equals(line)) {
        return false;
      }
      System.out.print("\nPlease enter 'y' or 'n': ");
    }
  }

}
