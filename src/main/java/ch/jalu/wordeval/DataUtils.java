package ch.jalu.wordeval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Wrapper for interaction with the file system and JSON operations.
 */
@NoArgsConstructor
public class DataUtils {
  
  @Getter
  private String root = "";
  
  @Getter
  private boolean usePrettyPrint = false;
  
  @Getter(lazy = true)
  private final Gson gson = createGson();
  
  /**
   * Initializes an instance and sets whether to use JSON Pretty Print or not.
   *
   * @param usePrettyPrint JSON pretty print setting
   */
  public DataUtils(boolean usePrettyPrint) {
    this.usePrettyPrint = usePrettyPrint;
  }
  
  /**
   * Sets the root path (the path to append to file references).
   *
   * @param root the root to append to file paths
   */
  public void setRoot(String root) {
    if (!root.isEmpty() && !root.endsWith("/") && !root.endsWith(File.separator)) {
      this.root = root + File.separator;
    } else {
      this.root = root; 
    }
  }
  
  /**
   * Writes the content to the given file.
   *
   * @param filename the name of the file to write to
   * @param content the content to write
   */
  public void writeToFile(String filename, String content) {
    try {
      Files.write(Paths.get(root + filename), content.getBytes());
    } catch (IOException e) {
      throw new IllegalStateException("Could not write to file '" + filename + "'", e);
    }
  }
  
  /**
   * Reads a file's contents as UTF-8.
   *
   * @param filename the name of the file to read
   * @return the contents of the file
   */
  public static String readFile(String filename) {
    try {
      return new String(Files.readAllBytes(Paths.get(filename)),
                        Charset.forName("UTF-8"));
    } catch (IOException e) {
      throw new IllegalStateException("Could not read file '" + filename + "'", e);
    }
  }

  /**
   * Reads all lines of a file as UTF-8.
   *
   * @param filename the name of the file to read
   * @return the file's contents by line
   */
  public static List<String> readAllLines(String filename) {
    try {
      return Files.readAllLines(Paths.get(filename));
    } catch (IOException e) {
      throw new IllegalStateException("Could not read from file '" + filename + "'", e);
    }
  }
  
  /**
   * Converts an object to its JSON representation.
   *
   * @param o the object to convert
   * @return the generated JSON
   */
  public String toJson(Object o) {
    return getGson().toJson(o);
  }
  
  /**
   * Deserializes JSON to the specified class.
   *
   * @param <T> the result type
   * @param json the JSON text to deserialize
   * @param classOfT the class of the resulting object
   * @return resulting object
   */
  public <T> T fromJson(String json, Class<T> classOfT) {
    return getGson().fromJson(json, classOfT);
  }

  /**
   * Deserializes JSON to the specified type.
   *
   * @param <T> the result type
   * @param json the JSON text to deserialize
   * @param typeOfT the type of the resulting object
   * @return resulting object
   */
  public <T> T fromJson(String json, Type typeOfT) {
    return getGson().fromJson(json, typeOfT);
  }
  
  private Gson createGson() {
    if (usePrettyPrint) {
      return new GsonBuilder().setPrettyPrinting().create();
    }
    return new Gson();
  }

}
