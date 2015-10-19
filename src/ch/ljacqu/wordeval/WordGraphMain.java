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
    Map<String, List<String>> connections;
    
    Optional<String> exportFilename = initializeExportFilename(scanner, dictionaryCode);
    if (exportFilename.isPresent()) {
      connections = WordGraphService.importConnections(exportFilename.get());
    } else {
      ConnectionsBuilder builder = new ConnectionsBuilder(dictionaryCode);
      processExportChoice(scanner, dictionaryCode, builder);
      connections = builder.getConnections();
    }
    
    boolean hasDotFile = processDotFileChoice(scanner, dictionaryCode, connections);
    if (hasDotFile) {
      processDotTransformation(scanner, dictionaryCode);
    }
    
    ConnectionsFinder finder = ConnectionsFinder.createFromAsymmetrical(connections);
    connectionsFinderLoop(finder, scanner);
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
          getExportFilename(dictionaryCode, "json"), builder.getConnections());
    }
  }
  
  private static boolean processDotFileChoice(Scanner scanner, String dictionaryCode, 
      Map<String, List<String>> connections) {
    String dotFile = getExportFilename(dictionaryCode, "dot");

    boolean writeDotFile;
    boolean hasDotFile;
    if (Files.isRegularFile(Paths.get(dotFile))) {
      System.out.println("Dot file " + dotFile + " already exists. Generate again? [y/n]");
      writeDotFile = getChoice(scanner);
      hasDotFile = true;
    } else {
      System.out.println("Dot file for " + dictionaryCode + " does not exist. Generate? [y/n]");
      writeDotFile = getChoice(scanner);
      hasDotFile = false;
    }

    if (writeDotFile) {
      WordGraphService.writeDotFile(connections, dotFile);
      hasDotFile = true;
    }
    return hasDotFile;
  }
  
  private static void processDotTransformation(Scanner scanner, String code) {
    System.out.println("Create graph from .dot file? [y/n]");
    boolean userChoice = getChoice(scanner);
    if (!userChoice) {
      return;
    }
    
    String dotFile = getExportFilename(code, "dot");
    String pngFile = getExportFilename(code, "png");
    WordGraphService.executeDotProcess(dotFile, pngFile);
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
