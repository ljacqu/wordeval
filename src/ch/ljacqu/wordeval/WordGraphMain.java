package ch.ljacqu.wordeval;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import ch.ljacqu.wordeval.wordgraph.ConnectionsBuilder;
import ch.ljacqu.wordeval.wordgraph.ConnectionsFinder;
import ch.ljacqu.wordeval.wordgraph.WordGraphService;

/**
 * Entry point for the word graph feature of <i>wordeval</i>.
 */
public class WordGraphMain {
  
  static {
    AppData.init();
  }

  /** Directory for graph exports. */
  private static final String GRAPH_EXPORT_DIRECTORY = "export/graph/";
  
  /**
   * Main function.
   * @param args .
   */
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String dictionaryCode = initializeDictionaryCode(sc);
    Map<String, List<String>> connections;
    
    Optional<String> exportFilename = initializeExportFilename(sc, dictionaryCode);
    if (exportFilename.isPresent()) {
      connections = WordGraphService.importConnections(exportFilename.get());
    } else {
      ConnectionsBuilder builder = new ConnectionsBuilder(dictionaryCode);
      processExportChoice(sc, dictionaryCode, builder);
      connections = builder.getConnections();
    }
    
    ConnectionsFinder finder = ConnectionsFinder.createFromAsymmetrical(connections);
    connectionsFinderLoop(finder, sc);
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
      useExport = !scanner.nextLine().trim().equals("n");;
    }
    if (useExport) {
      return Optional.of(exportFilename);
    }
    return Optional.empty();
  }
  
  private static void processExportChoice(Scanner scanner, String dictionaryCode, 
      ConnectionsBuilder builder) {
    System.out.println("Export connections to file? [y/n]");
    boolean doExport = scanner.nextLine().trim().equals("y");
    if (doExport) {
      WordGraphService.exportConnections(
          getExportFilename(dictionaryCode), builder.getConnections());
    }
  }
  
  private static void connectionsFinderLoop(ConnectionsFinder finder, Scanner scanner) {
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

      System.out.println(finder.findConnection(left, right));
    }
    scanner.close();
  }
  
  private static String getExportFilename(String code) {
    return GRAPH_EXPORT_DIRECTORY + code + ".json";
  }

}
