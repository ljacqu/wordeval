package ch.ljacqu.wordeval.wordgraph;

import ch.ljacqu.wordeval.AppData;

public class WordGraphDriver {
  
  static {
    AppData.init();
  }
  
  public static void main(String[] args) {
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
