package ch.ljacqu.wordeval;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.wordgraph.ConnectionsBuilder;
import ch.ljacqu.wordeval.wordgraph.ConnectionsFinder;
import ch.ljacqu.wordeval.wordgraph.ConnectionsFinderTest;
import ch.ljacqu.wordeval.wordgraph.WordGraphService;

public class WordGraphMain {
  
  static {
    AppData.init();
  }
  
  private static ConnectionsFinder finder;
  
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
    
    finder = ConnectionsFinder.createFromAsymmetrical(connections);
    connectionsFinderLoop(finder, sc);
  }
  
  private static String initializeDictionaryCode(Scanner scanner) {
    System.out.println("Dictionary code:");
    return scanner.nextLine().trim(); 
  }
  
  private static Optional<String> initializeExportFilename(Scanner scanner, String dictionaryCode) {
    String exportFilename = WordGraphService.getExportFilename(dictionaryCode);
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
          WordGraphService.getExportFilename(dictionaryCode), builder.getConnections());
    }
  }
  
  private static void connectionsFinderLoop(ConnectionsFinder finder, Scanner scanner) {
    System.out.println("Connections finder\n");
    String left, right;
    while (true) {
      System.out.println("Enter word 1 (! to quit)");
      left = scanner.nextLine().trim();
      if (left.equals("!")) {
        break;
      }
      
      System.out.println("Enter word 2 (! to quit)");
      right = scanner.nextLine().trim();
      if (right.equals("!")) {
        break;
      }

      System.out.println(finder.findConnection(left, right));
    }
    scanner.close();
  }
  
  public static void main2(String[] args) {
    ConnectionsBuilder builder = new ConnectionsBuilder("en-test");
    builder.removeIsolatedWords();
    
    WordGraphService.exportConnections("asdf", builder.getConnections());
    System.out.println(builder.getConnections());
    
    /*connection.findConn("alarm", "abiyi");
    connection.findConn("car", "bet");
    connection.findConn("car", "nonexistent");
    connection.findConn("brute", "acute");
    connection.findConn("acre", "bear");*/
  }

}
